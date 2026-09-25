#!/usr/bin/env python3
"""Install Fabric or Quilt, then smoke the packaged PinChat JAR."""
import argparse
import os
import pathlib
import signal
import shutil
import subprocess
import sys
import threading
import time
import urllib.request

import minecraft_launcher_lib as launcher

parser = argparse.ArgumentParser()
parser.add_argument('--loader', choices=('fabric', 'quilt'), default='quilt')
parser.add_argument('--minecraft-version', default='26.1')
parser.add_argument('--artifact-version', default='26.1')
parser.add_argument('--fabric-api-version')
args = parser.parse_args()
root = pathlib.Path(__file__).resolve().parents[1]
props = dict(line.split('=', 1) for line in (root / 'fabric-mc26/gradle.properties').read_text().splitlines() if '=' in line and not line.startswith('#'))
game = root / 'build/client26-smoke'
mods = game / 'mods'
mods.mkdir(parents=True, exist_ok=True)
for old_mod in mods.glob('*.jar'):
    old_mod.unlink()
version = args.minecraft_version
loader = '0.30.1' if args.loader == 'quilt' else props['loader_version']
profile = f'{args.loader}-loader-{loader}-{version}'
java = os.environ.get('JAVA_25_HOME') or os.environ.get('JAVA_HOME_25_X64', '')
java_exec = str(pathlib.Path(java) / 'bin/java') if java else shutil.which('java')
if not java_exec:
    raise RuntimeError('Java 25 not found; set JAVA_25_HOME')

if args.loader == 'quilt':
    launcher.quilt.install_quilt(version, game, loader_version=loader, java=java_exec)
else:
    launcher.fabric.install_fabric(version, game, loader_version=loader, java=java_exec)
mod_jar = root / 'fabric-mc26/build/libs' / f"pinchat-mod-fabric-mc{args.artifact_version}-{props['mod_version']}.jar"
shutil.copy2(mod_jar, mods / mod_jar.name)
api = args.fabric_api_version or props['fabric_api_version']
api_jar = mods / f'fabric-api-{api}.jar'
if not api_jar.exists():
    url = f'https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/{api}/fabric-api-{api}.jar'
    urllib.request.urlretrieve(url, api_jar)

options = {
    'username': 'PinChatSmoke',
    'uuid': '00000000000000000000000000000001',
    'token': '0',
    'executablePath': java_exec,
    'gameDirectory': str(game),
    'launcherName': 'PinChat CI smoke',
    'launcherVersion': '1',
}
command = launcher.command.get_minecraft_command(profile, game, options)
if os.environ.get('DISPLAY') and not shutil.which('xdotool'):
    raise RuntimeError('DISPLAY is set but xdotool is unavailable')
process = subprocess.Popen(command, cwd=game, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                           text=True, bufsize=1, start_new_session=True)
loader_confirmed = threading.Event()
setup = threading.Event()
render = threading.Event()
error = threading.Event()
error_text = ['']

def read_output():
    for line in process.stdout:
        print(line, end='', flush=True)
        if 'Loading Minecraft ' + version + ' with ' + args.loader.title() + ' Loader' in line:
            loader_confirmed.set()
            print(f'{args.loader.title()} loader confirmed', flush=True)
        if 'PinChat Client Setup' in line:
            setup.set()
        if 'textures/atlas/gui.png-atlas' in line and 'Render thread' in line:
            render.set()
        if 'Game crashed!' in line or '/FATAL]' in line or 'Failed to find a primary monitor' in line:
            error_text[0] = line.strip()
            error.set()

threading.Thread(target=read_output, daemon=True).start()
start = time.monotonic()
stable = None
try:
    while time.monotonic() - start < 900:
        if error.is_set():
            raise RuntimeError(error_text[0])
        if process.poll() is not None:
            raise RuntimeError(f'{args.loader} client exited with {process.returncode}')
        window = not os.environ.get('DISPLAY') or subprocess.run(
            ['xdotool', 'search', '--onlyvisible', '--name', 'Minecraft'],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0
        if loader_confirmed.is_set() and setup.is_set() and render.is_set() and window:
            stable = stable or time.monotonic()
            if time.monotonic() - stable >= 15:
                print(f'{args.loader.title()} {version} smoke passed: PinChat entrypoint, GUI atlas and stable client', flush=True)
                sys.exit(0)
        else:
            stable = None
        time.sleep(2)
    raise TimeoutError(f'{args.loader} client did not reach render-ready state in 15 minutes')
finally:
    try:
        os.killpg(process.pid, signal.SIGTERM)
    except ProcessLookupError:
        pass
    try:
        process.wait(timeout=10)
    except subprocess.TimeoutExpired:
        os.killpg(process.pid, signal.SIGKILL)
        process.wait()
