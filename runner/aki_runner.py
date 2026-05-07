import subprocess
import threading
import sys
import argparse
import time
import json
import re

def run_on_device(device_id, args):
    extra = " ".join([f"-e {k} {v}" for k, v in args.items()])
    package_test = "com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner"
    class_test = "com.aki.akiwarmup.AkiFrameworkTest"
    
    cmd = f"adb -s {device_id} shell am instrument -w -r {extra} -e class {class_test} {package_test}"
    
    print(f"[{device_id}] Starting session with args: {args}")
    
    # Start logcat monitoring in a separate thread or just read from the process
    process = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    
    report_started = False
    report_content = []
    
    for line in process.stdout:
        # Check for real-time action logs
        if "AKI_FRAMEWORK: ACTION:" in line:
            print(f"[{device_id}] {line.split('ACTION: ')[1].strip()}")
            
        # Check for report boundaries
        if "SESSION_COMPLETE_REPORT_START" in line:
            report_started = True
            continue
        if "SESSION_COMPLETE_REPORT_END" in line:
            report_started = False
            save_report(device_id, "".join(report_content))
            continue
            
        if report_started:
            # Strip logcat metadata if necessary, but usually -r in instrument or specific tags help
            # If we use Log.i, the report lines will have logcat prefixes. 
            # We can use a regex to extract the actual message content.
            match = re.search(r'I AKI_FRAMEWORK: (.*)', line)
            if match:
                report_content.append(match.group(1))
            else:
                # If the line was wrapped or something, try to just take it if it looks like JSON
                report_content.append(line.strip())

    process.wait()
    print(f"[{device_id}] Session finished.")

def save_report(device_id, report_json):
    filename = f"report_{device_id}_{int(time.time())}.json"
    try:
        data = json.loads(report_json)
        with open(filename, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=4, ensureInstance=False)
        print(f"[{device_id}] Report saved to {filename}")
    except Exception as e:
        print(f"[{device_id}] Failed to save report: {e}")
        # Save raw if JSON failed
        with open(filename + ".raw", "w") as f:
            f.write(report_json)

def get_devices():
    out = subprocess.check_output("adb devices", shell=True, text=True)
    return [line.split()[0] for line in out.strip().splitlines()[1:] if "device" in line and "offline" not in line]

def main():
    parser = argparse.ArgumentParser(description="AkiFramework PC Runner")
    parser.add_argument("--scene", default="tiktok_warmup", help="Name of the scene to run")
    parser.add_argument("--duration", default="30", help="Duration in minutes")
    parser.add_argument("--likeRate", default="0.15", help="Probability of liking a video")
    parser.add_argument("--keywords", default="funny,cooking,travel", help="Comma-separated search keywords")
    parser.add_argument("--device", default=None, help="Target device ID (optional)")
    
    args = parser.parse_args()
    
    config = {
        "scene": args.scene,
        "duration": args.duration,
        "likeRate": args.likeRate,
        "keywords": args.keywords
    }
    
    devices = [args.device] if args.device else get_devices()
    
    if not devices:
        print("No devices found.")
        return
        
    print(f"Dispatched to {len(devices)} device(s): {devices}")
    
    threads = []
    for d in devices:
        t = threading.Thread(target=run_on_device, args=(d, config))
        t.start()
        threads.append(t)
        
    for t in threads:
        t.join()
        
    print("All device sessions completed.")

if __name__ == "__main__":
    main()
