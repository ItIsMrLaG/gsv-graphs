package org.myexample;

import org.CommonRunner;
import org.apache.giraph.io.formats.IdWithValueTextOutputFormat;
import org.apache.giraph.io.formats.JsonLongDoubleFloatDoubleVertexInputFormat;

public class Fun {

  public static void main(String[] args) throws Exception {

    System.exit(
        CommonRunner.run(
            GiraphShortestDistance.class.getName(),
            new String[] {
              "-vip", System.getProperty("giraph.input.graph"),
              "-vif", JsonLongDoubleFloatDoubleVertexInputFormat.class.getName(),
              "-vof", IdWithValueTextOutputFormat.class.getName(),
            }));
  }
}
