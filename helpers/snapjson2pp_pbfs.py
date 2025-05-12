import sys
import argparse
import json


def convert_json_to_adjlist(
      input_filename, 
      output_filename
      ):
    """
    Reads a JSON Adjacency List file (with unweighted edges)
    and converts it to a 'vertex_id \t num_neighbours v1 v2 ...' 
    format.

     Args:
       input_filename (str): Path to the JSON input file.
       output_filename (str): Path for the resulting output file.
       
    Raises:
        FileNotFoundError: If input_file cannot be opened.
        json.JSONDecodeError: If input_file is not valid JSON.
        TypeError: If JSON is not a Dictionary.
    """

    graph_data = {}
    all_nodes = set() # Use a set to track ALL nodes that should be in the graph

    print(f"--> Reading {input_filename}...")
     
    try:
        with open(input_filename, 'r') as fin:
            graph_data = json.load(fin)
    except FileNotFoundError:
         print(f"ERROR: Input file not found: {input_filename}", file=sys.stderr)
         raise
    except json.JSONDecodeError as e:
         print(f"ERROR: Could not parse JSON file: {e}", file=sys.stderr)
         raise
         
    if not isinstance(graph_data, dict):
         raise TypeError(f"Unsupported JSON structure: Top level must be a Dictionary, but found {type(graph_data)}.")
         
    # --- Collect all nodes mentioned, both sources (keys) and targets (values) ---
    for source_node, targets in graph_data.items():
        all_nodes.add(source_node) # Add the key (source)
        if not isinstance(targets, list):
             print(f"WARNING: Value for source node '{source_node}' is not a List. Ignoring. Value={targets}", file=sys.stderr)
             continue
        for target_node in targets:
             # Add target, ensuring it's a string for consistency with keys
             all_nodes.add(str(target_node)) 
             
    print(f"    Graph processing complete. Found {len(all_nodes)} unique nodes.")
    print(f"--> Writing to {output_filename}...")

    # --- Write Output ---
    # Sort nodes for deterministic output
    # Try to sort numerically if possible, otherwise alphabetically
    try:
       # Use the integer value for sorting if possible
       sorted_nodes = sorted(list(all_nodes), key=int)
       print("INFO: Sorting nodes numerically.", file=sys.stderr)
    except ValueError:
       # Fallback to standard string sort if IDs are not all integers
       sorted_nodes = sorted(list(all_nodes))
       print("INFO: Sorting nodes alphabetically (some IDs may not be integers).", file=sys.stderr)
       
    lines_written = 0
    with open(output_filename, 'w') as fout:
       # Iterate over ALL nodes we found, not just the keys,
       # to ensure we include lines for nodes with 0 out-degree.
       for node_id in sorted_nodes:
           
            # Use .get() to safely get [] if node_id was a target but not a source
            neighbours_list = graph_data.get(node_id, []) 
            num_neighbours = len(neighbours_list)

            # Create "v1 v2 ..." string using the default weight
            neighbours_str = " ".join([
                f"{v}" for v in neighbours_list
                ])
            
            if neighbours_str:
              fout.write(f"{node_id}\t{num_neighbours} {neighbours_str}\n")
            else:
               # Handles num_neighbours = 0
              fout.write(f"{node_id}\t{num_neighbours}\n") 
              
            lines_written +=1
            
    print(f"--> Done. Wrote {lines_written} lines.")

if __name__ == "__main__":
    
    parser = argparse.ArgumentParser(
        description="Convert JSON Adjacency List (unweighted) to 'ID \\t COUNT N1 D1 N2 D2...' format.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
         )
    parser.add_argument("input_file", 
                         help="Path to the input JSON file. Format: { \"src\": [tgt1, tgt2,..] }")
    parser.add_argument("output_file", 
                         help="Path for the converted output file")
                         
    args = parser.parse_args()

    try:
       convert_json_to_adjlist(
           args.input_file, 
           args.output_file,
           )
       sys.exit(0)
       
    except Exception as e:
       print(f"\nSCRIPT FAILED: {e}", file=sys.stderr)
       sys.exit(1)