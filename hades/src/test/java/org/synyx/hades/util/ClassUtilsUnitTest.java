package org.synyx.hades.util;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link ClassUtils}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class ClassUtilsUnitTest {

    @Test
    public void testname() throws Exception {

        assertEquals(User.class, ClassUtils.getDomainClass(UserDao.class));
        assertEquals(User.class, ClassUtils.getDomainClass(SomeDao.class));
    }

    /**
     * Sample interface to serve two purposes:
     * <ol>
     * <li>Check that {@link ClassUtils#getDomainClass(Class)} skips non
     * {@link GenericDao} interfaces</li>
     * <li>Check that {@link ClassUtils#getDomainClass(Class)} traverses
     * interface hierarchy</li>
     * </ol>
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private interface SomeDao extends Serializable, UserDao {

    }
}