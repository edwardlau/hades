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
package org.synyx.hades.domain.auditing;

import org.synyx.hades.domain.User;


/**
 * Stub implementation for {@link AuditorAware}. Returns {@literal null} for the
 * current auditor.
 * 
 * @author Oliver Gierke
 */
public class AuditorAwareStub implements AuditorAware<User> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.auditing.AuditorAware#getCurrentAuditor()
     */
    public User getCurrentAuditor() {

        return null;
    }

}