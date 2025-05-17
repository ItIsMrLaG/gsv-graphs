package org.algos.boruvka;

import static org.algos.boruvka.VertexVoteType.*;

import org.apache.giraph.aggregators.BasicAggregator;
import org.apache.hadoop.io.IntWritable;

public class VoteAggregator extends BasicAggregator<IntWritable> {
  @Override
  public void aggregate(IntWritable newVoteType) {
    VertexVoteType newVote = VertexVoteType.fromWritable(newVoteType);
    VertexVoteType currentVote = VertexVoteType.fromWritable(getAggregatedValue());

    assert newVote == INIT_VOTE;

    switch (currentVote) {
      case INIT_VOTE:
        setAggregatedValue(newVoteType);
        return;
      case CURRENT_VOTE:
        return;
      case NEXT_VOTE:
        if (newVote.code == CURRENT_VOTE.code)
          setAggregatedValue(new IntWritable(CURRENT_VOTE.code));
        return;
      default:
        throw new IllegalStateException();
    }
  }

  @Override
  public IntWritable createInitialValue() {
    return new IntWritable(INIT_VOTE.code);
  }
}
