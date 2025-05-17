package org.algos.boruvka;

import org.apache.hadoop.io.IntWritable;

public enum GlobalState {
  PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID(1),
  PHASE2_GET_RESPONSE_FROM_CHOSEN_ID(2),
  PHASE3_SUPERNODE_FINDING(3),
  PHASE4_REQUEST_UPDATE_GRAPH_EDGES(4),
  PHASE5_RESPONSE_UPDATE_GRAPH_EDGES(5),
  PHASE6_UPDATE_GRAPH_EDGES(6),
  PHASE7_COLLAPSE_TO_SUPER_VERTEX(7),
  PHASE8_RESET_VERTEX(8);

  public final int code;

  GlobalState(int code) {
    this.code = code;
  }

  public IntWritable getWritable() {
    return new IntWritable(code);
  }

  public static GlobalState fromCode(int code) {
    for (GlobalState vt : GlobalState.values()) {
      if (vt.code == code) return vt;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }

  public static GlobalState fromWritable(IntWritable v) {
    return fromCode(v.get());
  }
}
