package org.algos.boruvka;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static org.algos.boruvka.PhaseSpecValues.PHASE2_DEFAULT;
import static org.algos.boruvka.PhaseSpecValues.PHASE3_DEFAULT;

public class BoruvkaMsg implements Writable {

    public IntWritable senderId;
    public IntWritable superVertexId = new IntWritable(PHASE2_DEFAULT.code);
    public IntWritable superVertexResponse = new IntWritable(PHASE3_DEFAULT.code);

    public BoruvkaMsg() {
    }

    BoruvkaMsg(IntWritable _senderId) {
        senderId = _senderId;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }
}
