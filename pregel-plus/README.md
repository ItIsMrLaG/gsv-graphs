# ⚡️ Mastering Large-Scale Graphs: High-Performance Algorithms with Pregel+ ⚡️

Greetings, Architect of Data! Prepare to command and conquer complex graph challenges. This project equips you with potent implementations of Borůvka's and Multi-source BFS, engineered to run on the formidable PregelPlus framework. This is your gateway to unlocking transformative insights from massive datasets.

## 🏗️ Building the Engine: Core Dependencies

To harness the full power of these algorithms, ensure your computational environment is primed with these foundational components:

1.  **PregelPlus:** The high-quanta engine driving our graph computations.
    *   Source: [https://github.com/yaobaiwei/PregelPlus](https://github.com/yaobaiwei/PregelPlus)
    *   **Essential Underpinnings (Dependencies of PregelPlus):**
        *   **Hadoop:** The bedrock for distributed data processing.
        *   **Java:** The ubiquitous language powering robust systems.
        *   **MPICH:** The conduit for high-speed inter-process communication.

    👉 **For comprehensive installation directives and dependency details for PregelPlus, consult the official scrolls:** [https://www.cse.cuhk.edu.hk/pregelplus/documentation.html](https://www.cse.cuhk.edu.hk/pregelplus/documentation.html)

## ⚙️ Fine-Tuning the Machine: Environment Configuration

Precision is key. Calibrate your system by defining these critical environment variables:

*   `$HADOOP_HOME`: Specifies the root directory of your Hadoop installation.
    *   *Example:* `export HADOOP_HOME=/usr/local/hadoop-2.10-2`
*   `$PP_HOME`: Designates the root directory of your PregelPlus installation.
    *   *Example:* `export PP_HOME=/usr/local/pregelplus`
*   `$PROC_NUM`: The number of processor cores to dedicate to the computation. Wield this power wisely.
    *   *Example:* `export PROC_NUM=8`

**Strategic Recommendation:** Embed these export commands within your shell's configuration file (e.g., `.bashrc`, `.zshrc`) for persistent availability across terminal sessions.

## 🚀 Ignite the Analysis: Executing the Algorithms

The stage is set. The instruments are tuned. It's time to unleash the computational might:

1.  **Navigate to the PregelPlus command center (if you haven't already):**
    ```bash
    cd pregel-plus 
    ```

2.  **Invoke the `run.sh` orchestrator with the specified parameters:**
    ```bash
    ./run.sh <ALGORITHM_ID> /path/to/your/graph_data_file [OPTIONAL_MFBFS_START_NODES]
    ```

    **Command Blueprint:**

    *   `./run.sh`: The master script initiating the execution pipeline.
    *   `<ALGORITHM_ID>`: Select your chosen analytical instrument:
        *   `boruvka`: To deploy Borůvka's algorithm (e.g., for Minimum Spanning Trees).
        *   `mfbfs`: To launch a Multi-source Breadth-First Search.
    *   `/path/to/your/graph_data_file`: The fully qualified or relative path to your input graph file.
        *   Supported formats typically include **SNAP** and **DIMACS**.
    *   `[OPTIONAL_MFBFS_START_NODES]`: **Applicable exclusively to `mfbfs`**.
        *   A space-delimited sequence of vertex identifiers to serve as the origins for the search.
        *   *Example for mfbfs:* `101 205 340`

**Illustrative Invocations:**

*   **Executing Borůvka's Algorithm:**
    ```bash
    ./run.sh boruvka /mnt/data/graphs/social_connections.dimacs/mnt/data/graphs/large_network.snap
    ```

*   **Executing Multi-source BFS from specified vertices:**
    ```bash
    ./run.sh mfbfs  101 205 340
    ```

## 📊 The Yield: Interpreting Results & Metrics

Upon completion of the computational endeavor, the crucial outputs are delivered:

*   **Primary Algorithm Output:** The core results generated by the algorithm (e.g., the set of edges forming the MST for Borůvka, or reachability information for MFBFS) will be meticulously stored within the `output/` directory (relative to the execution path).
*   **Comprehensive Performance Chronicles:** Detailed telemetry of the execution—including processing duration, communication overhead, and other vital statistics—will be logged in the `run_ALGONAME.res` file (e.g., `run_boruvka.res`, `run_mfbfs.res`). This artifact will also reside in the directory from which `run.sh` was launched.

---

Go forth and illuminate the hidden structures within your data. May your computations be swift and your insights profound. This is how legends are made.