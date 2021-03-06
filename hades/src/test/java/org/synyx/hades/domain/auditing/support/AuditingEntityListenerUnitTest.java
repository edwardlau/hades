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
package org.synyx.hades.domain.auditing.support;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.domain.auditing.AuditableUser;
import org.synyx.hades.domain.auditing.AuditorAware;


/**
 * Unit test for {@code AuditionAdvice}.
 * 
 * @author Oliver Gierke
 */
@SuppressWarnings("unchecked")
public class AuditingEntityListenerUnitTest {

    private AuditingEntityListener<AuditableUser> auditionAdvice;
    private AuditorAware<AuditableUser> auditorAware;

    private AuditableUser user;


    @Before
    public void setUp() {

        auditionAdvice = new AuditingEntityListener<AuditableUser>();

        user = new AuditableUser();

        auditorAware = mock(AuditorAware.class);
        when(auditorAware.getCurrentAuditor()).thenReturn(user);
    }


    /**
     * Checks that the advice does not set auditor on the target entity if no
     * {@code AuditorAware} was configured.
     */
    @Test
    public void doesNotSetAuditorIfNotConfigured() {

        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getLastModifiedDate());

        assertNull(user.getCreatedBy());
        assertNull(user.getLastModifiedBy());
    }


    /**
     * Checks that the advice sets the auditor on the target entity if an
     * {@code AuditorAware} was configured.
     */
    @Test
    public void setsAuditorIfConfigured() {

        auditionAdvice.setAuditorAware(auditorAware);

        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getLastModifiedDate());

        assertNotNull(user.getCreatedBy());
        assertNotNull(user.getLastModifiedBy());

        verify(auditorAware).getCurrentAuditor();
    }


    /**
     * Checks that the advice does not set modification information on creation
     * if the falg is set to {@code false}.
     */
    @Test
    public void honoursModifiedOnCreationFlag() {

        auditionAdvice.setAuditorAware(auditorAware);
        auditionAdvice.setModifyOnCreation(false);
        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getCreatedBy());

        assertNull(user.getLastModifiedBy());
        assertNull(user.getLastModifiedDate());

        verify(auditorAware).getCurrentAuditor();
    }


    /**
     * Tests that the advice only sets modification data if a not-new entity is
     * handled.
     */
    @Test
    public void onlySetsModificationDataOnNotNewEntities() {

        user = new AuditableUser(1L);

        auditionAdvice.setAuditorAware(auditorAware);
        auditionAdvice.touch(user);

        assertNull(user.getCreatedBy());
        assertNull(user.getCreatedDate());

        assertNotNull(user.getLastModifiedBy());
        assertNotNull(user.getLastModifiedDate());

        verify(auditorAware).getCurrentAuditor();
    }


    @Test
    public void doesNotSetTimeIfConfigured() throws Exception {

        auditionAdvice.setDateTimeForNow(false);
        auditionAdvice.setAuditorAware(auditorAware);
        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedBy());
        assertNull(user.getCreatedDate());

        assertNotNull(user.getLastModifiedBy());
        assertNull(user.getLastModifiedDate());
    }
}
