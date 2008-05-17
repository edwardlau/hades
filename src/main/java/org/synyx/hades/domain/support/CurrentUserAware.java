package org.synyx.hades.domain.support;

import java.io.Serializable;

import org.synyx.hades.domain.Persistable;


/**
 * Interface for components that are aware of the application's current user.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface CurrentUserAware<T extends Persistable<PK>, PK extends Serializable> {

    /**
     * Returns the current user of the application.
     * 
     * @return the current user
     */
    public abstract T getCurrentUser();
}