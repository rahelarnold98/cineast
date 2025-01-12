package org.vitrivr.cineast.standalone.cli;

import com.github.rvesse.airline.annotations.Command;
import org.vitrivr.cineast.core.db.DataSource;
import org.vitrivr.cineast.core.db.cottontaildb.CottontailWrapper;
import org.vitrivr.cineast.standalone.config.Config;
import org.vitrivr.cottontail.client.language.ddl.ListEntities;
import org.vitrivr.cottontail.client.language.ddl.OptimizeEntity;

@Command(name = "optimize", description = "Optimize all entities for the Cineast schema. This command is only compatible with the Cottontail DB database.")
public class OptimizeEntitiesCommand implements Runnable {

  @Override
  public void run() {
    optimizeAllCottontailEntities();
  }

  public static void optimizeAllCottontailEntities() {
    if (Config.sharedConfig().getDatabase().getSelector() != DataSource.COTTONTAIL || Config.sharedConfig().getDatabase().getWriter() != DataSource.COTTONTAIL) {
      System.err.println("Cottontail DB is not both selector & writer in the config. exiting");
      return;
    }
    try (final CottontailWrapper wrapper = new CottontailWrapper(Config.sharedConfig().getDatabase().getHost(), Config.sharedConfig().getDatabase().getPort())) {
      System.out.println("Optimizing all entities for schema '" + CottontailWrapper.CINEAST_SCHEMA + "' in Cottontail");
      wrapper.client.list(new ListEntities(CottontailWrapper.CINEAST_SCHEMA)).forEachRemaining(entity -> {
        System.out.println("Optimizing entity " + entity);
        final String name = entity.asString("dbo").replace("warren.", "");
        wrapper.client.optimize(new OptimizeEntity(name));
      });
      System.out.println("Finished optimizing all entities");
    }
  }
}
