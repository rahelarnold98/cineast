package org.vitrivr.cineast.standalone.cli;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.vitrivr.cineast.core.config.DatabaseConfig;
import org.vitrivr.cineast.core.data.entities.MediaObjectMetadataDescriptor;
import org.vitrivr.cineast.core.db.dao.reader.MediaObjectMetadataReader;

import org.vitrivr.cineast.standalone.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * A CLI command that can be used to lookup metadata for one or many media objects.
 *
 * @author Ralph Gasser
 * @version 1.0
 */
@Command(name = "metadata", description = "Performs a metadata lookup for the media objects specified.")
public class MetadataCli extends CineastCli {
    @Option(name = { "-o", "--objectid" }, title = "Object ID", description = "The ID of the object to lookup metadata for.")
    private String objectId;

    public void run() {
        super.loadConfig();
        final List<String> objectIds = new ArrayList<>(1);
        objectIds.add(this.objectId);
        final DatabaseConfig config = Config.sharedConfig().getDatabase();
        final Ordering<MediaObjectMetadataDescriptor> ordering = Ordering.explicit(objectIds).onResultOf(MediaObjectMetadataDescriptor::getObjectId);
        try (final MediaObjectMetadataReader r = new MediaObjectMetadataReader(config.getSelectorSupplier().get())) {
            List<MediaObjectMetadataDescriptor> descriptors = r.lookupMultimediaMetadata(objectIds);
            descriptors.sort(ordering);
            descriptors.forEach(System.out::println);
        }
    }
}
