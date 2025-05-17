package org.algos.boruvka;

public enum PhaseSpecValues {
  DEFAULT(-100),
  SYNTHETIC_LABEL_WAIT_DELETION(-101),
  PHASE3_REQUEST_SUPER_V_ID(-4),
  PHASE45_REQUEST_SUPER_V_ID(-6);

  public final int code;

  PhaseSpecValues(int code) {
    this.code = code;
  }
}
