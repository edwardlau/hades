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
package org.synyx.hades.dao.query;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link HadesQuery} based on
 * {@link javax.persistence.NamedQuery}s.
 * 
 * @author Oliver Gierke
 */
final class NamedHadesQuery extends AbstractHadesQuery {

    private static final Logger LOG = LoggerFactory
            .getLogger(NamedHadesQuery.class);

    private String queryName;
    private QueryExtractor extractor;


    /**
     * Creates a new {@link NamedHadesQuery}.
     */
    private NamedHadesQuery(QueryMethod method, EntityManager em) {

        super(method, em);

        this.queryName = method.getNamedQueryName();
        this.extractor = method.getQueryExtractor();
        Query query = em.createNamedQuery(queryName);

        // Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=322579
        // until it gets fixed
        if (null != query) {
            query.getHints();
        }
    }


    /**
     * Looks up a named query for the given {@link QueryMethod}.
     * 
     * @param method
     * @return
     */
    public static HadesQuery lookupFrom(QueryMethod method, EntityManager em) {

        final String queryName = method.getNamedQueryName();

        LOG.debug("Looking up named query {}", queryName);

        try {

            HadesQuery query = new NamedHadesQuery(method, em);
            Parameters parameters = method.getParameters();

            if (parameters.hasSortParameter()) {
                throw new IllegalStateException(
                        String.format(
                                "Finder method %s is backed "
                                        + "by a NamedQuery and must "
                                        + "not contain a sort parameter as we "
                                        + "cannot modify the query! Use @Query instead!",
                                method));
            }

            boolean isPaging = parameters.hasPageableParameter();
            boolean cannotExtractQuery =
                    !method.getQueryExtractor().canExtractQuery();

            if (isPaging && cannotExtractQuery) {
                throw QueryCreationException
                        .create(method,
                                "Cannot use Pageable parameter in query methods with your persistence provider!");
            }

            if (parameters.hasPageableParameter()) {
                LOG.info(
                        "Finder method {} is backed by a NamedQuery"
                                + " but contains a Pageble parameter! Sorting deliviered "
                                + "via this Pageable will not be applied!",
                        method);
            }

            return query;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.query.AbstractHadesQuery#createQuery(javax.persistence
     * .EntityManager, org.synyx.hades.dao.query.ParameterBinder)
     */
    @Override
    protected Query createQuery(EntityManager em, ParameterBinder binder) {

        return em.createNamedQuery(queryName);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.query.AbstractHadesQuery#createCountQuery(javax.
     * persistence.EntityManager)
     */
    @Override
    protected Query createCountQuery(EntityManager em) {

        Query query = createQuery(em, null);
        String queryString = extractor.extractQueryString(query);

        return em.createQuery(QueryUtils.createCountQueryFor(queryString));
    }
}