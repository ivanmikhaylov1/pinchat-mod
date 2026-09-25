#!/usr/bin/env python3
"""Launch the NeoForge client under Xvfb and wait for PinChat plus a live window."""

import os
import pathlib
import signal
import subprocess
import sys
import threading
import time


root = pathlib.Path(__file__).resolve().parents[1]
deadline = time.monotonic() + 900
process = subprocess.Popen(
    [str(root / "gradlew"), ":neoforge:runClient", "--no-daemon", "--console=plain"],
    cwd=root,
    stdout=subprocess.PIPE,
    stderr=subprocess.STDOUT,
    text=True,
    bufsize=1,
    start_new_session=True,
)
seen_client_setup = threading.Event()


def read_output():
    for line in process.stdout:
        print(line, end="", flush=True)
        if "PinChat Client Setup" in line:
            seen_client_setup.set()


threading.Thread(target=read_output, daemon=True).start()
stable_since = None
try:
    while time.monotonic() < deadline:
        if process.poll() is not None:
            raise RuntimeError(f"NeoForge client exited early with code {process.returncode}")
        window = subprocess.run(
            ["xdotool", "search", "--onlyvisible", "--name", "Minecraft"],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            check=False,
        ).returncode == 0
        if window and seen_client_setup.is_set():
            stable_since = stable_since or time.monotonic()
            if time.monotonic() - stable_since >= 15:
                print("NeoForge client smoke test: PinChat client event and stable Minecraft window observed")
                sys.exit(0)
        else:
            stable_since = None
        time.sleep(2)
    raise TimeoutError("NeoForge client did not reach PinChat setup and a stable Minecraft window within 15 minutes")
finally:
    os.killpg(process.pid, signal.SIGTERM)
    try:
        process.wait(timeout=10)
    except subprocess.TimeoutExpired:
        os.killpg(process.pid, signal.SIGKILL)
        process.wait()
