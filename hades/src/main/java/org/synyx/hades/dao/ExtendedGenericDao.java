/*
 * Copyright 2008-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.synyx.hades.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;


/**
 * Interface for a more sophisticated DAO implementation. Mostly the
 * functionality declared here will require an implementation that is based of
 * some proprietary features of certain JPA providers.
 * 
 * @author Oliver Gierke
 */
public interface ExtendedGenericDao<T, PK extends Serializable> extends
        GenericDao<T, PK> {

    /**
     * Returns all entities matching the given examples. If you provide more
     * than one example their restrictions will be OR concatenated. If you
     * provide no example at all or {@code null}, the call returns the same
     * entities as {@code GenericDao#readAll()}.
     * 
     * @param examples
     * @return all objects meeting the criterias expressed by the given examples
     */
    List<T> readByExample(final T... examples);


    /**
     * Returns all entites matching the given criteria sorted by the given sort
     * options.
     * 
     * @see ExtendedGenericDao#readByExample(Persistable...)
     * @param sort
     * @param examples
     * @return all entites matching the given criteria sorted by the given sort
     *         options
     */
    List<T> readByExample(final Collection<T> examples);


    /**
     * Returns all entites matching the given criteria sorted by the given sort
     * options.
     * 
     * @see ExtendedGenericDao#readByExample(Persistable...)
     * @param sort
     * @param examples
     * @return all entites matching the given criteria sorted by the given sort
     *         options
     */
    List<T> readByExample(final Sort sort, final T... examples);


    /**
     * Returns all entites matching the given criteria sorted by the given sort
     * options.
     * 
     * @see ExtendedGenericDao#readByExample(Persistable...)
     * @param sort
     * @param examples
     * @return all entites matching the given criteria sorted by the given sort
     *         options
     */
    List<T> readByExample(final Sort sort, final Collection<T> examples);


    /**
     * Allows pageable access to all entities matching the given examples. If
     * you provide {@code null} for the pageable, the call is identical to
     * {@code ExtendedGenericDao#readByExample(T...)}.
     * 
     * @param pageable
     * @param examples
     * @return the page of objects meeting the example's criterias
     */
    Page<T> readByExample(final Pageable pageable, final T... examples);


    /**
     * Allows pageable access to all entities matching the given examples. If
     * you provide {@code null} for the pageable, the call is identical to
     * {@code ExtendedGenericDao#readByExample(T...)}.
     * 
     * @param pageable
     * @param examples
     * @return the page of objects meeting the example's criterias
     */
    Page<T> readByExample(final Pageable pageable, final Collection<T> examples);


    /**
     * Deletes all entities mathing the given examples.
     * 
     * @param examples
     */
    void deleteByExample(final T... examples);


    /**
     * Deletes all entities mathing the given examples.
     * 
     * @param examples
     */
    void deleteByExample(final Collection<T> examples);
}
