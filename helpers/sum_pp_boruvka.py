import sys

def sum_third_column():
    total = 0
    for line_number, line in enumerate(sys.stdin, 1):
        parts = line.strip().split()
        # In case the line doesn't have at least 3 columns:
        if len(parts) >= 3:
            try:
                value = int(parts[2])
                total += value
            except ValueError:
                print(f"Warning: Line {line_number} has a non-numeric third column: '{parts[2]}'")
        else:
            print(f"Warning: Line {line_number} does not have 3 columns: '{line.strip()}'")
    print(f"Total sum of third column: {total}")

if __name__ == "__main__":
    sum_third_column()
