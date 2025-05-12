package org.algos.msbfs;

import org.apache.giraph.utils.ArrayWritable;
import org.apache.hadoop.io.LongWritable;

public class MSBfsMessage extends ArrayWritable<LongWritable> {

    MSBfsMessage(
            Class<LongWritable> valueClass,
            LongWritable[] values,
            LongWritable senderId
    ) {
        super(valueClass, values);

        LongWritable[] _values = new LongWritable[values.length + 1];
        System.arraycopy(values, 0, _values, 0, values.length);
        _values[values.length] = senderId;

        this.set(_values);
    }

    @Override
    public LongWritable[] get() {
        LongWritable[] _values = super.get();
        LongWritable[] values = new LongWritable[_values.length - 1];
        System.arraycopy(_values, 0, values, 0, values.length);

        return values;
    }

    public LongWritable getSenderId() {
        LongWritable[] _values = super.get();
        return _values[_values.length - 1];
    }
}
