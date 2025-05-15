package org.algos.boruvka;

import static org.algos.boruvka.PhaseSpecValues.DEFAULT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class BoruvkaMsg implements Writable {

  public IntWritable senderId;
  public IntWritable superVertexId = new IntWritable(DEFAULT.code);
  public IntWritable superVertexResponse = new IntWritable(DEFAULT.code);
  public IntWritable superVertexResponseLabel = new IntWritable(DEFAULT.code);
  private ArrayWritable outEdges = null;
  private ArrayWritable superVEdges = null;

  public BoruvkaMsg() {}

  BoruvkaMsg(IntWritable _senderId) {
    senderId = _senderId;
  }

  void setOutEdges(List<EdgeTriple> _outEdges) {
    outEdges = new ArrayWritable(EdgeTriple.class, _outEdges.toArray(new EdgeTriple[0]));
  }

  List<EdgeTriple> getOutEdges() {
    assert outEdges != null;
    return Arrays.asList((EdgeTriple[]) outEdges.get());
  }

  void setSuperVEdges(List<EdgeTriple> _superVEdges) {
    superVEdges = new ArrayWritable(EdgeTriple.class, _superVEdges.toArray(new EdgeTriple[0]));
  }

  List<EdgeTriple> getSuperVEdges() {
    assert superVEdges != null;
    return Arrays.asList((EdgeTriple[]) superVEdges.get());
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {}

  @Override
  public void readFields(DataInput dataInput) throws IOException {}
}
