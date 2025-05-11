#include "reqresp/req-dev.h"
using namespace std;

typedef enum {
    MIN_EDGE_PICKIKNG = 1, // may be useless
    FIND_SUPERVERTEX = 2,
    EDGE_CLEANING_AND_RELABLING = 3,
    SUPERVERTEX_FORMATION = 4
} BorAlgoStep;

#define FIRST_ALGO_STEP MIN_EDGE_PICKIKNG
#define LAST_ALGO_STEP  SUPERVERTEX_FORMATION

// input line format: vertexID \t numOfNeighbors neighbor1 neighbor2 ...
// output line format: v \t min_vertexID(v's connected component)

struct BorEdge {
    int value;
    VertexID from;
    VertexID to;
};

typedef enum {
    UNKNOWN,
    SUPERVERTEX,
    SUBVERTEX // already point to supervertex
} VertexType;

struct BorVertexValue {
    vector<BorEdge> edges;
    VertexID pickedEdgeDestID; // neighbour with minimal edge
    VertexID pointToSupervertex;
    VertexType vertexType = UNKNOWN;
};

// ibinstream& operator<<(ibinstream& m, const CCValue_pregel& v) {
//     m << v.color;
//     m << v.edges;
//     return m;
// }

// obinstream& operator>>(obinstream& m, CCValue_pregel& v) {
//     m >> v.color;
//     m >> v.edges;
//     return m;
// }

struct BorMessage {};

struct BorRespond {
    VertexType vertexType;
    VertexID pointer; // vertex type == UNKNOWN     -> .pickedEdgeDestID
                      //             == SUPERVERTEX -> this
                      //             == SUBVERTEX   -> .pointToSupervertex
};

//====================================

class BorVertex : public RVertex<VertexID, BorVertexValue, BorMessage, BorRespond> {
  private:
    BorAlgoStep algoStep;

  public:
    void vote_to_next_step() {
        vote_to_halt();
        if (algoStep == LAST_ALGO_STEP) {
            algoStep = FIRST_ALGO_STEP;
        } else {
            algoStep = (BorAlgoStep)(algoStep + 1);
        }
    }

    BorRespond Respond() {
        BorVertexValue val = value();
        return BorRespond{val.vertexType, (val.vertexType == UNKNOWN) ? val.pickedEdgeDestID : val.pointToSupervertex};
    };

    void compute(MessageContainer& messages) {}
};

// class CCWorker_pregel : public Worker<CCVertex_pregel> {
//     char buf[100];

//   public:
//     // C version
//     virtual CCVertex_pregel* toVertex(char* line) {
//         char* pch;
//         pch = strtok(line, "\t");
//         CCVertex_pregel* v = new CCVertex_pregel;
//         v->id = atoi(pch);
//         pch = strtok(NULL, " ");
//         int num = atoi(pch);
//         for (int i = 0; i < num; i++) {
//             pch = strtok(NULL, " ");
//             v->value().edges.push_back(atoi(pch));
//         }
//         return v;
//     }

//     virtual void toline(CCVertex_pregel* v, BufferedWriter& writer) {
//         sprintf(buf, "%d\t%d\n", v->id, v->value().color);
//         writer.write(buf);
//     }
// };

// class CCCombiner_pregel : public Combiner<VertexID> {
//   public:
//     virtual void combine(VertexID& old, const VertexID& new_msg) {
//         if (old > new_msg)
//             old = new_msg;
//     }
// };

// void pregel_hashmin(string in_path, string out_path, bool use_combiner) {
//     WorkerParams param;
//     param.input_path = in_path;
//     param.output_path = out_path;
//     param.force_write = true;
//     param.native_dispatcher = false;
//     CCWorker_pregel worker;
//     CCCombiner_pregel combiner;
//     if (use_combiner)
//         worker.setCombiner(&combiner);
//     worker.run(param);
// }
