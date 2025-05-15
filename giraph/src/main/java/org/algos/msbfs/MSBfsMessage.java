package org.algos.msbfs;

import org.apache.giraph.utils.ArrayWritable;
import org.apache.hadoop.io.IntWritable;

public class MSBfsMessage extends ArrayWritable<IntWritable> {

  public MSBfsMessage() {}

  public MSBfsMessage(Class<IntWritable> valueClass, IntWritable[] values, IntWritable senderId) {
    super(valueClass, values);

    IntWritable[] _values = new IntWritable[values.length + 1];
    System.arraycopy(values, 0, _values, 0, values.length);
    _values[values.length] = senderId;

    this.set(_values);
  }

  @Override
  public IntWritable[] get() {
    IntWritable[] _values = super.get();
    IntWritable[] values = new IntWritable[_values.length - 1];
    System.arraycopy(_values, 0, values, 0, values.length);

    return values;
  }

  public IntWritable getSenderId() {
    IntWritable[] _values = super.get();
    return _values[_values.length - 1];
  }
}
