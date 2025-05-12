package org.algos.msbfs;

import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MSBFS extends BasicComputation<
        LongWritable, DoubleWritable, FloatWritable, DoubleWritable> {

    private static final Logger LOG =
            Logger.getLogger(MSBFS.class);

    private boolean isSourceVertex(Vertex<LongWritable, ?, ?> vertex) {
        return ((MSBFSWorkerContext) getWorkerContext()).isSource(
                vertex.getId().get());
    }

    private int numSourceVertices() {
        return ((MSBFSWorkerContext) getWorkerContext()).numSources();
    }

//    TODO: implement data holder

    @Override
    public void compute(
            Vertex<LongWritable, DoubleWritable, FloatWritable> vertex,
            Iterable<DoubleWritable> messages) throws IOException {

//        TODO:

    }
}

