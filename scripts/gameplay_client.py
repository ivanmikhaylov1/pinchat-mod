#!/usr/bin/env python3
"""Exercise a packaged PinChat JAR with native input on an isolated loopback server."""
import argparse
import hashlib
import json
import math
import os
import pathlib
import re
import secrets
import shutil
import signal
import socket
import struct
import subprocess
import time
import urllib.parse
import urllib.request
import zipfile

from release import artifact_name, matrix, properties, ROOT


class Rcon:
    def __init__(self, port, password):
        self.sock = socket.create_connection(("127.0.0.1", port), timeout=5)
        self.counter = 0
        try:
            self.send(3, password)
            while True:
                request, kind, _ = self.receive()
                if request == -1:
                    raise RuntimeError("RCON authentication failed")
                if kind == 2:
                    break
        except BaseException:
            self.sock.close()
            raise

    def read(self, count):
        data = bytearray()
        while len(data) < count:
            part = self.sock.recv(count - len(data))
            if not part:
                raise ConnectionError("RCON socket closed")
            data.extend(part)
        return bytes(data)

    def receive(self):
        size = struct.unpack("<i", self.read(4))[0]
        if not 10 <= size <= 4 * 1024 * 1024:
            raise ValueError(f"Invalid RCON packet length: {size}")
        packet = self.read(size)
        request, kind = struct.unpack("<ii", packet[:8])
        return request, kind, packet[8:-2].decode("utf-8")

    def send(self, kind, text):
        self.counter += 1
        packet = struct.pack("<ii", self.counter, kind) + text.encode() + b"\0\0"
        self.sock.sendall(struct.pack("<i", len(packet)) + packet)

    def command(self, text):
        self.send(2, text)
        request, kind, answer = self.receive()
        if request != self.counter or kind != 0:
            raise RuntimeError("Unexpected RCON response")
        return answer

    def close(self):
        self.sock.close()


def available_port():
    with socket.socket() as sock:
        sock.bind(("127.0.0.1", 0))
        return sock.getsockname()[1]


def download(url, destination, sha1=None):
    if destination.exists() and (not sha1 or hashlib.sha1(destination.read_bytes()).hexdigest() == sha1):
        return
    destination.parent.mkdir(parents=True, exist_ok=True)
    with urllib.request.urlopen(url, timeout=120) as response:
        data = response.read()
    if sha1 and hashlib.sha1(data).hexdigest() != sha1:
        raise ValueError(f"SHA-1 mismatch for {destination.name}")
    destination.write_bytes(data)


def server_jar(version, cache):
    with urllib.request.urlopen("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json", timeout=60) as response:
        manifest = json.load(response)
    entry = next(v for v in manifest["versions"] if v["id"] == version)
    with urllib.request.urlopen(entry["url"], timeout=60) as response:
        details = json.load(response)["downloads"]["server"]
    path = cache / f"server-{version}.jar"
    download(details["url"], path, details["sha1"])
    return path


def install_client(row, game, java):
    import minecraft_launcher_lib as launcher
    version, loader = row["minecraft"], row["loader"]
    manager = launcher.mod_loader.get_mod_loader(loader)
    expected = manager.get_installed_version(version, row["loader_version"])
    if (game / "versions" / expected / f"{expected}.json").exists():
        launcher.install.install_minecraft_version(expected, game)
        return expected
    installer_java = java
    proxy = urllib.parse.urlparse(os.environ.get("HTTPS_PROXY", ""))
    if proxy.hostname:
        # The Java installers do not read HTTPS_PROXY themselves.
        arguments = [f"-Dhttps.proxyHost={proxy.hostname}", f"-Dhttps.proxyPort={proxy.port or 80}",
                     f"-Dhttp.proxyHost={proxy.hostname}", f"-Dhttp.proxyPort={proxy.port or 80}"]
        wrapper = game / "installer-java.py"
        wrapper.write_text("#!/usr/bin/env python3\nimport os, sys\n" +
                           f"os.execv({java!r}, [{java!r}] + {arguments!r} + sys.argv[1:])\n")
        wrapper.chmod(0o755)
        installer_java = str(wrapper)
    if loader != "neoforge":
        return manager.install(
            version, game, loader_version=row["loader_version"], java=installer_java)
    # launcher-lib 8 assumes every NeoForge version starts with Minecraft '1.'.
    # Install the explicitly pinned 26.x installer without that legacy version parser.
    launcher.install.install_minecraft_version(version, game)
    launcher.vanilla_launcher.create_empty_vanilla_launcher_profiles_file(game)
    neo = row["loader_version"]
    installer = game / f"neoforge-{neo}-installer.jar"
    download(f"https://maven.neoforged.net/releases/net/neoforged/neoforge/{neo}/neoforge-{neo}-installer.jar", installer)
    command = [java]
    proxy = urllib.parse.urlparse(os.environ.get("HTTPS_PROXY", ""))
    if proxy.hostname:
        command += [f"-Dhttps.proxyHost={proxy.hostname}", f"-Dhttps.proxyPort={proxy.port or 80}",
                    f"-Dhttp.proxyHost={proxy.hostname}", f"-Dhttp.proxyPort={proxy.port or 80}"]
    with (game / "installer.log").open("w") as log:
        subprocess.run([*command, "-jar", str(installer), "--install-client", str(game)],
                       cwd=game, stdout=log, stderr=subprocess.STDOUT, check=True, timeout=600)
    profile = f"neoforge-{neo}"
    launcher.install.install_minecraft_version(profile, game)
    return profile


