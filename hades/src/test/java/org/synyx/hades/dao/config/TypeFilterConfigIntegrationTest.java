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

import static org.junit.Assert.*;

import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test to test
 * {@link org.springframework.core.type.filter.TypeFilter} integration into
 * namespace.
 * 
 * @author Oliver Gierke
 */
@ContextConfiguration(locations = "classpath:namespace-autoconfig-typefilter-context.xml")
public class TypeFilterConfigIntegrationTest extends
        AbstractDaoConfigIntegrationTest {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.synyx.hades.dao.config.AbstractDaoConfigIntegrationTest#
     * testContextCreation()
     */
    @Override
    public void testContextCreation() {

        assertNotNull(userDao);
        assertNotNull(roleDao);
        assertNull(auditableUserDao);
    }
}
