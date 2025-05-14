import re
import sys
import argparse


def convert_format(input_data, source_id_order):
    lines = input_data.strip().split("\n")

    # Parse each line into a dictionary with id and a list of (sourceId: parentId) pairs
    data = []
    for line in lines:
        parts = line.split("\t")
        if len(parts) != 2:
            continue  # Skip malformed lines

        line_id = int(parts[0])
        pairs_str = parts[1]

        # Extract (sourceId: parentId) pairs
        pairs = re.findall(r"\((\d+):(-?\d+)\)", pairs_str)
        source_id_to_parent_id = {
            int(source_id): int(parent_id) for source_id, parent_id in pairs
        }

        # Create a list of parentIds ordered by source_id_order
        ordered_parent_ids = [
            str(source_id_to_parent_id.get(source_id, ""))
            for source_id in source_id_order
        ]

        data.append((line_id, ordered_parent_ids))

    # Sort data by id
    data.sort(key=lambda x: x[0])

    # Prepare the output
    output = []
    for line_id, parent_ids in data:
        output.append(f"{line_id}\t" + " ".join(parent_ids))

    return "\n".join(output)


def main():
    parser = argparse.ArgumentParser(
        description="Convert input format to desired output format."
    )
    parser.add_argument("input_file", help="Path to the input file")
    parser.add_argument("output_file", help="Path for the converted output file")
    parser.add_argument(
        "--source-id-order",
        required=True,
        type=int,
        nargs="+",
        help="Order of sourceIds",
    )
    args = parser.parse_args()

    print(args.source_id_order)

    with open(args.input_file, "r") as file:
        input_data = file.read()
        converted_output = convert_format(input_data, args.source_id_order)
        with open(args.output_file, "w+") as out_file:
            out_file.write(converted_output)


if __name__ == "__main__":
    main()
