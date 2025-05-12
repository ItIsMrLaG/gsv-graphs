package org.algos.msbfs;

import com.google.common.collect.ImmutableSet;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.*;
import org.apache.log4j.Logger;

import java.util.Set;

public class MSBfs extends BasicComputation<
        LongWritable, MapWritable, NullWritable, MSBfsMessage> {

    private static final Logger LOG =
            Logger.getLogger(MSBfs.class);

    private static final long STRANGE_VALUE = Long.MIN_VALUE;

    private boolean isSourceVertex(Vertex<LongWritable, ?, ?> vertex) {
        return ((MSBFSWorkerContext) getWorkerContext()).isSource(
                vertex.getId().get());
    }

    @Override
    public void compute(
            Vertex<LongWritable, MapWritable, NullWritable> vertex,
            Iterable<MSBfsMessage> messages
    ) {

        if (getSuperstep() == 0) {
            initializeVertex(vertex);
        }

        MapWritable sourceParentMap = vertex.getValue();
        ImmutableSet.Builder<LongWritable> msgDataBuilder = ImmutableSet.builder();

        for (MSBfsMessage message : messages) {
            LongWritable mbNewParentId = message.getSenderId();

            for (LongWritable mbSourceId : message.get()) {
                if (sourceParentMap.containsKey(mbSourceId)) {

                    // For deterministic behaviour //
                    LongWritable oldParentId = (LongWritable) sourceParentMap.get(mbSourceId);

                    if (mbNewParentId.get() < oldParentId.get()) {
                        sourceParentMap.put(mbSourceId, mbNewParentId);
                    }

                    continue;
                }

                msgDataBuilder.add(mbSourceId);
                sourceParentMap.put(mbSourceId, mbNewParentId);
            }
        }
        vertex.setValue(sourceParentMap);

        Set<LongWritable> msgData = msgDataBuilder.build();
        if (!msgData.isEmpty()) {
            LongWritable[] msgIds = new LongWritable[msgData.size()];

            int i = 0;
            for (LongWritable value : msgData) {
                msgIds[i++] = value;
            }

            sendMessageToAllEdges(vertex, new MSBfsMessage(LongWritable.class, msgIds, vertex.getId()));
        }

        vertex.voteToHalt();
    }

    private void initializeVertex(Vertex<LongWritable, MapWritable, NullWritable> vertex) {
        MapWritable sourceParentMap = new MapWritable(); // parent_id -> part_of_path_from_parent_id

        if (isSourceVertex(vertex)) {
            long sourceIndex = vertex.getId().get();
            sourceParentMap.put(new LongWritable(sourceIndex), new LongWritable(STRANGE_VALUE));
        }

        vertex.setValue(sourceParentMap);
    }
}

