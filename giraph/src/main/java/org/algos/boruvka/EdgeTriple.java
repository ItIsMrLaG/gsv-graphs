package org.algos.boruvka;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EdgeTriple implements Writable {
    private IntWritable from = new IntWritable();
    private IntWritable to = new IntWritable();
    private IntWritable weight = new IntWritable();

    public EdgeTriple() {}

    public EdgeTriple(IntWritable from, IntWritable to, IntWritable weight) {
        this.from.set(from.get());
        this.to.set(to.get());
        this.weight.set(weight.get());
    }

    public int getFrom() { return from.get(); }
    public int getTo() { return to.get(); }
    public int getWeight() { return weight.get(); }

    @Override
    public void write(DataOutput out) throws IOException {
        from.write(out);
        to.write(out);
        weight.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        from.readFields(in);
        to.readFields(in);
        weight.readFields(in);
    }
}
