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
package org.synyx.hades.dao.orm;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.ClassUtils;


/**
 * A
 * {@link org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor}
 * implementing {@code #predictBeanType(Class, String)} to return the configured
 * DAO interface from {@link GenericDaoFactoryBean}s. This is done as shortcut
 * to prevent the need of instantiating {@link GenericDaoFactoryBean}s just to
 * find out what DAO interface they actually create.
 * 
 * @author Oliver Gierke
 */
class DaoInterfaceAwareBeanPostProcessor extends
        InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    private ConfigurableListableBeanFactory context;


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org
     * .springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) {

        if (beanFactory instanceof ConfigurableListableBeanFactory) {

            this.context = (ConfigurableListableBeanFactory) beanFactory;
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.config.
     * InstantiationAwareBeanPostProcessorAdapter
     * #predictBeanType(java.lang.Class, java.lang.String)
     */
    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {

        if (null == context
                || !GenericDaoFactoryBean.class.isAssignableFrom(beanClass)) {
            return null;
        }

        BeanDefinition definition = context.getBeanDefinition(beanName);
        PropertyValue value =
                definition.getPropertyValues().getPropertyValue("daoInterface");

        return getClassForPropertyValue(value);
    }


    /**
     * Returns the class which is configured in the given {@link PropertyValue}.
     * In case it is not a {@link TypedStringValue} or the value contained
     * cannot be interpreted as {@link Class} it will return null.
     * 
     * @param propertyValue
     * @return
     */
    private Class<?> getClassForPropertyValue(PropertyValue propertyValue) {

        Object value = propertyValue.getValue();
        String className = null;

        if (value instanceof TypedStringValue) {
            className = ((TypedStringValue) value).getValue();
        } else if (value instanceof String) {
            className = (String) value;
        } else if (value instanceof Class<?>) {
            return (Class<?>) value;
        } else {
            return null;
        }

        try {
            return ClassUtils.resolveClassName(className,
                    DaoInterfaceAwareBeanPostProcessor.class.getClassLoader());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
