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

  public IntWritable senderId = new IntWritable(DEFAULT.code);
  public IntWritable superVertexId = new IntWritable(DEFAULT.code);
  public IntWritable superVertexResponse = new IntWritable(DEFAULT.code);
  public EdgeMeta superVertexResponseEdgeMeta = new EdgeMeta();
  private ArrayWritable outEdges = new ArrayWritable(EdgeMeta.class);
  private ArrayWritable superVEdges = new ArrayWritable(EdgeMeta.class);

  public BoruvkaMsg() {}

  BoruvkaMsg(IntWritable _senderId) {
    senderId = _senderId;
  }

  void setOutEdges(List<EdgeWritable> _outEdges) {
    outEdges = new ArrayWritable(EdgeMeta.class, _outEdges.toArray(new EdgeWritable[0]));
  }

  List<EdgeWritable> getOutEdges() {
    assert outEdges != null;
    return Arrays.asList((EdgeWritable[]) outEdges.get());
  }

  //  TODO: why don't use
  void setSuperVEdgesMeta(List<EdgeMeta> _superVEdges) {
    superVEdges = new ArrayWritable(EdgeMeta.class, _superVEdges.toArray(new EdgeMeta[0]));
  }

  List<EdgeMeta> getSuperVEdgesMeta() {
    assert superVEdges != null;
    return Arrays.asList((EdgeMeta[]) superVEdges.get());
  }

  @Override
  public void write(DataOutput out) throws IOException {
    senderId.write(out);
    superVertexId.write(out);
    superVertexResponse.write(out);
    superVertexResponseEdgeMeta.write(out);

    outEdges.write(out);
    superVEdges.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    senderId.readFields(in);
    superVertexId.readFields(in);
    superVertexResponse.readFields(in);
    superVertexResponseEdgeMeta.readFields(in);

    outEdges.readFields(in);
    superVEdges.readFields(in);
  }
}
