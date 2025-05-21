import sys
import os
import re

def parse_metric_file(file_path):
    superstep_time = 0.0
    network_time = 0.0
    with open(file_path, 'r') as file:
        lines = file.readlines()
    for i, line in enumerate(lines):
        if 'superstep time' in line and 'mean:' in lines[i+1]:
            match = re.search(r'mean:\s+([\d\.]+)', lines[i+1])
            if match:
                superstep_time += float(match.group(1))
        if 'network communication time' in line and 'mean:' in lines[i+1]:
            match = re.search(r'mean:\s+([\d\.]+)', lines[i+1])
            if match:
                network_time += float(match.group(1))
    return superstep_time, network_time

def main():
    if len(sys.argv) != 2:
        print("Usage: python metrics_summary.py <path_to_directory>")
        sys.exit(1)

    directory = sys.argv[1]
    if not os.path.isdir(directory):
        print(f"Error: {directory} is not a valid directory.")
        sys.exit(1)

    total_superstep_time = 0.0
    total_network_time = 0.0

    for filename in os.listdir(directory):
        if filename.startswith("superstep_") and filename.endswith(".metrics"):
            file_path = os.path.join(directory, filename)
            s_time, n_time = parse_metric_file(file_path)
            total_superstep_time += s_time
            total_network_time += n_time

    print(f"network_ms,superstep_ms")
    print(f"{total_network_time:.4f},{total_superstep_time:.4f}")

if __name__ == "__main__":
    main()

