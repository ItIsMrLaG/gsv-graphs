package org.algos.boruvka;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EdgeWritable implements Writable {

    public IntWritable targetId = new IntWritable();
    public EdgeMeta meta = new EdgeMeta();

    public EdgeWritable() {}

    public EdgeWritable(IntWritable targetId, EdgeMeta meta) {
            this.targetId.set(targetId.get());
            this.meta.setMeta(meta);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
//        TODO
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
//        TODO
    }

}
