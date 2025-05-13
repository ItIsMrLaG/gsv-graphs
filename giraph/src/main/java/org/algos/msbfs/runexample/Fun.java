package org.algos.msbfs.runexample;

import org.CommonRunner;
import org.algos.common.CSVIntNullReverseTextEdgeInputFormat;
import org.algos.msbfs.MSBFSWorkerContext;
import org.algos.msbfs.MSBfs;
import org.algos.msbfs.MSBfsOutputFormat;
import org.apache.giraph.GiraphRunner;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.hadoop.util.ToolRunner;

public class Fun {

    public static void main(String[] args) throws Exception {

        System.exit(CommonRunner.run(
                MSBfs.class.getName(),
                new String[]{
                        "-eip", System.getProperty("giraph.input.graph"),
                        "-cf", System.getProperty("giraph.input.sourceIds"),
                        "-eif", CSVIntNullReverseTextEdgeInputFormat.class.getName(),
                        "-vof", MSBfsOutputFormat.class.getName(),
                        "-wc", MSBFSWorkerContext.class.getName(),
                }));

    }
}
