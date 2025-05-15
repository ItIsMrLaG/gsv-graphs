package org.algos.msbfs;

import java.util.HashSet;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.*;
import org.apache.log4j.Logger;

public class MSBfs extends BasicComputation<IntWritable, MapWritable, NullWritable, MSBfsMessage> {

  private static final Logger LOG = Logger.getLogger(MSBfs.class);

  private static final int STRANGE_VALUE = -1;

  private boolean isSourceVertex(Vertex<IntWritable, ?, ?> vertex) {
    return ((MSBFSWorkerContext) getWorkerContext()).isSource(vertex.getId().get());
  }

  @Override
  public void compute(
      Vertex<IntWritable, MapWritable, NullWritable> vertex, Iterable<MSBfsMessage> messages) {
    HashSet<IntWritable> msgData = new HashSet<>();

    if (getSuperstep() == 0) {
      initializeVertex(vertex);

      if (isSourceVertex(vertex)) {
        msgData.add(vertex.getId());
      }
    }

    MapWritable sourceParentMap = vertex.getValue();

    for (MSBfsMessage message : messages) {
      IntWritable mbNewParentId = message.getSenderId();

      for (IntWritable mbSourceId : message.get()) {
        if (msgData.contains(mbSourceId)) {

          // For deterministic behaviour //
          IntWritable oldParentId = (IntWritable) sourceParentMap.get(mbSourceId);

          if (mbNewParentId.get() < oldParentId.get()) {
            sourceParentMap.put(mbSourceId, mbNewParentId);
          }

          continue;
        }

        if (sourceParentMap.containsKey(mbSourceId)) {
          continue;
        }

        msgData.add(mbSourceId);
        sourceParentMap.put(mbSourceId, mbNewParentId);
      }
    }
    vertex.setValue(sourceParentMap);

    if (!msgData.isEmpty()) {
      IntWritable[] msgIds = new IntWritable[msgData.size()];

      int i = 0;
      for (IntWritable value : msgData) {
        msgIds[i++] = value;
      }

      sendMessageToAllEdges(vertex, new MSBfsMessage(IntWritable.class, msgIds, vertex.getId()));
    }

    vertex.voteToHalt();
  }

  private void initializeVertex(Vertex<IntWritable, MapWritable, NullWritable> vertex) {
    MapWritable sourceParentMap = new MapWritable(); // parent_id -> part_of_path_from_parent_id

    if (isSourceVertex(vertex)) {
      int sourceIndex = vertex.getId().get();
      sourceParentMap.put(new IntWritable(sourceIndex), new IntWritable(STRANGE_VALUE));
    }

    vertex.setValue(sourceParentMap);
  }
}
