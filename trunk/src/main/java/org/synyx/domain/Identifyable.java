package org.synyx.domain;

import java.io.Serializable;


/**
 * Simple interface for entities.
 * 
 * @author Oliver Gierke
 */
public interface Identifyable<PK extends Serializable> {

    /**
     * Returns the id of the entity.
     * 
     * @return the id
     */
    public PK getId();


    /**
     * Returns if the identifyable is new or was persisted already.
     * 
     * @return if the object is new
     */
    public boolean isNew();
}
