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
    /**
     * The shortest paths id
     */
    public static final LongConfOption SOURCE_ID =
            new LongConfOption("SimpleShortestPathsVertex.sourceId", 1,
                    "The shortest paths id");

    private static final Logger LOG =
            Logger.getLogger(MSBFS.class);

    @Override
    public void compute(
            Vertex<LongWritable, DoubleWritable, FloatWritable> vertex,
            Iterable<DoubleWritable> messages) throws IOException {

    }
}

