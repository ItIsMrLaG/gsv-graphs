package org.algos.boruvka;

import org.apache.hadoop.io.IntWritable;

public enum VertexType {
  SUPER_VERTEX(0),
  SUPER_VERTEX_PART(1),
  UNKNOWN(2);

  public final int code;

  VertexType(int code) {
    this.code = code;
  }

  public IntWritable getWritable() {
    return new IntWritable(code);
  }

  public static VertexType fromCode(int code) {
    for (VertexType vt : VertexType.values()) {
      if (vt.code == code) return vt;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }
}
