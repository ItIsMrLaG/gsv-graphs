package org.algos.msbfs.runexample;

import org.CommonRunner;
import org.algos.msbfs.CSVIntNullReverseTextEdgeInputFormat;
import org.algos.msbfs.MSBFSWorkerContext;
import org.algos.msbfs.MSBfs;
import org.algos.msbfs.MSBfsOutputFormat;

public class Fun {

  public static void main(String[] args) throws Exception {

    System.exit(
        CommonRunner.run(
            MSBfs.class.getName(),
            new String[] {
              "-eip", System.getProperty("giraph.input.graph"),
              "-cf", System.getProperty("giraph.input.sourceIds"),
              "-eif", CSVIntNullReverseTextEdgeInputFormat.class.getName(),
              "-vof", MSBfsOutputFormat.class.getName(),
              "-wc", MSBFSWorkerContext.class.getName(),
            }));
  }
}
