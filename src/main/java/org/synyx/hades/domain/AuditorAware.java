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

package org.synyx.hades.domain;

import java.io.Serializable;



/**
 * Interface for components that are aware of the application's current auditor.
 * This will be some kind of user mostly.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of the auditing instance
 * @param <PK> the type of the auditing instance's identifier
 */
public interface AuditorAware<T extends Persistable<PK>, PK extends Serializable> {

    /**
     * Returns the current auditor of the application.
     * 
     * @return the current auditor
     */
    T getCurrentAuditor();
}
