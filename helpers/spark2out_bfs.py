import re
import argparse


def rotate_output(input_data):
    lines = input_data.strip().split("\n")

    # Parse each line into a list of values
    data = []
    for line in lines:
        match = re.match(r"\((\d+),(.*)\)", line)
        if not match:
            continue  # Skip malformed lines

        line_id = int(match.group(1))
        values_str = match.group(2)
        values = list(map(int, values_str.split()))

        data.append(values)

    # Determine the maximum number of values to ensure consistent output columns
    max_values = max(len(values) for values in data)

    # Prepare the output
    output = []
    for idx in range(max_values):
        output.append(f"{idx}\t")
        for values in data:
            output.append(f"{values[idx]} ")
        output.append(f"\n")

    return "".join(output)


def main():
    parser = argparse.ArgumentParser(
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("input_file", help="Path to the input file")
    parser.add_argument("output_file", help="Path for the converted output file")

    args = parser.parse_args()

    # Read input file
    with open(args.input_file, "r") as file:
        input_data = file.read()

    # Convert the input data
    rotated_output = rotate_output(input_data)

    # Write output to file
    with open(args.output_file, "w") as file:
        file.write(rotated_output)


if __name__ == "__main__":
    main()
