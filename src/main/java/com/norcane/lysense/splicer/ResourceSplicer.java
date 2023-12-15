package com.norcane.lysense.splicer;

import com.norcane.lysense.resource.WritableResource;

/**
 * <i>Splicer</i> allows to splice (e.g. cut a piece off, append) content of given {@link WritableResource} with the content defined by the {@link Operation}.
 * This operation is <b>mutable</b> and the content of the original resource is modified.
 *
 * @see WritableResource
 * @see Operation
 */
public interface ResourceSplicer {

    /**
     * Splices the content of given {@link WritableResource} with the content defined by the {@link Operation}.
     *
     * @param resource  resource to be spliced
     * @param operation splicing operation
     */
    void splice(WritableResource resource, Operation operation);
}
