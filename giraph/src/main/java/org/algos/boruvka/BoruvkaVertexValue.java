package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

public class BoruvkaVertexValue implements Writable {
  public boolean isDead = false;

  public int minEdgeHolderId = PhaseSpecValues.DEFAULT.code;
  public VertexType type = VertexType.UNKNOWN;
  public int superVertexId = PhaseSpecValues.DEFAULT.code;

  private EdgeWritable minEdge = new EdgeWritable();

  // COMMON //
  public BoruvkaVertexValue() {}

  public EdgeWritable getMinEdge() {
    return new EdgeWritable(minEdge.getTargetId(), minEdge.getMeta());
  }

  public void setMinEdge(EdgeWritable minEdge) {
    this.minEdge.setTargetId(minEdge.getTargetId());
    this.minEdge.setMeta(minEdge.getMeta());
  }

  public void setMinEdge(int targetId, EdgeMeta meta) {
    this.minEdge.setTargetId(targetId);
    this.minEdge.setMeta(meta);
  }

  public void reset() {
    isDead = false;
    minEdgeHolderId = PhaseSpecValues.DEFAULT.code;
    minEdge.reset();
    type = VertexType.UNKNOWN;
    superVertexId = PhaseSpecValues.DEFAULT.code;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeBoolean(isDead);
    out.writeInt(minEdgeHolderId);
    minEdge.write(out);
    out.writeInt(type.code);
    out.writeInt(superVertexId);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    isDead = in.readBoolean();
    minEdgeHolderId = in.readInt();
    minEdge.readFields(in);

    int typeCode = in.readInt();
    type = VertexType.fromCode(typeCode);

    superVertexId = in.readInt();
  }
}
