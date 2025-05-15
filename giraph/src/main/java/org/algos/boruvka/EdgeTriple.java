package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class EdgeTriple implements Writable {
  private IntWritable first = new IntWritable();
  private IntWritable second = new IntWritable();
  private IntWritable label = new IntWritable();

  public EdgeTriple() {}

  public EdgeTriple(IntWritable from, IntWritable to, IntWritable weight) {
    this.first.set(from.get());
    this.second.set(to.get());
    this.label.set(weight.get());
  }

  public int getFirst() {
    return first.get();
  }

  public int getSecond() {
    return second.get();
  }

  public IntWritable getLabel() {
    return label;
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
