package org.algos.boruvka;

import static org.algos.boruvka.PhaseSpecValues.*;
import static org.algos.boruvka.VertexType.*;
import static org.algos.boruvka.VertexVoteType.NEXT_VOTE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.log4j.Logger;

public class Boruvka
    extends BasicComputation<IntWritable, BoruvkaVertexValue, EdgeMeta, BoruvkaMsg> {

  private static final Logger LOG = Logger.getLogger(Boruvka.class);

  @Override
  public void compute(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages)
      throws IOException {

    if (getSuperstep() == 0) {
      vertex.setValue(new BoruvkaVertexValue());
    }

    if (vertex.getValue().isDead) {
      vertex.voteToHalt();
      return;
    }

    switch (getGlobalState()) {
      case PHASE1_CHOSE_MIN_EDGE_AND_SEND_ID:
        phase1_choseMinEdgeAndSendId(vertex, messages);
        break;
      case PHASE2_GET_RESPONSE_FROM_CHOSEN_ID:
        phase2_getResponseFromChosenId(vertex, messages);
        break;
      case PHASE3_SUPERNODE_FINDING:
        phase3_findingSuperVertex(vertex, messages);
        break;
      case PHASE4_REQUEST_UPDATE_GRAPH_EDGES:
        phase4_requestUpdateGraphEdges(vertex, messages);
        break;
      case PHASE5_RESPONSE_UPDATE_GRAPH_EDGES:
        phase5_responseUpdateGraphEdges(vertex, messages);
        break;
      case PHASE6_UPDATE_GRAPH_EDGES:
        phase6_updateGraphEdges(vertex, messages);
        break;
      case PHASE7_COLLAPSE_TO_SUPER_VERTEX:
        phase7_collapseToSuperVertex(vertex, messages);
        break;
      case PHASE8_RESET_VERTEX:
        phase8_resetVertex(vertex, messages);
        break;
      default:
        throw new IllegalStateException();
    }
  }

  /* ~~~~~~~~~~~~~~~~~~~ PHASES ~~~~~~~~~~~~~~~~~~~ */

  //  ++++++++++++  PHASE 1  ++++++++++++  //

  void phase1_choseMinEdgeAndSendId(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {

    Edge<IntWritable, EdgeMeta> minEdge = null;

    for (Edge<IntWritable, EdgeMeta> edge : vertex.getEdges()) {
      int label = edge.getValue().label.get();
      int toId = edge.getTargetVertexId().get();

      if (minEdge == null || label < minEdge.getValue().label.get()) {
        minEdge = edge;
        continue;
      }

      //      if (edge.getValue().label.get() < label) minEdge = edge;

      if (label == minEdge.getValue().label.get() && toId < minEdge.getTargetVertexId().get())
        minEdge = edge;
    }

    BoruvkaVertexValue myValue = vertex.getValue();

    if (minEdge == null) {
      myValue.isDead = true;
      myValue.type = SUPER_VERTEX;
      voteToNext();
      vertex.setValue(myValue);
      vertex.voteToHalt();
      return;
    }

    myValue.minEdgeHolderId = minEdge.getTargetVertexId().get();
    myValue.minEdge = new EdgeWritable(minEdge.getTargetVertexId(), minEdge.getValue());

    sendMessage(minEdge.getTargetVertexId(), new BoruvkaMsg(vertex.getId()));
    vertex.setValue(myValue);
    voteToNext();
  }

  //  ++++++++++++  PHASE 2  ++++++++++++  //

  void phase2_getResponseFromChosenId(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {
    BoruvkaVertexValue myValue = vertex.getValue();
    boolean isReceived = false;

    for (BoruvkaMsg msg : messages) {
      if (isReceived) continue;

      isReceived = msg.senderId.get() == vertex.getValue().minEdgeHolderId;
    }

    if (isReceived) {
      int myId = vertex.getId().get();
      int toId = myValue.minEdgeHolderId;

      if (myId < toId) {
        myValue.type = SUPER_VERTEX;
        myValue.superVertexId = myId;
      } else {
        myValue.type = SUPER_VERTEX_PART;
        myValue.superVertexId = toId;
      }
    }

    if (myValue.type != SUPER_VERTEX) myValue.minEdgesMeta.add(myValue.minEdge.meta);

    vertex.setValue(myValue);
    voteToNext();
  }

  //  ++++++++++++  PHASE 3  ++++++++++++  //

  void phase3_findingSuperVertex(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {
    BoruvkaVertexValue myValue = vertex.getValue();

    switch (myValue.type) {
      case UNKNOWN:
        for (BoruvkaMsg msg : messages) {
          int mbSuperVertexId = msg.superVertexId.get();

          if (mbSuperVertexId == PHASE3_REQUEST_SUPER_V_ID.code) continue;

          myValue.superVertexId = mbSuperVertexId;
          myValue.type = SUPER_VERTEX_PART;
          vertex.setValue(myValue);
          voteToNext();
          return;
        }

        BoruvkaMsg msg = new BoruvkaMsg(vertex.getId());
        msg.superVertexId = new IntWritable(PHASE3_REQUEST_SUPER_V_ID.code);

        sendMessage(new IntWritable(myValue.minEdgeHolderId), msg);
        voteToCurrent();
        return;

      case SUPER_VERTEX:
      case SUPER_VERTEX_PART:
        for (BoruvkaMsg message : messages) {
          if (message.superVertexId.get() == PHASE3_REQUEST_SUPER_V_ID.code) {
            BoruvkaMsg response = new BoruvkaMsg(vertex.getId());

            response.superVertexId = new IntWritable(myValue.superVertexId);
            sendMessage(message.senderId, response);
          }
        }

        voteToNext();
        return;

      default:
        throw new IllegalStateException();
    }
  }

  //  ++++++++++++  PHASE 4-5-6  ++++++++++++  //

  void phase4_requestUpdateGraphEdges(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {

    BoruvkaMsg msg = new BoruvkaMsg(vertex.getId());
    msg.superVertexResponse = new IntWritable(PHASE45_REQUEST_SUPER_V_ID.code);

    for (Edge<IntWritable, EdgeMeta> edge : vertex.getEdges()) {
      msg.superVertexResponseEdgeMeta = edge.getValue();
      sendMessage(edge.getTargetVertexId(), msg);
    }

    voteToNext();
  }

  void phase5_responseUpdateGraphEdges(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {
    for (BoruvkaMsg msg : messages) {
      BoruvkaMsg newMsg = new BoruvkaMsg(vertex.getId());

      newMsg.superVertexResponse = new IntWritable(vertex.getValue().superVertexId);
      newMsg.superVertexResponseEdgeMeta = msg.superVertexResponseEdgeMeta;

      if (msg.superVertexResponse.get() == PHASE45_REQUEST_SUPER_V_ID.code)
        sendMessage(msg.senderId, newMsg);
      else LOG.assertLog(msg.superVertexResponse.get() == DEFAULT.code, "Strange message1");
    }
    voteToNext();
  }

  void phase6_updateGraphEdges(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {
    BoruvkaVertexValue myValue = vertex.getValue();

    HashMap<IntWritable, EdgeMeta> superVIdToMinEdgeMeta = new HashMap<>();

    for (BoruvkaMsg msg : messages) {
      IntWritable otherSuperVId = msg.superVertexResponse;

      if (otherSuperVId.get() == myValue.superVertexId) continue;

      EdgeMeta mbMinEdgeMeta = msg.superVertexResponseEdgeMeta;

      if (superVIdToMinEdgeMeta.containsKey(otherSuperVId)) {
        EdgeMeta oldMinEdgeMeta = superVIdToMinEdgeMeta.get(otherSuperVId);

        if (mbMinEdgeMeta.label.get() < oldMinEdgeMeta.label.get())
          superVIdToMinEdgeMeta.put(otherSuperVId, mbMinEdgeMeta);

        continue;
      }

      superVIdToMinEdgeMeta.put(otherSuperVId, mbMinEdgeMeta);
    }

    IntWritable mySuperVertexId = new IntWritable(myValue.superVertexId);

    //    ADD EDGES TO ANOTHER SUPER VERTEXES
    List<EdgeWritable> outEdges = new ArrayList<>();

    for (IntWritable otherSuperVId : superVIdToMinEdgeMeta.keySet()) {
      EdgeMeta minEdgeMetaLabel = superVIdToMinEdgeMeta.get(otherSuperVId);

      outEdges.add(new EdgeWritable(otherSuperVId, minEdgeMetaLabel));
    }

    //    CREATE MSG TO SUPER VERTEX
    BoruvkaMsg msg = new BoruvkaMsg(vertex.getId());
    msg.setOutEdges(outEdges);
    msg.setSuperVEdgesMeta(myValue.minEdgesMeta);

    //    SEND INFO TO SUPER VERTEX
    Edge<IntWritable, EdgeMeta> edgeToSuperVertex =
        EdgeFactory.create(mySuperVertexId, new EdgeMeta());

    vertex.addEdge(edgeToSuperVertex);
    sendMessage(mySuperVertexId, msg);

    //    NEXT STEP
    vertex.setValue(myValue);
    voteToNext();
  }

  //  ++++++++++++  PHASE 7  ++++++++++++  //

  void phase7_collapseToSuperVertex(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {
    BoruvkaVertexValue myValue = vertex.getValue();

    //    DELETE ALL EDGES
    vertex.setEdges(new ArrayList<>());

    switch (myValue.type) {
      case UNKNOWN:
        myValue.isDead = true;
        LOG.error("Strange type");
        break;
      case SUPER_VERTEX_PART:
        myValue.isDead = true;
        break;
      case SUPER_VERTEX:
        myValue.isDead = false;

        HashMap<IntWritable, EdgeWritable> superVIdToMinEdges = new HashMap<>();

        for (BoruvkaMsg msg : messages) {
          myValue.minEdgesMeta.addAll(msg.getSuperVEdgesMeta());

          for (EdgeWritable myEdge : msg.getOutEdges()) {
            int mbNewMinLabel = myEdge.meta.getLabel();

            if (superVIdToMinEdges.containsKey(myEdge.targetId)) {
              int oldMinLabel = superVIdToMinEdges.get(myEdge.targetId).meta.getLabel();

              if (mbNewMinLabel < oldMinLabel) superVIdToMinEdges.put(myEdge.targetId, myEdge);

              continue;
            }

            superVIdToMinEdges.put(myEdge.targetId, myEdge);
          }
        }

        //  REGISTER NEW EDGES
        for (IntWritable otherSuperVId : superVIdToMinEdges.keySet()) {
          EdgeWritable newEdge = superVIdToMinEdges.get(otherSuperVId);

          Edge<IntWritable, EdgeMeta> edgeToOtherSuperV =
              EdgeFactory.create(otherSuperVId, newEdge.meta);

          vertex.addEdge(edgeToOtherSuperV);
        }

      default:
        throw new IllegalStateException();
    }

    vertex.setValue(myValue);
    voteToNext();
  }

  //  ++++++++++++  PHASE 8  ++++++++++++  //

  void phase8_resetVertex(
      Vertex<IntWritable, BoruvkaVertexValue, EdgeMeta> vertex, Iterable<BoruvkaMsg> messages) {
    BoruvkaVertexValue myValue = vertex.getValue();

    myValue.reset();
    vertex.setValue(myValue);
    voteToNext();
  }

  /* ~~~~~~~~~~~~~~~~~~~ TOOLS ~~~~~~~~~~~~~~~~~~~ */

  GlobalState getGlobalState() {
    return GlobalState.fromCode(((IntWritable) getAggregatedValue("GLOBAL")).get());
  }

  void voteToNext() {
    aggregate(GlobalState.class.getName(), new IntWritable(NEXT_VOTE.code));
  }

  void voteToCurrent() {
    aggregate(GlobalState.class.getName(), new IntWritable(NEXT_VOTE.code));
  }
}
