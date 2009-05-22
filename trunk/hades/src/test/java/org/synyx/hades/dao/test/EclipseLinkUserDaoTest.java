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

package org.synyx.hades.dao.test;

import org.springframework.test.context.ContextConfiguration;


/**
 * Custom {@link UserDaoIntegrationTest} to work with EclipseLink. Unfortunately
 * we have to open the tested constraints a little as EclipseLink does not allow
 * to prevent cascading.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@ContextConfiguration(locations = "classpath:eclipselink.xml")
public class EclipseLinkUserDaoTest extends UserDaoIntegrationTest {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.test.UserDaoTest#testMergingDoesNotCascadeRoles()
     */
    @Override
    public void testMergingDoesNotCascadeRoles() {

        // We have to skip this test as EclipseLink silently cascades, although
        // we have not configured it <- FOO
    }
}
