package org.algos.boruvka;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoruvkaVertexValue implements Writable {

    // COMMON //
    List<EdgeTriple> edges = new ArrayList<>();
    boolean isDead = false;

    // PHASE1 //
    int minEdgeHolderId;
    EdgeTriple minEdgeTriple;

    // PHASE2 //
    VertexType type;
    int superVertexId;


    BoruvkaVertexValue() {
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
//            TODO
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
//            TODO
    }
}
