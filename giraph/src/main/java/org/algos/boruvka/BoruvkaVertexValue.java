package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Writable;

public class BoruvkaVertexValue implements Writable {
  boolean isDead = false;

  int minEdgeHolderId = PhaseSpecValues.DEFAULT.code;
  EdgeWritable minEdge = new EdgeWritable();

  VertexType type = VertexType.UNKNOWN;
  int superVertexId = PhaseSpecValues.DEFAULT.code;

  // COMMON //
  List<EdgeMeta> minEdgesMeta = new ArrayList<>();

  BoruvkaVertexValue() {}

  public void reset() {
    isDead = false;
    minEdgeHolderId = PhaseSpecValues.DEFAULT.code;
    minEdge = new EdgeWritable();
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
