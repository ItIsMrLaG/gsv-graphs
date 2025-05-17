package org.algos.msbfs;

import java.io.IOException;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * Format:
 *
 * <pre>{@code
 * id\t(sourceId1: parentId1); (sourceId2: parentId2); ... (sourceId2: parentId2)
 *
 * <=>
 *
 * id  (sourceId1: parentId1); (sourceId2: parentId2); ... (sourceId2: parentId2)
 * }</pre>
 */
public class MSBfsOutputFormat
    extends TextVertexOutputFormat<IntWritable, MapWritable, NullWritable> {

  public static final String LINE_TOKENIZE_VALUE = "output.delimiter";
  public static final String LINE_TOKENIZE_VALUE_DEFAULT = "\t";

  @Override
  public MsBfsIdWithValueVertexWriter createVertexWriter(TaskAttemptContext context) {
    return new MSBfsOutputFormat.MsBfsIdWithValueVertexWriter();
  }

  public class MsBfsIdWithValueVertexWriter extends TextVertexWriterToEachLine {
    private String delimiter;

    @Override
    public void initialize(TaskAttemptContext context) throws IOException, InterruptedException {
      super.initialize(context);
      delimiter = getConf().get(LINE_TOKENIZE_VALUE, LINE_TOKENIZE_VALUE_DEFAULT);
    }

    @Override
    protected Text convertVertexToLine(Vertex<IntWritable, MapWritable, NullWritable> vertex) {

      StringBuilder str = new StringBuilder();

      str.append(vertex.getId().toString());
      str.append(delimiter);
      str.append(converteValueToString(vertex.getValue()));

      return new Text(str.toString());
    }

    private String converteValueToString(MapWritable map) {
      StringBuilder str = new StringBuilder();

      for (Writable key : map.keySet()) {
        str.append("(");
        str.append(key.toString());
        str.append(":");
        str.append(map.get(key).toString());
        str.append(");");
      }

      return str.toString();
    }
  }
}
