/*
 * Copyright 2008-2010 the original author or authors.
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

package org.synyx.hades.dao.config;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;


/**
 * Captures configuration for a single DAO. Delegates property lookup to parent
 * {@code dao-config} element (represented by a {@code DaoConfigContext}) if no
 * value found on the {@code dao} element. This way {@code dao} declarations can
 * override parent element's configuration.
 * 
 * @author Oliver Gierke
 */
class DaoContext extends DaoConfigContext {

    private static final String CUSTOM_IMPL_REF = "custom-impl-ref";

    private final DaoConfigContext parent;
    private final String id;


    /**
     * Creates a new instance of {@code DaoContext}. Typically used on manual
     * configuration.
     */
    public DaoContext(Element element, DaoConfigContext parent) {

        super(element);
        this.parent = parent;
        this.id = element.getAttribute("id");
    }


    /**
     * Creates a new instance of {@code DaoContext}. This constructor is used
     * during auto configuration, thus only parent element configuration is
     * regarded.
     * 
     * @param id
     * @param parent
     */
    public DaoContext(String id, DaoConfigContext parent) {

        super(parent.getElement());
        this.parent = parent;
        this.id = id;
    }


    /**
     * Creates a {@link DaoContext} from the given DAO interface name. Derives
     * DAO id, as well as the package name to use from the given interface.
     * 
     * @param interfaceName
     * @param parent
     * @return
     */
    public static DaoContext fromInterfaceName(String interfaceName,
            DaoConfigContext parent) {

        String shortName = ClassUtils.getShortName(interfaceName);

        String id = StringUtils.uncapitalize(shortName);

        final String packageName =
                interfaceName.substring(0,
                        interfaceName.lastIndexOf(shortName) - 1);

        return new DaoContext(id, parent) {

            /*
             * (non-Javadoc)
             * 
             * @see org.synyx.hades.dao.config.DaoContext#getDaoPackageName()
             */
            @Override
            protected String getDaoBasePackageName() {

                return packageName;
            }
        };
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#validate()
     */
    @Override
    public void validate() {

        Assert.notNull(getId());
    }


    /**
     * Returns the id of the DAO bean registered in the
     * {@code ApplicationContext}.
     * 
     * @return
     */
    public String getId() {

        return this.id;
    }


    /**
     * Returns the bean name to be used for the DAO.
     * 
     * @return
     */
    public String getBeanName() {

        return id;
    }


    /**
     * Returns the name of the DAO interface.
     * 
     * @return
     */
    public String getInterfaceName() {

        return getDaoBasePackageName() + "." + StringUtils.capitalize(id);
    }


    /**
     * Returns the class name of a possible custom DAO implementation class to
     * detect.
     * 
     * @return
     */
    public String getImplementationClassName() {

        return StringUtils.capitalize(id) + getDaoImplPostfix();
    }


    /**
     * Returns the bean name a possibly found custom implementation shall be
     * registered in the {@code ApplicationContext} under.
     * 
     * @return
     */
    public String getImplementationBeanName() {

        return id + getDaoImplPostfix();
    }


    /**
     * Returns if a custom DAO implementation shall be autodetected.
     * 
     * @return
     */
    public boolean autodetectCustomImplementation() {

        return !StringUtils.hasText(getElement().getAttribute(CUSTOM_IMPL_REF));
    }


    /**
     * Returns the bean reference to the custom DAO implementation.
     * 
     * @return
     */
    public String getCustomImplementationRef() {

        return getElement().getAttribute(CUSTOM_IMPL_REF);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoPackageName()
     */
    @Override
    protected String getDaoBasePackageName() {

        String daoPackageName = getElement().getAttribute(DAO_PACKAGE_NAME);
        return StringUtils.hasText(daoPackageName) ? daoPackageName : parent
                .getDaoBasePackageName();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoImplPostfix()
     */
    @Override
    protected String getDaoImplPostfix() {

        String daoImplPostfix = getElement().getAttribute(DAO_IMPL_POSTFIX);
        return StringUtils.hasLength(daoImplPostfix) ? daoImplPostfix : parent
                .getDaoImplPostfix();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoFactoryClassName()
     */
    @Override
    protected String getDaoFactoryClassName() {

        String factoryClassName =
                getElement().getAttribute(DAO_FACTORY_CLASS_NAME);
        return StringUtils.hasText(factoryClassName) ? factoryClassName
                : parent.getDaoFactoryClassName();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getEntityManagerRef()
     */
    @Override
    protected String getEntityManagerFactoryRef() {

        String ref = getElement().getAttribute(ENTITY_MANAGER_FACTORY_REF);
        return StringUtils.hasText(ref) ? ref : parent
                .getEntityManagerFactoryRef();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.config.DaoConfigContext#getTransactionManagerRef()
     */
    @Override
    protected String getTransactionManagerRef() {

        String ref = getElement().getAttribute(TRANSACTION_MANAGER_REF);
        return StringUtils.hasText(ref) ? ref : parent
                .getTransactionManagerRef();
    }
}
