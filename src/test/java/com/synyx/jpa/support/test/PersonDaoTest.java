package com.synyx.jpa.support.test;

/**
 * Integration test class for <code>PersonDao</code> using standard Spring
 * beans configuration.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public class PersonDaoTest extends AbstractPersonDaoTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "applicationContext.xml" };
    }
}