def stop(process):
    if process is None:
        return
    try:
        os.killpg(process.pid, signal.SIGTERM)
    except ProcessLookupError:
        return
    try:
        process.wait(timeout=10)
    except subprocess.TimeoutExpired:
        os.killpg(process.pid, signal.SIGKILL)
        process.wait(timeout=10)


class Gameplay:
    USER = "PinChatTest"
    MESSAGE = "Base: X=120 Y=64 Z=-80"

    def __init__(self, row, game, java, jar, timeout):
        self.row, self.game, self.java, self.jar = row, game, java, jar
        self.deadline = time.monotonic() + timeout
        self.client = self.server = self.rcon = None
        self.results = []
        self.window = None
        self.phase = 0

    def check_alive(self):
        if time.monotonic() > self.deadline:
            raise TimeoutError("Gameplay test exceeded its deadline")
        for name, process in (("server", self.server), ("client", self.client)):
            if process is not None and process.poll() is not None:
                raise RuntimeError(f"{name} exited with code {process.returncode}")

    def wait(self, predicate, description, timeout=30):
        until = min(self.deadline, time.monotonic() + timeout)
        while time.monotonic() < until:
            self.check_alive()
            if predicate():
                return
            time.sleep(0.2)
        raise AssertionError(f"Timed out: {description}")

    def passed(self, name):
        self.results.append(name)
        self.write_result("running")
        print(f"PASS: {name}", flush=True)

    def write_result(self, status, error=None):
        (self.game / "result.json").write_text(json.dumps(
            {"target": self.row, "jar": self.jar.name,
             "jar_sha256": hashlib.sha256(self.jar.read_bytes()).hexdigest(),
             "status": status, "passed": self.results, "error": error}, indent=2) + "\n")

    def config(self):
        try:
            return json.loads((self.game / "config/pinchat.json").read_text())
        except (FileNotFoundError, json.JSONDecodeError):
            return None

    def groups(self):
        data = self.config()
        return data["groups"] if data else []

    def xd(self, *args, capture=False):
        self.check_alive()
        result = subprocess.run(["xdotool", *map(str, args)], check=True, text=True,
                                capture_output=capture, timeout=10)
        return result.stdout.strip() if capture else None

    def key(self, key):
        self.xd("keydown", key)
        try:
            time.sleep(0.25)  # GLFW polling needs a held key across at least one client tick.
        finally:
            self.xd("keyup", key)
        time.sleep(0.2)

    def click(self, x, y, button=1):
        # Fixed 1024x768 window and GUI scale 2, configured before startup.
        self.xd("mousemove", "--window", self.window, int(x * 2), int(y * 2))
        self.xd("click", button)
        time.sleep(0.3)

    def screenshot(self, name):
        existing = set((self.game / "screenshots").glob("*.png"))
        self.key("F2")
        def completed_screenshots():
            return [p for p in set((self.game / "screenshots").glob("*.png")) - existing
                    if p.read_bytes().endswith(b"IEND\xaeB`\x82")]
        self.wait(lambda: bool(completed_screenshots()), "screenshot fully saved")
        source = completed_screenshots()[0]
        shutil.copy2(source, self.game / f"{name}.png")

    def position(self):
        answer = self.rcon.command(f"data get entity {self.USER} Pos")
        found = re.search(r"\[(-?[\d.eE+]+)d,\s*(-?[\d.eE+]+)d,\s*(-?[\d.eE+]+)d\]", answer)
        return tuple(map(float, found.groups())) if found else None

    def connect(self):
        self.phase += 1
        log = (self.game / f"client-{self.phase}.log").open("w")
        self.client = subprocess.Popen(self.command, cwd=self.game, stdout=log,
                                       stderr=subprocess.STDOUT, start_new_session=True)
        log.close()
        self.wait(lambda: self.position() is not None, "player joined the real world", timeout=180)
        self.wait(lambda: any(marker in (self.game / f"client-{self.phase}.log").read_text()
                              for marker in ("PinChat Client Setup", "PinChatMod initialized!")),
                  "PinChat initialized", timeout=30)
        self.window = self.xd("search", "--onlyvisible", "--pid", self.client.pid, "--name", "Minecraft", capture=True).splitlines()[0]
        self.xd("windowfocus", "--sync", self.window)
        geometry = self.xd("getwindowgeometry", "--shell", self.window, capture=True)
        dimensions = dict(line.split("=", 1) for line in geometry.splitlines() if "=" in line)
        if (int(dimensions["WIDTH"]), int(dimensions["HEIGHT"])) != (1024, 768):
            raise AssertionError(f"Unexpected window dimensions: {geometry}")
        self.wait(lambda: re.search(r"Loaded \d+ advancements", (self.game / f"client-{self.phase}.log").read_text()),
                  "client finished joining", timeout=60)
        time.sleep(5)  # LoadingScreen must finish before T can be consumed.

    def prepare(self):
        import minecraft_launcher_lib as launcher
        self.game.mkdir(parents=True, exist_ok=False)
        config = self.game / "config"
        config.mkdir()
        (config / "pinchat.json").write_text(json.dumps({
            "maxPinnedMessages": 3, "maxLineWidth": 200, "pinnedX": 100, "pinnedY": 100,
            "pinnedScale": 1.0, "moveableChatEnabled": True, "groups": []}))
        # Share downloaded assets/libraries across local matrix runs; each instance is isolated.
        cache = ROOT / "build/game-cache"
        for name in ("assets", "libraries", "versions"):
            shared = cache / name
            shared.mkdir(parents=True, exist_ok=True)
            (self.game / name).symlink_to(shared, target_is_directory=True)
        print(f"Installing {self.row['loader']} {self.row['loader_version']} / Minecraft {self.row['minecraft']}", flush=True)
        profile = install_client(self.row, self.game, self.java)
        # Explicit version prevents Minecraft migrating modern key names as old numeric codes.
        vanilla = self.row["minecraft"]
        with zipfile.ZipFile(self.game / "versions" / vanilla / f"{vanilla}.jar") as client_jar:
            data_version = json.loads(client_jar.read("version.json"))["world_version"]
        (self.game / "options.txt").write_text(
            f"version:{data_version}\nkey_pinchat.hotkey.openConfig:key.keyboard.f8\nguiScale:2\nlang:en_us\nonboardAccessibility:false\njoinedFirstServer:true\nfullscreen:false\n"
            "pauseOnLostFocus:false\nrenderDistance:3\nsimulationDistance:5\n"
            "chatScale:1.0\nchatLineSpacing:0.0\nmaxFps:60\ntutorialStep:none\nsoundCategory_master:0.0\n")
        mods = self.game / "mods"
        mods.mkdir(exist_ok=True)
        shutil.copy2(self.jar, mods / self.jar.name)
        if self.row["loader"] in ("fabric", "quilt"):
            api = self.row["fabric_api"]
            download(f"https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/{api}/fabric-api-{api}.jar", mods / f"fabric-api-{api}.jar")
        server = self.game / "server"
        server.mkdir()
        port, rcon_port = available_port(), available_port()
        while rcon_port == port:
            rcon_port = available_port()
        password = secrets.token_hex(24)
        (server / "eula.txt").write_text("eula=true\n")
        (server / "server.properties").write_text(
            f"server-ip=127.0.0.1\nserver-port={port}\nonline-mode=false\n"
            f"enable-rcon=true\nrcon.port={rcon_port}\nrcon.password={password}\n"
            "level-type=minecraft:flat\ngenerate-structures=false\ngamemode=creative\n"
            "difficulty=peaceful\nspawn-protection=0\nview-distance=3\nsimulation-distance=3\nmax-players=1\n")
        jar = server_jar(self.row["minecraft"], ROOT / "build/game-cache")
        log = (self.game / "server.log").open("w")
        self.server = subprocess.Popen([self.java, "-Xmx1G", "-jar", str(jar), "nogui"],
                                       cwd=server, stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
        log.close()
        self.wait(lambda: 'Done (' in (self.game / "server.log").read_text(), "server ready", timeout=180)
        def connect_rcon():
            try:
                self.rcon = Rcon(rcon_port, password)
                return True
            except (ConnectionRefusedError, TimeoutError):
                return False
        self.wait(connect_rcon, "RCON listener ready", timeout=30)
        self.command = launcher.command.get_minecraft_command(profile, self.game, {
            "username": self.USER, "uuid": "00000000000000000000000000000001", "token": "0",
            "executablePath": self.java, "gameDirectory": str(self.game),
            "jvmArguments": ["-Xmx1G"], "launcherName": "PinChat Gameplay Tests", "launcherVersion": "1"})
        self.command += ["--width", "1024", "--height", "768", "--quickPlayMultiplayer", f"127.0.0.1:{port}"]

    def chat_message(self, text):
        self.rcon.command(f"tellraw {self.USER} " + json.dumps({"text": text}))
        self.wait(lambda: f"[CHAT] {text}" in (self.game / f"client-{self.phase}.log").read_text(),
                  f"chat message arrived: {text}")
        self.click(20, 340, 3)  # height/2 - 44: newest chat row

    def drag(self, x, y, dx, dy):
        self.xd("mousemove", "--window", self.window, int(x * 2), int(y * 2))
        self.xd("mousedown", "1")
        try:
            time.sleep(0.2)
            for step in range(1, 11):
                self.xd("mousemove", "--window", self.window,
                        int((x + dx * step / 10) * 2), int((y + dy * step / 10) * 2))
                time.sleep(0.08)
            time.sleep(0.3)  # Let the final cursor position render while the button is held.
        finally:
            self.xd("mouseup", "1")
        time.sleep(0.3)

    def scenario(self):
        self.connect()
        self.passed("world entry and packaged mod initialization")
        self.key("t")
        self.chat_message(self.MESSAGE)
        self.wait(lambda: len(self.groups()) == 1 and self.groups()[0]["messages"] == [self.MESSAGE], "right-click pins")
        self.click(20, 340, 3)
        self.wait(lambda: not self.groups()[0]["messages"], "second click unpins")
        self.click(20, 340, 3)
        self.wait(lambda: self.groups()[0]["messages"] == [self.MESSAGE], "pin again")
        self.passed("pin, unpin and pin again through the chat mixin")
        self.xd("keydown", "Shift_L")
        try:
            self.click(20, 340, 3)
        finally:
            self.xd("keyup", "Shift_L")
        self.wait(lambda: len(self.groups()) == 2 and self.groups()[1]["messages"] == [self.MESSAGE], "Shift creates independent group")
        # Separate the new group from the default group using native dragging.
        self.drag(104, 102, 170, 80)
        self.wait(lambda: self.groups()[1]["x"] >= 260 and self.groups()[1]["y"] >= 170, "drag position persisted")
        self.passed("independent group creation and native dragging")
        self.click(104, 94)
        self.wait(lambda: self.groups()[0]["isCollapsed"], "collapse")
        self.click(104, 94)
        self.wait(lambda: not self.groups()[0]["isCollapsed"], "expand")
        # Default Minecraft font: measured by the existing client GameTest screenshot.
        self.click(189, 94)
        self.click(256, 182)
        self.key("ctrl+a")
        self.xd("type", "--clearmodifiers", "--delay", "40", "Coordinates")
        self.click(201, 212)
        self.wait(lambda: self.groups()[0]["name"] == "Coordinates", "rename screen saved name")
        self.passed("collapse, expand and rename through screens")
        # Message uses vanilla font width 122 GUI pixels; handle is at its lower-right corner.
        self.drag(219, 107, 35, 35)
        self.wait(lambda: self.groups()[0]["scale"] > 1.2, "resize persists scale")
        self.passed("native resize handle")
        self.screenshot("groups")
        self.key("Escape")
        self.key("u")
        self.wait(lambda: "MoveableChatScreen: init() completed" in
                  (self.game / f"client-{self.phase}.log").read_text(), "special chat actually opened")
        before = self.position()
        self.xd("keydown", "w")
        try:
            time.sleep(1.5)
        finally:
            self.xd("keyup", "w")
        after = self.position()
        if before is None or after is None or math.hypot(after[0] - before[0], after[2] - before[2]) < 0.5:
            raise AssertionError("Player did not move with special chat open")
        self.screenshot("moveable-chat")
        self.key("Escape")
        self.wait(lambda: "MoveableChatScreen: Restoring original input" in
                  (self.game / f"client-{self.phase}.log").read_text(), "original input restored")
        self.passed("movement with special chat open (server coordinates)")
        # Reopen U and close with U as well, then check the switch and its persistence.
        self.key("u")
        time.sleep(0.6)
        restored_count = (self.game / f"client-{self.phase}.log").read_text().count(
            "MoveableChatScreen: Restoring original input")
        self.key("u")
        self.wait(lambda: (self.game / f"client-{self.phase}.log").read_text().count(
                  "MoveableChatScreen: Restoring original input") > restored_count, "U closes special chat")
        self.key("F8")
        self.click(256, 192)
        self.wait(lambda: not self.config()["moveableChatEnabled"], "settings switch saved")
        self.click(256, 222)
        self.passed("U close and rebound F8 settings toggle")
        persisted = self.config()
        stop(self.client)
        self.client = None
        self.wait(lambda: self.position() is None, "first client disconnected")
        self.connect()
        # Persisted file alone is insufficient: interact with the restored groups.
        self.key("t")
        self.click(103, 103, 3)
        self.wait(lambda: not self.groups()[0]["messages"], "restored pinned row responds after restart")
        self.wait(lambda: self.groups()[1] == persisted["groups"][1], "other restored group remains unchanged")
        self.passed("real client restart restores groups and display settings")
        for message in ("First", "Second", "Third", "Overflow"):
            self.chat_message(message)
        self.wait(lambda: self.groups()[0]["messages"] == ["First", "Second", "Third"], "per-group limit")
        self.passed("per-group message limit")
        self.key("Escape")
        self.key("u")
        # With U disabled, T opens normal chat. W must then type, not move the player.
        self.key("t")
        before = self.position()
        self.xd("keydown", "w")
        try:
            time.sleep(1)
        finally:
            self.xd("keyup", "w")
        after = self.position()
        if math.hypot(after[0] - before[0], after[2] - before[2]) > 0.1:
            raise AssertionError("Disabled special chat still allows movement")
        self.key("Escape")
        self.passed("disabled setting survives restart and normal chat blocks movement")
        self.write_result("passed")

    def run(self):
        try:
            self.prepare()
            self.scenario()
        except Exception as error:
            if self.game.exists():
                self.write_result("failed", str(error))
                if self.client is not None and self.client.poll() is None and self.window:
                    try:
                        self.screenshot("failure")
                    except Exception:
                        pass  # Keep the original assertion; screenshot is diagnostic only.
            raise
        finally:
            stop(self.client)
            if self.rcon:
                self.rcon.close()
            stop(self.server)
            # Never upload the RCON password or unnecessary server worlds.
            secret_file = self.game / "server/server.properties"
            if secret_file.exists():
                secret_file.unlink()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--loader", required=True, choices=("fabric", "quilt", "forge", "neoforge"))
    parser.add_argument("--minecraft-version", required=True)
    parser.add_argument("--jar", type=pathlib.Path)
    parser.add_argument("--java-home", type=pathlib.Path)
    parser.add_argument("--timeout", type=int, default=1200)
    args = parser.parse_args()
    row = next((r for r in matrix() if r["loader"] == args.loader and r["minecraft"] == args.minecraft_version), None)
    if row is None:
        parser.error("Unsupported target; see config/targets.json")
    if not os.environ.get("DISPLAY") or not shutil.which("xdotool"):
        parser.error("A display and xdotool are required; use xvfb-run on Linux")
    java_home = args.java_home or os.environ.get(f"JAVA_{row['java']}_HOME") or os.environ.get(f"JAVA_HOME_{row['java']}_X64") or os.environ.get("JAVA_HOME")
    java = str(pathlib.Path(java_home) / "bin/java") if java_home else shutil.which("java")
    version = properties(ROOT / "gradle.properties")["mod_version"]
    jar = (args.jar or ROOT / "dist" / artifact_name(version, row)).resolve()
    if not jar.is_file():
        parser.error(f"Packaged JAR not found: {jar}")
    result = subprocess.run([java, "-version"], capture_output=True, text=True, check=True)
    if not re.search(rf'version "{row["java"]}(?:\.|\")', result.stderr):
        parser.error(f"Java {row['java']} is required")
    game = ROOT / "build/gameplay" / f"{args.loader}-{args.minecraft_version}"
    if game.exists():
        archive = game.parent / "archive" / f"{game.name}-{time.time_ns()}"
        archive.parent.mkdir(parents=True, exist_ok=True)
        game.rename(archive)
        print(f"Previous test diagnostics preserved in {archive}", flush=True)
    Gameplay(row, game, java, jar, args.timeout).run()
    print(f"Gameplay passed: {args.loader} / Minecraft {args.minecraft_version}", flush=True)


if __name__ == "__main__":
    main()
