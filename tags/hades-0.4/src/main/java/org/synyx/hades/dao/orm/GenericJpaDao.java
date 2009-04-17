/*
 * Copyright 2002-2008 the original author or authors.
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

package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.support.PageImpl;


/**
 * Default implementation of the <code>GenericDao</code> interface. Use
 * <code>GenericDaoFactoryBean</code> to create instances of it. Furthermore it
 * is able to execute named queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 * @param <T> the type of the entity to handle
 * @param <PK> the type of the entity's identifier
 */
@Repository
public class GenericJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends AbstractJpaFinder<T> implements GenericDao<T, PK> {

    private static final String COUNT_QUERY_STRING =
            "select count(x) from %s x";

    private static final String DELETE_ALL_QUERY_STRING = "delete from %s x";


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#delete(java.lang.Object)
     */
    public void delete(final T entity) {

        getEntityManager().remove(entity);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#deleteAll()
     */
    public void deleteAll() {

        getEntityManager().createQuery(
                String.format(DELETE_ALL_QUERY_STRING, getDomainClass()
                        .getSimpleName())).executeUpdate();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.synyx.jpa.support.GenericDao#readByPrimaryKey(java.io.Serializable)
     */
    public T readByPrimaryKey(final PK primaryKey) {

        Assert.notNull(primaryKey, "The given primaryKey must not be null!");

        return getEntityManager().find(getDomainClass(), primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#exists(java.io.Serializable)
     */
    public boolean exists(final PK primaryKey) {

        Assert.notNull(primaryKey, "The given primary key must not be null!");

        return null != readByPrimaryKey(primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll() {

        return getReadAllQuery().getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#readAll(org.synyx.hades.domain.Sort)
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll(final Sort sort) {

        Query query =
                getEntityManager().createQuery(
                        applySorting(getReadAllQueryString(), sort));

        return (null == sort) ? readAll() : query.getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.dao.GenericDao#readAll(org.synyx.hades.hades.dao
     * .Pageable)
     */
    public Page<T> readAll(final Pageable pageable) {

        if (null == pageable) {

            return new PageImpl<T>(readAll());
        }

        return readPage(pageable, getReadAllQueryString());
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#count()
     */
    public Long count() {

        return (Long) getEntityManager().createQuery(
                String.format(COUNT_QUERY_STRING, getDomainClass()
                        .getSimpleName())).getSingleResult();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#save(java.lang.Object)
     */
    public T save(final T entity) {

        if (entity.isNew()) {
            getEntityManager().persist(entity);
        } else {
            getEntityManager().merge(entity);
        }

        return entity;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.jpa.support.GenericDao#saveAndFlush(org.synyx.hades
     * .hades.jpa.support.Entity)
     */
    public T saveAndFlush(final T entity) {

        T result = save(entity);
        flush();

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#saveAll(java.util.List)
     */
    public List<T> saveAll(List<T> entities) {

        List<T> result = new ArrayList<T>();

        for (T entity : entities) {
            result.add(save(entity));
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#flush()
     */
    public void flush() {

        getEntityManager().flush();
    }


    /**
     * Reads a page of entities for the given JPQL query.
     * 
     * @param pageable
     * @param query
     * @return a page of entities for the given JPQL query
     */
    @SuppressWarnings("unchecked")
    protected Page<T> readPage(final Pageable pageable, final String query) {

        applySorting(query, pageable.getSort());

        Query jpaQuery =
                getEntityManager().createQuery(
                        applySorting(query, pageable.getSort()));

        jpaQuery.setFirstResult(pageable.getFirstItem());
        jpaQuery.setMaxResults(pageable.getPageSize());

        return new PageImpl<T>(jpaQuery.getResultList(), pageable, count());
    }


    /**
     * Returns the query string to delete all entities.
     * 
     * @return
     */
    protected String getDeleteAllQueryString() {

        return DELETE_ALL_QUERY_STRING;
    }


    /**
     * Adds {@literal order by} clause to the JPQL query.
     * 
     * @param query
     * @param sort
     * @return
     */
    private String applySorting(String query, Sort sort) {

        if (null == sort) {
            return query;
        }

        StringBuilder builder = new StringBuilder(query);
        builder.append(" order by");

        for (String property : sort.getProperties()) {
            builder.append(" x.");
            builder.append(property);
            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);

        builder.append(" ");
        builder.append(sort.getOrder().getJpaValue());

        return builder.toString();
    }
}