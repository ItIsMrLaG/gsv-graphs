#!/usr/bin/env python3

import sys
from collections import defaultdict

def convert_format(input_file=None):
    """
    Reads data from input_file (or stdin), groups by the first column,
    collects unique second column values, and prints in the desired format.
    """
    # Use defaultdict(set) to automatically create a set for a new key
    neighbors = defaultdict(set)

    # Read input data
    if input_file:
        try:
            f = open(input_file, 'r')
        except FileNotFoundError:
            print(f"Error: Input file '{input_file}' not found.", file=sys.stderr)
            sys.exit(1)
    else:
        f = sys.stdin

    for line in f:
        line = line.strip() # Remove leading/trailing whitespace and newline
        if not line or line.startswith('#'): # Skip empty lines and comments
            continue

        parts = line.split() # Split by whitespace

        if len(parts) >= 2:
            v1 = parts[0]
            v2 = parts[1]
            neighbors[v1].add(v2) # Add v2 to the set for v1 (sets handle uniqueness)
        else:
            print(f"Warning: Skipping line with unexpected format: '{line}'", file=sys.stderr)

    if input_file:
        f.close()

    # Process and print grouped data
    # Sort v1 keys for consistent output order
    for v1 in sorted(neighbors.keys()):
        nbr_set = neighbors[v1]
        nbr_count = len(nbr_set)
        # Sort neighbors for consistent output order (optional)
        sorted_nbrs = sorted(list(nbr_set))

        # Format and print the output line
        # Use ' '.join() to create a space-separated string of neighbors
        print(f"{v1}\t{nbr_count} {' '.join(sorted_nbrs)}")

if __name__ == "__main__":
    if len(sys.argv) > 2:
        print("Usage: python script_name.py [input_file]", file=sys.stderr)
        sys.exit(1)

    if len(sys.argv) == 2:
        convert_format(sys.argv[1]) # Read from specified file
    else:
        convert_format() # Read from stdin