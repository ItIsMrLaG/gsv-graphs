package org.algos.boruvka;

import org.apache.hadoop.io.IntWritable;

public enum VertexVoteType {
  INIT_VOTE(0),
  NEXT_VOTE(1),
  CURRENT_VOTE(2);

  public final int code;

  VertexVoteType(int code) {
    this.code = code;
  }

  public IntWritable getWritable() {
    return new IntWritable(code);
  }

  public static VertexVoteType fromCode(int code) {
    for (VertexVoteType vt : VertexVoteType.values()) {
      if (vt.code == code) return vt;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }

  public static VertexVoteType fromWritable(IntWritable v) {
    return fromCode(v.get());
  }
}
