package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Writable;

public class BoruvkaVertexValue implements Writable {
  public boolean isDead = false;

  public int minEdgeHolderId = PhaseSpecValues.DEFAULT.code;
  public VertexType type = VertexType.UNKNOWN;
  public int superVertexId = PhaseSpecValues.DEFAULT.code;

  private EdgeWritable minEdge = new EdgeWritable();

  // COMMON //
  public List<EdgeMeta> minEdgesMeta = new ArrayList<>();

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
    out.writeInt(minEdgesMeta.size());
    for (EdgeMeta edge : minEdgesMeta) {
      edge.write(out);
    }

    out.writeBoolean(isDead);
    out.writeInt(minEdgeHolderId);
    minEdge.write(out);
    out.writeInt(type.code);
    out.writeInt(superVertexId);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    minEdgesMeta.clear();
    int metaSize = in.readInt();
    for (int i = 0; i < metaSize; i++) {
      EdgeMeta edge = new EdgeMeta();
      edge.readFields(in);
      minEdgesMeta.add(edge);
    }

    isDead = in.readBoolean();
    minEdgeHolderId = in.readInt();
    minEdge.readFields(in);

    int typeCode = in.readInt();
    type = VertexType.fromCode(typeCode);

    superVertexId = in.readInt();
  }
}
