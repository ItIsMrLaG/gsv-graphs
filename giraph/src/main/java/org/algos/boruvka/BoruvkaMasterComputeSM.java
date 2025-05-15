package org.algos.boruvka;

import org.apache.giraph.aggregators.IntOverwriteAggregator;
import org.apache.giraph.master.DefaultMasterCompute;

import static org.algos.boruvka.GlobalState.*;

public class BoruvkaMasterComputeSM extends DefaultMasterCompute {

    GlobalState state = PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID;

    @Override
    public void initialize() throws InstantiationException, IllegalAccessException {
        registerAggregator("VOTE", StateAggregator.class);

        registerPersistentAggregator("GLOBAL", IntOverwriteAggregator.class);
        setGlobalState(PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID);
    }

    @Override
    public void compute() {
        VertexVoteType resultVote = VertexVoteType.fromWritable(getAggregatedValue(VertexVoteType.class.getName()));

        if (resultVote != VertexVoteType.NEXT_VOTE) return;

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

            default:
                throw new IllegalStateException("Impossible state");
        }
    }

    void setGlobalState(GlobalState globalState) {
        setAggregatedValue("GLOBAL", globalState.getWritable());
    }

}
