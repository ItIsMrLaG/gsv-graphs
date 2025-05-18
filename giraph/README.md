# Common 

Each time before run:
```bash
hadoop fs -test -d $G_OUTPUT_DIR && hadoop fs -rm -r -skipTrash $G_OUTPUT_DIR
```

Get stats:
```bash
hadoop fs -get $G_OUTPUT_DIR
python helpers/giraphStatsAggregate.py $G_OUTPUT_DIR
```

---
# Boruvka

**Preset:**
```bash
$G_INPUT_GRAPH=</hdfs/path/to/inputGraph.txt>
$G_OUTPUT_DIR=</hdfs/path/to/resultDir>
$G_THREAD_N=<N>
```
**Input file:**
- `inputGraph.txt`
- each line <=> edge
- a id1 id2 label

**Output file:**
- `part-m-...` (`part-m-00000`)
- each line <=> edge of MST
- id1 id2 label

**Script:**
```bash
hadoop jar giraph-examples/target/giraph-examples-1.3.0-SNAPSHOT-for-hadoop-2.7.7-jar-with-dependencies.jar      \
  org.apache.giraph.GiraphRunner                                                  \
  org.apache.giraph.examples.boruvka.Boruvka                                      \
  -eif org.apache.giraph.examples.boruvka.CSVIntEdgeMetaInputFormat               \
  -vof org.apache.giraph.examples.boruvka.BoruvkaOutputFormat                     \
  -mc org.apache.giraph.examples.boruvka.BoruvkaMasterComputeSM                   \
  -eip "$G_INPUT_GRAPH"                                                           \
  -op "$G_OUTPUT_DIR"                                                             \
  -ca giraph.numComputeThreads=$G_THREAD_N                                        \
  -ca giraph.metrics.enable=true                                                  \
  -ca giraph.metrics.directory="$G_OUTPUT_DIR"                                    \
  -ca giraph.SplitMasterWorker=false                                              \
  -w 1
```

---

# MS-Bfs

**Preset:**
```bash
$G_INPUT_GRAPH=</hdfs/path/to/inputGraph.txt>
$G_OUTPUT_DIR=</hdfs/path/to/resultDir>
$G_THREAD_N=<N>
$G_MSBFS_SOURCE_IDS=</hdfs/path/to/sourceIds.txt>
```
**Input file:**
- `inputGraph.txt`
- each line <=> edge
- id1 \t id2 (\t between)

**Sources file:**
- `sourceIds.txt`
- each line <=> sourceId
```txt
sourceId1
sourceId2
sourceId3
...
```

**Output file:**
- `part-m-...` (`part-m-00000`)
- each line <=> edge of MST
- id \t (sourceId1: parentId1); (sourceId2: parentId2); ... (sourceId2: parentId2)

**Script:**
```bash
hadoop jar giraph-examples/target/giraph-examples-1.3.0-SNAPSHOT-for-hadoop-2.7.7-jar-with-dependencies.jar  \
  org.apache.giraph.GiraphRunner                                                  \
  org.apache.giraph.examples.msbfs.MSBfs                                          \
  -eif org.apache.giraph.examples.msbfs.CSVIntNullReverseTextEdgeInputFormat      \
  -vof org.apache.giraph.examples.msbfs.MSBfsOutputFormat                         \
  -wc org.apache.giraph.examples.msbfs.MSBFSWorkerContext                         \
  -cf "$G_MSBFS_SOURCE_IDS"                                                       \
  -eip "$G_INPUT_GRAPH"                                                           \
  -op "$G_OUTPUT_DIR"                                                             \
  -ca giraph.numComputeThreads=$G_THREAD_N                                        \
  -ca giraph.metrics.enable=true                                                  \
  -ca giraph.metrics.directory="$G_OUTPUT_DIR"                                    \
  -ca giraph.SplitMasterWorker=false                                              \
  -w 1
```
