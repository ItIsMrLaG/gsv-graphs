package org.algos.msbfs;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.giraph.io.EdgeReader;
import org.apache.giraph.io.formats.IntNullReverseTextEdgeInputFormat;
import org.apache.giraph.utils.IntPair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CSVIntNullReverseTextEdgeInputFormat extends IntNullReverseTextEdgeInputFormat {
  private static final Pattern CUSTOM_SEPARATOR = Pattern.compile(",");

  @Override
  public EdgeReader<IntWritable, NullWritable> createEdgeReader(
      InputSplit split, TaskAttemptContext context) {

    return new CSVDelimiterEdgeReader();
  }

  public class CSVDelimiterEdgeReader extends IntNullTextEdgeReader {
    @Override
    protected IntPair preprocessLine(Text line) throws IOException {
      String[] tokens = CUSTOM_SEPARATOR.split(line.toString());
      if (tokens.length != 2) {
        throw new IOException("Invalid line format: " + line);
      }
      return new IntPair(Integer.parseInt(tokens[0].trim()), Integer.parseInt(tokens[1].trim()));
    }
  }
}
