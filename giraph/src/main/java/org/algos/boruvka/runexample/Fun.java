package org.algos.boruvka.runexample;

import org.CommonRunner;
import org.algos.boruvka.Boruvka;
import org.algos.boruvka.BoruvkaMasterComputeSM;
import org.algos.boruvka.BoruvkaOutputFormat;
import org.algos.boruvka.CSVIntEdgeMetaInputFormat;

public class Fun {

  public static void main(String[] args) throws Exception {

    System.exit(
        CommonRunner.run(
            Boruvka.class.getName(),
            new String[] {
              "-eip", System.getProperty("giraph.input.graph"),
              "-eif", CSVIntEdgeMetaInputFormat.class.getName(),
              "-vof", BoruvkaOutputFormat.class.getName(),
              "-mc", BoruvkaMasterComputeSM.class.getName(),
            }));
  }
}
