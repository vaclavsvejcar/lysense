package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;

import java.util.List;
import java.util.function.Predicate;

/**
 * Extends the capabilities of {@link ResourceFactory} by adding support for loading and filtering multiple resources specified by the <i>GLOB pattern</i>.
 */
public interface IterableResourceFactory extends ResourceFactory {

    /**
     * Loads multiple resources defined by the <i>GLOB pattern</i> and then also eventually filters them using the provided predicate. Note that this operation
     * might not be supported by the concrete implementation, in such case {@link UnsupportedOperationException} will be thrown.
     *
     * @param locationGlobPattern resource(s) location as <i>GLOB pattern</i>
     * @param filter              filter to filter resources
     * @return loaded and filtered resources
     * @throws UnsupportedOperationException if current implementation doesn't support this operation
     */
    List<Resource> resources(String locationGlobPattern, Predicate<Resource> filter);
}
