package org.algos.boruvka;

import java.io.IOException;
import java.util.List;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class BoruvkaOutputFormat
    extends TextVertexOutputFormat<IntWritable, BoruvkaVertexValue, EdgeMeta> {

  public static final String LINE_TOKENIZE_VALUE = "output.delimiter";
  public static final String LINE_TOKENIZE_VALUE_DEFAULT = "\t";

  @Override
  public TextVertexOutputFormat<IntWritable, BoruvkaVertexValue, EdgeMeta>.TextVertexWriter
      createVertexWriter(TaskAttemptContext context) throws IOException, InterruptedException {
    return new BoruvkaOutputFormat.BoruvkaIdWithValueVertexWriter();
  }

  public class BoruvkaIdWithValueVertexWriter extends TextVertexWriterToEachLine {
    private String delimiter;

    @Override
    public void initialize(TaskAttemptContext context) throws IOException, InterruptedException {
      super.initialize(context);
      delimiter = getConf().get(LINE_TOKENIZE_VALUE, LINE_TOKENIZE_VALUE_DEFAULT);
    }

    @Override
    protected Text convertVertexToLine(Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex) {
      if (vertex.getValue().type != VertexType.SUPER_VERTEX) return null;

      return new Text(converteValueToString(vertex.getValue().minEdgesMeta));
    }

    private String converteValueToString(List<EdgeMeta> minEdges) {
      StringBuilder str = new StringBuilder();

      for (EdgeMeta edge : minEdges) {
        str.append(edge.pp(delimiter));
        str.append("\n");
      }

      str.deleteCharAt(str.length() - 1);

      return str.toString();
    }
  }
}
