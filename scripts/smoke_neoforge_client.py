#!/usr/bin/env python3
"""Launch the NeoForge client under Xvfb and wait for PinChat plus a live window."""

import os
import argparse
import pathlib
import signal
import shutil
import subprocess
import sys
import threading
import time


root = pathlib.Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument("--target", choices=("1.21.11", "26.1", "26.2"), default="1.21.11")
parser.add_argument("--require-window", action="store_true")
parser.add_argument("--neo-version", help="Override NeoForge version for hotfix compatibility smoke")
args = parser.parse_args()
target = args.target
if args.require_window and (not os.environ.get("DISPLAY") or not shutil.which("xdotool")):
    parser.error("--require-window requires DISPLAY and xdotool")
if target in ("26.1", "26.2"):
    command = [str(root / "mc26_1/gradlew"), "-p", str(root / "mc26_1"), "runClient"]
    if target == "26.2":
        command.extend(["-Pminecraft_version=26.2", "-Pneo_version=26.2.0.88", "-Ppack_format=88"])
    if args.neo_version:
        command.append(f"-Pneo_version={args.neo_version}")
else:
    command = [str(root / "gradlew"), ":neoforge:runClient"]
deadline = time.monotonic() + 900
process = subprocess.Popen(
    [*command, "--no-daemon", "--console=plain"],
    cwd=root,
    stdout=subprocess.PIPE,
    stderr=subprocess.STDOUT,
    text=True,
    bufsize=1,
    start_new_session=True,
)
seen_client_setup = threading.Event()
seen_render_ready = threading.Event()
startup_error = threading.Event()
error_line = [""]
require_window = args.require_window or bool(os.environ.get("DISPLAY") and shutil.which("xdotool"))


def read_output():
    for line in process.stdout:
        print(line, end="", flush=True)
        if "PinChat Client Setup" in line:
            seen_client_setup.set()
        if "Render thread" in line and "textures/atlas/gui.png-atlas" in line:
            seen_render_ready.set()
        if "Failed to find a primary monitor" in line or "Game crashed!" in line or "/FATAL]" in line:
            error_line[0] = line.strip()
            startup_error.set()


threading.Thread(target=read_output, daemon=True).start()
stable_since = None
try:
    while time.monotonic() < deadline:
        if startup_error.is_set():
            raise RuntimeError(f"NeoForge client startup error: {error_line[0]}")
        if process.poll() is not None:
            raise RuntimeError(f"NeoForge client exited early with code {process.returncode}")
        window = True
        if require_window:
            window = subprocess.run(
                ["xdotool", "search", "--onlyvisible", "--name", "Minecraft"],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
                check=False,
            ).returncode == 0
        if window and seen_client_setup.is_set() and seen_render_ready.is_set():
            stable_since = stable_since or time.monotonic()
            if time.monotonic() - stable_since >= 15:
                print(f"NeoForge {target} client smoke test: PinChat event and render-ready checkpoint observed"
                      + (" with a stable Minecraft window" if require_window else " with a live client process"))
                sys.exit(0)
        else:
            stable_since = None
        time.sleep(2)
    raise TimeoutError("NeoForge client did not reach PinChat setup and stable render readiness within 15 minutes")
finally:
    try:
        os.killpg(process.pid, signal.SIGTERM)
    except ProcessLookupError:
        pass
    try:
        process.wait(timeout=10)
    except subprocess.TimeoutExpired:
        try:
            os.killpg(process.pid, signal.SIGKILL)
        except ProcessLookupError:
            pass
        process.wait()
