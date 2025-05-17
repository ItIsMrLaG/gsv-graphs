package org.algos.boruvka;

import org.apache.giraph.aggregators.IntOverwriteAggregator;
import org.apache.hadoop.io.IntWritable;

public class GlobalStateIntAggregator extends IntOverwriteAggregator {

  @Override
  public IntWritable createInitialValue() {
    return new IntWritable(GlobalState.PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID.code);
  }
}
