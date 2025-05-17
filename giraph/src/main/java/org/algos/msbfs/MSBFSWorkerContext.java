package org.algos.msbfs;

import com.google.common.collect.ImmutableSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Set;
import org.apache.giraph.worker.WorkerContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 * Algorithm execution configuration:
 *
 * <p>1. To specify source vertices file (file will be available via DistributedCache):
 *
 * <pre>{@code
 * -cf /path/to/source_nodes.txt
 * }</pre>
 *
 * <p>2. To use custom worker context:
 *
 * <pre>{@code
 * -wc org.algos.msbfs.MSBFSWorkerContext
 * }</pre>
 *
 * <p>3. /path/to/source_nodes.txt format:
 *
 * <pre>{@code
 * start_id1
 * start_id2
 * start_id3
 * ...
 * }</pre>
 *
 * <p>Implementation reference:
 *
 * @see <a
 *     href="https://github.com/apache/giraph/blob/trunk/giraph-examples/src/main/java/org/apache/giraph/examples/RandomWalkWorkerContext.java">
 *     RandomWalkWorkerContext (reference implementation) </a>
 */
public class MSBFSWorkerContext extends WorkerContext {

  private static Set<Long> SOURCES;

  private static final Logger LOG = Logger.getLogger(MSBFSWorkerContext.class);

  public boolean isSource(long id) {
    return SOURCES.contains(id);
  }

  public int numSources() {
    return SOURCES.size();
  }

  @Override
  public void preApplication() {
    SOURCES = initializeSources(getContext().getConfiguration());
  }

  private ImmutableSet<Long> initializeSources(Configuration configuration) {
    ImmutableSet.Builder<Long> builder = ImmutableSet.builder();

    Path sourceFile = null;
    try {

      Path[] cacheFiles = DistributedCache.getLocalCacheFiles(configuration);
      if (cacheFiles == null || cacheFiles.length == 0) {
        throw new RuntimeException("can't initialize WorkerContext");
      }

      sourceFile = cacheFiles[0];
      FileSystem fs = FileSystem.getLocal(configuration);
      BufferedReader in =
          new BufferedReader(new InputStreamReader(fs.open(sourceFile), Charset.defaultCharset()));
      String line;
      while ((line = in.readLine()) != null) {
        builder.add(Long.parseLong(line));
      }
      in.close();
    } catch (Exception e) {
      getContext().setStatus("Could not load local cache files: " + sourceFile);
      LOG.error("Could not load local cache files: " + sourceFile, e);
    }

    return builder.build();
  }

  @Override
  public void preSuperstep() {}

  @Override
  public void postSuperstep() {}

  @Override
  public void postApplication() {}
}
