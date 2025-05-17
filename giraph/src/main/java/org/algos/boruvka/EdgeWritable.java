package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class EdgeWritable implements Writable {

  private IntWritable targetId = new IntWritable();
  private EdgeMeta meta = new EdgeMeta();

  public EdgeWritable() {}

  public EdgeWritable(int targetId, EdgeMeta meta) {
    this.targetId.set(targetId);
    this.meta.setMeta(meta);
  }

  public EdgeWritable clone() {
    return new EdgeWritable(targetId.get(), meta);
  }

  public void setTargetId(int targetId) {
    this.targetId.set(targetId);
  }

  public int getTargetId() {
    return this.targetId.get();
  }

  public EdgeMeta getMeta() {
    EdgeMeta newMeta = new EdgeMeta();
    newMeta.setMeta(meta);
    return newMeta;
  }

  public void setMeta(EdgeMeta meta) {
    this.meta.setMeta(meta);
  }

  public void reset() {
    targetId.set(PhaseSpecValues.DEFAULT.code);
    meta.reset();
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    targetId.write(dataOutput);
    meta.write(dataOutput);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    targetId.readFields(dataInput);
    meta.readFields(dataInput);
  }
}
