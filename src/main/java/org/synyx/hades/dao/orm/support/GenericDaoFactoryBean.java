/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.dao.orm.support;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.dao.FinderExecuter;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.AbstractJpaFinder;
import org.synyx.hades.dao.orm.GenericJpaDao;
import org.synyx.hades.dao.orm.QueryLookupStrategy;
import org.synyx.hades.domain.Persistable;


/**
 * Factory bean to create instances of a given DAO interface. Creates a proxy
 * implementing the configured DAO interface and apply an advice handing the
 * control to the {@code FinderExecuter} when a method beginning with "find" is
 * called.
 * <p>
 * E.g. if you define a method {@code findByName} on an interface extending
 * {@code GenericDao<User, Integer>} the advice will try to call a named query
 * named {@code User.findByName}. The advice can distinguish between calls for
 * lists or a single instance by checking the return value of the defined
 * method.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 * @param <D> the base type for the DAO to create
 * @param <T> the type of the entity to handle by the DAO
 * @param <PK> the type of the identifier of the entity
 */
@SuppressWarnings("unchecked")
public class GenericDaoFactoryBean<D extends AbstractJpaFinder<T, PK>, T extends Persistable<PK>, PK extends Serializable>
        implements FactoryBean, InitializingBean {

    public static final Class<?> DEFAULT_DAO_IMPLEMENTATION = GenericJpaDao.class;

    private Class<GenericDao<T, PK>> daoInterface;

    private Class<T> domainClass;
    private Class<D> daoClass = (Class<D>) DEFAULT_DAO_IMPLEMENTATION;
    private Object customDaoImplementation;
    private QueryLookupStrategy createFinderQueries = QueryLookupStrategy.CREATE_IF_NOT_FOUND;
    private String finderPrefix = AbstractJpaFinder.DEFAULT_FINDER_PREFIX;

    private EntityManagerFactory entityManagerFactory;


    /**
     * Setter to inject the dao interface to implement.
     * 
     * @param daoInterface the daoInterface to set
     */
    @Required
    public void setDaoInterface(final Class<GenericDao<T, PK>> daoInterface) {

        Assert.notNull(daoInterface);
        Assert.isAssignable(GenericDao.class, daoInterface,
                "DAO interface has to implement at least GenericDao!");

        this.daoInterface = daoInterface;
    }


    /**
     * Setter to inject a custom DAO implementation. This class needs
     * 
     * @param customDaoImplementation the customDaoImplementation to set
     */
    public void setCustomDaoImplementation(Object customDaoImplementation) {

        this.customDaoImplementation = customDaoImplementation;
    }


    /**
     * Setter to inject the domain class to manage.
     * 
     * @param domainClass the domainClass to set
     */
    @Required
    public void setDomainClass(final Class<T> domainClass) {

        Assert.notNull(domainClass);
        Assert.isAssignable(Persistable.class, domainClass,
                "Domain class has to implement at least Persistable!");

        this.domainClass = domainClass;
    }


    /**
     * Setter to inject a custom DAO base class. If this setter is not called
     * the factory will use {@link GenericJpaDao} as implementation base class
     * as default.
     * 
     * @param daoClass the daoClass to set
     */
    public void setDaoClass(final Class<D> daoClass) {

        Assert.notNull(daoClass);
        Assert.isAssignable(GenericDao.class, daoClass,
                "DAO base class has to implement at least GenericDao!");

        this.daoClass = daoClass;
    }


    /**
     * @param createFinderQueries the createFinderQueries to set
     */
    public void setCreateFinderQueries(QueryLookupStrategy createFinderQueries) {

        this.createFinderQueries = createFinderQueries;
    }


    /**
     * Setter to inject entity manager.
     * 
     * @param entityManagerFactory the entityManagerFactory to set
     */
    @PersistenceUnit
    public void setEntityManagerFactory(
            final EntityManagerFactory entityManagerFactory) {

        this.entityManagerFactory = entityManagerFactory;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {

        // Instantiate generic dao
        D genericJpaDao = daoClass.newInstance();
        genericJpaDao.setDomainClass(domainClass);
        genericJpaDao.setEntityManagerFactory(entityManagerFactory);
        genericJpaDao.setCreateFinderQueries(createFinderQueries);

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(genericJpaDao);
        result.setInterfaces(new Class[] { daoInterface });
        result.addAdvice(new DelegatingMethodInterceptor());

        return result.getProxy();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType() {

        return daoInterface;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {

        return true;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(daoInterface);
        Assert.notNull(domainClass);

        if (isExtendedDaoInterface()
                && !ExtendedGenericDao.class.isAssignableFrom(daoClass)) {
            throw new BeanCreationException(
                    "If you want to create ExtendedGenericDao instances you "
                            + "have to provide an implementation base class that "
                            + "implements this interface!");
        }

        if (null == customDaoImplementation && hasCustomMethod()) {

            throw new BeanCreationException(
                    "You have configured an "
                            + "DAO interface with custom methods but not provided a custom implementation!");
        }

    }


    private boolean hasCustomMethod() {

        boolean hasCustomMethod = false;

        for (Method method : daoInterface.getMethods()) {

            if (method.getName().startsWith(finderPrefix)) {
                continue;
            }

            if (!method.getDeclaringClass().equals(daoInterface)) {
                continue;
            }
            hasCustomMethod = true;
            break;

        }

        return hasCustomMethod;
    }


    /**
     * Returns if the DAO bean to be created shall implement
     * {@link ExtendedGenericDao}.
     * 
     * @return
     */
    private boolean isExtendedDaoInterface() {

        return ExtendedGenericDao.class.isAssignableFrom(daoInterface);
    }

    /**
     * This {@code MethodInterceptor} intercepts calls to methods of the custom
     * implementation and delegates the to it if configured. Furthermore it
     * resolves method calls to finders and triggers execution of them. You can
     * rely on having a custom dao implementation instance set if this returns
     * true.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private class DelegatingMethodInterceptor implements MethodInterceptor {

        /**
         * Returns if the given call is a call to a method of the custom
         * implementation.
         * 
         * @param invocation
         * @return
         */
        private boolean isCallToCustomMethod(MethodInvocation invocation) {

            if (null == customDaoImplementation) {
                return false;
            }

            Class<?> declaringClass = invocation.getMethod()
                    .getDeclaringClass();

            return declaringClass.isInstance(customDaoImplementation);
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
         */
        @SuppressWarnings("unchecked")
        public Object invoke(final MethodInvocation invocation)
                throws Throwable {

            if (isCallToCustomMethod(invocation)) {

                try {
                    return invocation.getMethod().invoke(
                            customDaoImplementation, invocation.getArguments());
                } catch (Exception e) {
                    throw new RuntimeException("Could not invoke "
                            + invocation.toString() + " on "
                            + customDaoImplementation);
                }
            }

            Method method = invocation.getMethod();

            if (method.getName().startsWith(
                    AbstractJpaFinder.DEFAULT_FINDER_PREFIX)) {

                FinderExecuter<T> target = (FinderExecuter<T>) invocation
                        .getThis();

                Class<?> returnType = invocation.getMethod().getReturnType();

                // Execute finder for single object if domain class type is
                // assignable to the methods return type, else finder for a
                // list
                // of objects
                if (ClassUtils.isAssignable(domainClass, returnType)) {
                    return target.executeObjectFinder(method, invocation
                            .getArguments());
                } else {
                    return target.executeFinder(method, invocation
                            .getArguments());
                }
            }

            return invocation.proceed();
        }
    }
}
