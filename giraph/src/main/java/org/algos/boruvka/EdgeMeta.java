package org.algos.boruvka;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class EdgeMeta implements Writable {
    public IntWritable from = new IntWritable();
    public IntWritable to = new IntWritable();
    public IntWritable label = new IntWritable();

    public EdgeMeta() {
    }

    public EdgeMeta(IntWritable from, IntWritable to, IntWritable label) {
        this.from.set(from.get());
        this.to.set(to.get());
        this.label.set(label.get());
    }

    public int getLabel() {
        return label.get();
    }

    public void setMeta(EdgeMeta other) {
        this.from.set(other.from.get());
        this.to.set(other.to.get());
        this.label.set(other.label.get());
    }

    public String pp(String delimiter) {
        StringBuilder str = new StringBuilder();

        str.append(from.get());
        str.append(delimiter);
        str.append(to.get());
        str.append(delimiter);
        str.append(label.get());

        return str.toString();
    }

    @Override
    public String toString() {
        return pp(",");
    }

    @Override
    public void write(DataOutput out) throws IOException {
        from.write(out);
        to.write(out);
        label.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        from.readFields(in);
        to.readFields(in);
        label.readFields(in);
    }
}
