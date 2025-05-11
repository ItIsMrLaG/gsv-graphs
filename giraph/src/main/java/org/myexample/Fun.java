package org.myexample;

import org.apache.giraph.GiraphRunner;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.hadoop.util.ToolRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fun {

    public static void main(String[] args) throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        GiraphConfiguration conf = new GiraphConfiguration();
        GiraphRunner runner = new GiraphRunner();
        runner.setConf(conf);
        System.exit(ToolRunner.run(runner, new String[]{
                GiraphShortestDistance.class.getName(),
                "-vip", "src/main/resources/giraph/tiny_graph.txt",
                "-vif", "org.apache.giraph.io.formats.JsonLongDoubleFloatDoubleVertexInputFormat",
                "-vof", "org.apache.giraph.io.formats.IdWithValueTextOutputFormat",
                "-op", "src/main/resources/giraph/output/" + dateFormat.format(new Date()),
                "-w", "1",
                "-ca", "mapred.job.tracker=local",
                "-ca", "giraph.SplitMasterWorker=false",
                "-ca", "giraph.useSuperstepCounters=false"}));
        /* Two other useful properties when writing complex algorithms */
        //"-ca", "giraph.masterComputeClass=YourMasterClass",
        //"-ca", "giraph.workerContextClass=YourWorkerContextClass",
    }
}
