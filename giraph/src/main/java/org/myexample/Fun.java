package org.myexample;

import org.algos.msbfs.MSBfs;
import org.apache.giraph.GiraphRunner;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.hadoop.util.ToolRunner;

public class Fun {

    public static void main(String[] args) throws Exception {

        GiraphConfiguration conf = new GiraphConfiguration();
        GiraphRunner runner = new GiraphRunner();
        String outputPath = System.getProperty("giraph.log.dir");

        runner.setConf(conf);
        System.exit(ToolRunner.run(runner, new String[]{
                MSBfs.class.getName(),
                "-vip", "src/main/resources/giraph/tiny_graph.txt",
                "-vif", "org.apache.giraph.io.formats.JsonLongDoubleFloatDoubleVertexInputFormat",
                "-vof", "org.apache.giraph.io.formats.IdWithValueTextOutputFormat",
                "-op", outputPath + "/res",
                "-w", "1",
                "-ca", "mapred.job.tracker=local",
                "-ca", "giraph.SplitMasterWorker=false",
                "-ca", "giraph.useSuperstepCounters=false",
                "-ca", "mapreduce.joboutput.outputformat.overwrite=true"}));
//                "-wc", "worker.ctx.class.getName()"
//                "-cf", "/path/with/bfs-start-nodes"
    }
}
