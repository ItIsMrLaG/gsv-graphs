import argparse
import re

parser = argparse.ArgumentParser(
        description="Convert input format to desired output format."
    )
parser.add_argument("input_file", help="Path to the input file")

args = parser.parse_args()


# Read the file
with open(args.input_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Regex to extract the times (note: using comma as decimal separator)
comm_match = re.search(r'Communication Time\s*:\s*([\d,]+)\s*seconds', content)
comp_match = re.search(r'Total Computational Time\s*:\s*([\d,]+)\s*seconds', content)

# Convert to float (replace comma with dot)
communication_time = comm_match.group(1)
computational_time = comp_match.group(1)

print(f"{communication_time}; {computational_time}")