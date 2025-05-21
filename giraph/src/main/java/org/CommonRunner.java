package org;

import java.util.Arrays;
import org.apache.giraph.GiraphRunner;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class CommonRunner {
  private static final Logger LOG = Logger.getLogger(CommonRunner.class);

  public static int run(String compClassName, String[] args) throws Exception {

    GiraphConfiguration conf = new GiraphConfiguration();
    GiraphRunner runner = new GiraphRunner();

    runner.setConf(conf);

    String outputPath = System.getProperty("giraph.output.dir");
    String logLevel = System.getProperty("giraph.log.level");
    String threadN = System.getProperty("giraph.thread.n");
    String metricsEnable = System.getProperty("giraph.metrics.enable");

    String[] base_settings =
        new String[] {
          "-op",
          outputPath + "/res",
          "-w",
          "1",
          "-ca",
          "giraph.numComputeThreads=" + threadN,
          "-ca",
          "giraph.logLevel=" + logLevel,
          "-ca",
          "mapred.job.tracker=local",
          "-ca",
          "giraph.SplitMasterWorker=false",
          "-ca",
          "giraph.useSuperstepCounters=false",
          "-ca",
          "mapreduce.joboutput.outputformat.overwrite=true",
          "-ca",
          "giraph.metrics.enable=" + metricsEnable,
          "-ca",
          "giraph.metrics.directory=" + outputPath + "/metrics",
        };

    String[] settings = new String[args.length + base_settings.length + 1];
    settings[0] = compClassName;
    System.arraycopy(args, 0, settings, 1, args.length);
    System.arraycopy(base_settings, 0, settings, args.length + 1, base_settings.length);

    LOG.info("[SETTINGS]: \n" + Arrays.toString(settings) + "\n");

    return ToolRunner.run(runner, settings);
  }
}
