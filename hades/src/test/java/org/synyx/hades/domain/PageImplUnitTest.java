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

package org.synyx.hades.domain;

import static org.synyx.hades.domain.UnitTestUtils.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.synyx.hades.domain.PageImpl;
import org.synyx.hades.domain.PageRequest;
import org.synyx.hades.domain.Pageable;


/**
 * Unit test for {@link PageImpl}.
 * 
 * @author Oliver Gierke
 */
public class PageImplUnitTest {

    @Test
    public void assertEqualsForSimpleSetup() throws Exception {

        PageImpl<String> page = new PageImpl<String>(Arrays.asList("Foo"));

        assertEqualsAndHashcode(page, page);
        assertEqualsAndHashcode(page,
                new PageImpl<String>(Arrays.asList("Foo")));
    }


    @Test
    public void assertEqualsForComplexSetup() throws Exception {

        Pageable pageable = new PageRequest(0, 10);
        List<String> content = Arrays.asList("Foo");

        PageImpl<String> page = new PageImpl<String>(content, pageable, 100);

        assertEqualsAndHashcode(page, page);

        assertEqualsAndHashcode(page, new PageImpl<String>(content, pageable,
                100));

        assertNotEqualsAndHashcode(page, new PageImpl<String>(content,
                pageable, 90));

        assertNotEqualsAndHashcode(page, new PageImpl<String>(content,
                new PageRequest(1, 10), 100));

        assertNotEqualsAndHashcode(page, new PageImpl<String>(content,
                new PageRequest(0, 15), 100));
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullContentForSimpleSetup() throws Exception {

        new PageImpl<Object>(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullContentForAdvancedSetup() throws Exception {

        new PageImpl<Object>(null, null, 0);
    }
}
