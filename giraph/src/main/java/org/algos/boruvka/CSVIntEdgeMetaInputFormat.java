package org.algos.boruvka;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.giraph.io.EdgeReader;
import org.apache.giraph.io.formats.TextEdgeInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CSVIntEdgeMetaInputFormat extends TextEdgeInputFormat<IntWritable, EdgeMeta> {
  private static final Pattern SEPARATOR = Pattern.compile(" ");

  @Override
  public EdgeReader<IntWritable, EdgeMeta> createEdgeReader(
      InputSplit split, TaskAttemptContext context) throws IOException {
    return new CSVIntEdgeMetaReader();
  }

  public class CSVIntEdgeMetaReader extends TextEdgeReaderFromEachLineProcessed<EdgeMeta> {

    @Override
    protected EdgeMeta preprocessLine(Text line) throws IOException {

      //      OTHER META UNSUPPORTED ('a', 'p')
      String[] tokens = SEPARATOR.split(line.toString());
      if (!tokens[0].equals("a") || tokens.length < 4) {
        throw new IOException(String.format("Invalid CSV line '%s'", line));
      }

      int from = Integer.parseInt(tokens[1]);
      int to = Integer.parseInt(tokens[2]);
      int label = Integer.parseInt(tokens[3]);

      return new EdgeMeta(from, to, label);
    }

    @Override
    protected IntWritable getSourceVertexId(EdgeMeta edge) throws IOException {
      return edge.from;
    }

    @Override
    protected IntWritable getTargetVertexId(EdgeMeta edge) throws IOException {
      return edge.to;
    }

    @Override
    protected EdgeMeta getValue(EdgeMeta edge) throws IOException {
      return edge;
    }
  }
}
