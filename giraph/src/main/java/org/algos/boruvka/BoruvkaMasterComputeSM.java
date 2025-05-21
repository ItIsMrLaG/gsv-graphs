package org.algos.boruvka;

import static org.algos.boruvka.GlobalState.*;

import org.apache.giraph.aggregators.IntSumAggregator;
import org.apache.giraph.master.DefaultMasterCompute;
import org.apache.hadoop.io.IntWritable;
import org.apache.log4j.Logger;

public class BoruvkaMasterComputeSM extends DefaultMasterCompute {

  private static final Logger LOG = Logger.getLogger(BoruvkaMasterComputeSM.class);

  GlobalState state = PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID;

  @Override
  public void initialize() throws InstantiationException, IllegalAccessException {
    registerAggregator("VOTE", VoteAggregator.class);
    registerPersistentAggregator("GLOBAL", GlobalStateIntAggregator.class);
    registerAggregator("CURRENT_COUNTER", IntSumAggregator.class);
    registerAggregator("NEXT_COUNTER", IntSumAggregator.class);
  }

  @Override
  public void compute() {
    VertexVoteType resultVote = VertexVoteType.fromWritable(getAggregatedValue("VOTE"));

    int current_counter = ((IntWritable) getAggregatedValue("CURRENT_COUNTER")).get();
    int next_counter = ((IntWritable) getAggregatedValue("NEXT_COUNTER")).get();

    LOG.fatal(
        "SUPERSTEP:"
            + getSuperstep()
            + " | STATE:"
            + state.code
            + " | NODES FOR NEXT: "
            + next_counter
            + " | NODES FOR CURRENT: "
            + current_counter);

    if (resultVote != VertexVoteType.NEXT_VOTE) {
      setGlobalState(state);
      return;
    }

    switch (state) {
      case PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID:
        setGlobalState(PHASE2_GET_RESPONSE_FROM_CHOSEN_ID);
        return;
      case PHASE2_GET_RESPONSE_FROM_CHOSEN_ID:
        setGlobalState(PHASE3_SUPERNODE_FINDING);
        return;
      case PHASE3_SUPERNODE_FINDING:
        setGlobalState(PHASE4_REQUEST_UPDATE_GRAPH_EDGES);
        return;
      case PHASE4_REQUEST_UPDATE_GRAPH_EDGES:
        setGlobalState(PHASE5_RESPONSE_UPDATE_GRAPH_EDGES);
        return;
      case PHASE5_RESPONSE_UPDATE_GRAPH_EDGES:
        setGlobalState(PHASE6_UPDATE_GRAPH_EDGES);
        return;
      case PHASE6_UPDATE_GRAPH_EDGES:
        setGlobalState(PHASE7_COLLAPSE_TO_SUPER_VERTEX);
        return;
      case PHASE7_COLLAPSE_TO_SUPER_VERTEX:
        setGlobalState(PHASE8_RESET_VERTEX);
        return;
      case PHASE8_RESET_VERTEX:
        setGlobalState(PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID);
        return;

      default:
        throw new IllegalStateException("Impossible state");
    }
  }

  private void setGlobalState(GlobalState globalState) {
    state = globalState;
    setAggregatedValue("GLOBAL", globalState.getWritable());
  }
}
