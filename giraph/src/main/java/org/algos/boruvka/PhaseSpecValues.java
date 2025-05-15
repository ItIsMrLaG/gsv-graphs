package org.algos.boruvka;

public enum PhaseSpecValues {
  PHASE2_DEFAULT(-2),
  PHASE3_DEFAULT(-3),
  PHASE3_REQUEST_SUPER_V_ID(-4),
  PHASE4_DEFAULT(-5),
  PHASE45_REQUEST_SUPER_V_ID(-6);

  public final int code;

  PhaseSpecValues(int code) {
    this.code = code;
  }
}
