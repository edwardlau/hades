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
package org.synyx.hades.dao.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.synyx.hades.daocustom.UserCustomExtendedDao;


/**
 * Annotation to exclude DAO interfaces from being picked up by Hades and thus
 * in consequence getting an instance being created.
 * <p>
 * This will typically be used when providing an extended base interface for all
 * DAOs in combination with a custom DAO base class to implement methods
 * declared in that intermediate interface. In this case you typically derive
 * your concrete DAO interfaces from the intermediate one but don't want Hades
 * to create a Spring bean for the intermediate interface.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:namespace-customfactory-context.xml")
public class CustomDaoFactoryConfigIntegrationTest {

    @Autowired
    private UserCustomExtendedDao userDao;


    @Test(expected = UnsupportedOperationException.class)
    public void testCustomFactoryUsed() {

        Assert.notNull(userDao);
        userDao.customMethod(1);
    }
}
