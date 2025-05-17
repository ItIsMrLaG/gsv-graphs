package org.algos.boruvka;

import static org.algos.boruvka.PhaseSpecValues.DEFAULT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class BoruvkaMsg implements Writable {

  private IntWritable senderId = new IntWritable(DEFAULT.code);
  private IntWritable superVertexId = new IntWritable(DEFAULT.code);
  private IntWritable superVertexResponse = new IntWritable(DEFAULT.code);
  private EdgeMeta superVertexResponseEdgeMeta = new EdgeMeta();
  private ArrayWritable outEdges = new ArrayWritable(EdgeWritable.class, new EdgeWritable[0]);
  private ArrayWritable superVEdges = new ArrayWritable(EdgeMeta.class, new EdgeWritable[0]);

  public BoruvkaMsg() {}

  BoruvkaMsg(IntWritable _senderId) {
    senderId = _senderId;
  }

  void setSenderId(int _senderId) {
    this.senderId.set(_senderId);
  }

  public int getSenderId() {
    return senderId.get();
  }

  void setSuperVertexId(int _superVertexId) {
    this.superVertexId.set(_superVertexId);
  }

  int getSuperVertexId() {
    return superVertexId.get();
  }

  void setSuperVertexResponse(int _superVertexResponse) {
    this.superVertexResponse.set(_superVertexResponse);
  }

  int getSuperVertexResponse() {
    return superVertexResponse.get();
  }

  void setSuperVertexResponseEdgeMeta(EdgeMeta _superVertexResponseEdgeMeta) {
    this.superVertexResponseEdgeMeta.setMeta(_superVertexResponseEdgeMeta);
  }

  EdgeMeta getSuperVertexResponseEdgeMeta() {
    return new EdgeMeta(
        superVertexResponseEdgeMeta.from.get(),
        superVertexResponseEdgeMeta.to.get(),
        superVertexResponseEdgeMeta.label.get());
  }

  void setOutEdges(List<EdgeWritable> _outEdges) {
    outEdges = new ArrayWritable(EdgeWritable.class, _outEdges.toArray(new EdgeWritable[0]));
  }

  List<EdgeWritable> getOutEdges() {
    return arrayWritableToList(outEdges, EdgeWritable.class);
  }

  //  TODO: why don't use
  void setSuperVEdgesMeta(List<EdgeMeta> _superVEdges) {
    superVEdges = new ArrayWritable(EdgeMeta.class, _superVEdges.toArray(new EdgeMeta[0]));
  }

  List<EdgeMeta> getSuperVEdgesMeta() {
    return arrayWritableToList(superVEdges, EdgeMeta.class);
  }

  public static <T extends Writable> List<T> arrayWritableToList(ArrayWritable aw, Class<T> clazz) {
    Writable[] writables = aw.get();
    List<T> result = new ArrayList<>(writables.length);
    for (Writable writable : writables) {
      result.add(clazz.cast(writable));
    }
    return result;
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
