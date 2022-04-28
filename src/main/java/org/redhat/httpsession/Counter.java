package org.redhat.httpsession;

import java.io.IOException;
import java.io.Serializable;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.jboss.logging.Logger;

/**
 * This class is a common class that increments a counter and it's store inside
 * HTTP Session in the index.jsp presentation layer. The target is to show how
 * the counter continue to increment its value accross the cluster.
 *
 * @author Mauro Vocale
 * @version 1.0 30/04/2018
 */
public class Counter implements Serializable {

    /**
     * A version number for this class so that serialization can occur without
     * worrying about the underlying class changing between serialization and
     * deserialization.
     */
    private static final long serialVersionUID = 6604436849415136871L;

    /**
     * The Logger instance. All LOG messages from this class are routed through
     * this member. The Logger namespace is the name class.
     */
    public static final Logger LOGGER = Logger.getLogger(Counter.class);

    /**
     * Global counter that simulates the stateful information.
     */
    private int counter = 0;

    /**
     * Public constructor.
     */
    public Counter() {
        LOGGER.info("************************");
        LOGGER.info("Counter is created");
        LOGGER.info("************************");
    }

    /**
     * Method that increments the global counter.
     *
     * @return The updated global counter.
     */
    public int getIncrementedValue() {
        return ++this.counter;
    }

    public String getCachedData() {
        RemoteCacheManager cacheManager = new RemoteCacheManager("hotrod://data-grid.parkers.svc.cluster.local:11222");

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.clustering().cacheMode(CacheMode.REPL_SYNC);
        builder.encoding().mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE);
        builder.expiration().lifespan(600000);
        builder.memory().maxCount(1000);
        //builder.clustering().persistence().addSoftIndexFileStore().dataLocation("/focus-data/data-grid").shared(true);

        Configuration configuration = builder.build();
        RemoteCache<String, String> cache = cacheManager.administration().getOrCreateCache("counter-cache", configuration);

        String value = cache.getOrDefault("value", "Value: ");
        cache.put("value", value + " " + this.counter);
        cacheManager.close();

        return value;
    }

    /**
     * Serialization method.
     *
     * @param out ObjectOutputStream where write informations.
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        LOGGER.info("****************************");
        LOGGER.info("Processing serialization");
        LOGGER.info("Counter = " + this.counter);
        LOGGER.info("****************************");
        out.defaultWriteObject();
    }

    /**
     * Default deserialization method
     *
     * @param in ObjectInputStream from which retrieve the informations.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        LOGGER.info("****************************");
        LOGGER.info("Processing Deserialization");
        LOGGER.info("Counter = " + this.counter);
        LOGGER.info("****************************");
    }
}
