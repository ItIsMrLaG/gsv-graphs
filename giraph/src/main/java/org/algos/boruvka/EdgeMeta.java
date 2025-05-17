package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class EdgeMeta implements Writable {
  public IntWritable from = new IntWritable();
  public IntWritable to = new IntWritable();
  public IntWritable label = new IntWritable();

  public EdgeMeta() {}

  public EdgeMeta(int from, int to, int label) {
    this.from.set(from);
    this.to.set(to);
    this.label.set(label);
  }

  public int getLabel() {
    return label.get();
  }

  public void setMeta(EdgeMeta other) {
    this.from.set(other.from.get());
    this.to.set(other.to.get());
    this.label.set(other.label.get());
  }

  public String pp(String delimiter) {
    StringBuilder str = new StringBuilder();

    if (from.get() > to.get()) {
      str.append(from.get());
      str.append(delimiter);
      str.append(to.get());
      str.append(delimiter);
    } else {
      str.append(to.get());
      str.append(delimiter);
      str.append(from.get());
      str.append(delimiter);
    }

    str.append(label.get());

    return str.toString();
  }

  public void reset() {
    from.set(PhaseSpecValues.DEFAULT.code);
    to.set(PhaseSpecValues.DEFAULT.code);
    label.set(PhaseSpecValues.DEFAULT.code);
  }

  @Override
  public String toString() {
    return pp(",");
  }

  @Override
  public void write(DataOutput out) throws IOException {
    from.write(out);
    to.write(out);
    label.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    from.readFields(in);
    to.readFields(in);
    label.readFields(in);
  }
}
