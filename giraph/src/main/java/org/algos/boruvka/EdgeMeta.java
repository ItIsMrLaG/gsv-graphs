package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class EdgeMeta implements Writable {
  public IntWritable first = new IntWritable();
  public IntWritable second = new IntWritable();
  public IntWritable label = new IntWritable();

  public EdgeMeta() {}

  public EdgeMeta(IntWritable first, IntWritable second, IntWritable label) {
    this.first.set(first.get());
    this.second.set(second.get());
    this.label.set(label.get());
  }
  
  public int getLabel() {
    return label.get();
  }

  public void setMeta(EdgeMeta other) {
        this.first.set(other.first.get());
        this.second.set(other.second.get());
        this.label.set(other.label.get());
  }
  
  @Override
  public void write(DataOutput out) throws IOException {
    first.write(out);
    second.write(out);
    label.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    first.readFields(in);
    second.readFields(in);
    label.readFields(in);
  }
}
