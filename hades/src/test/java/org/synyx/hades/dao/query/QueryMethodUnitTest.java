package org.synyx.hades.dao.query;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.synyx.hades.dao.query.QueryLookupStrategy.*;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link QueryMethod}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryMethodUnitTest {

    private static final Class<?> DOMAIN_CLASS = User.class;

    private Method daoMethod;
    private EntityManager em;

    private static final String METHOD_NAME = "findByFirstname";
    private Method invalidReturnType;
    private Method pageableAndSort;
    private Method pageableTwice;
    private Method sortableTwice;
    private Method modifyingMethod;


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        daoMethod = UserDao.class.getMethod("findByLastname", String.class);
        em = createNiceMock(EntityManager.class);

        invalidReturnType =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Pageable.class);
        pageableAndSort =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Pageable.class, Sort.class);
        pageableTwice =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Pageable.class, Pageable.class);

        sortableTwice =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Sort.class, Sort.class);
        modifyingMethod =
                UserDao.class.getMethod("renameAllUsersTo", String.class);
    }


    @Test
    public void testname() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, CREATE);

        assertEquals("User.findByLastname", method.getNamedQueryName());
        assertTrue(method.isCollectionQuery());
        assertEquals("select x from User x where x.lastname = ?",
                new QueryCreator(method).constructQuery());
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoMethod() throws Exception {

        new QueryMethod(null, DOMAIN_CLASS, em);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDomainClass() throws Exception {

        new QueryMethod(daoMethod, null, em, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullEntityManager() throws Exception {

        new QueryMethod(daoMethod, DOMAIN_CLASS, null);
    }


    @Test
    public void returnsCorrectName() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, CREATE);

        assertEquals(daoMethod.getName(), method.getName());
    }


    @Test
    public void determinesValidFieldsCorrectly() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, CREATE);

        assertTrue(method.isValidField("firstname"));
        assertTrue(method.isValidField("Firstname"));
        assertFalse(method.isValidField("address"));
    }


    @Test
    public void returnsQueryAnnotationIfAvailable() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, CREATE);

        assertNull(method.getQueryAnnotation());

        Method daoMethod =
                UserDao.class.getMethod("findByHadesQuery", String.class);

        assertNotNull(new QueryMethod(daoMethod, DOMAIN_CLASS, em)
                .getQueryAnnotation());
    }


    @Test
    public void returnsCorrectDomainClassName() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, CREATE);

        assertEquals(DOMAIN_CLASS.getSimpleName(), method.getDomainClassName());
    }


    @Test
    public void returnsCorrectNumberOfParameters() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, CREATE);

        assertTrue(method.isCorrectNumberOfParameters(daoMethod
                .getParameterTypes().length));
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidReturntypeOnPagebleFinder() throws Exception {

        new QueryMethod(invalidReturnType, DOMAIN_CLASS, em);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsPageableAndSortInFinderMethod() throws Exception {

        new QueryMethod(pageableAndSort, DOMAIN_CLASS, em);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsTwoPageableParameters() throws Exception {

        new QueryMethod(pageableTwice, DOMAIN_CLASS, em);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsTwoSortableParameters() throws Exception {

        new QueryMethod(sortableTwice, DOMAIN_CLASS, em);
    }


    @Test
    public void recognizesModifyingMethod() throws Exception {

        QueryMethod method = new QueryMethod(modifyingMethod, DOMAIN_CLASS, em);
        assertTrue(method.isModifyingQuery());
    }

    /**
     * Interface to define invalid DAO methods for testing.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private static interface InvalidDao {

        // Invalid return type
        User findByFirstname(String firstname, Pageable pageable);


        // Should not use Pageable *and* Sort
        Page<User> findByFirstname(String firstname, Pageable pageable,
                Sort sort);


        // Must not use two Pageables
        Page<User> findByFirstname(String firstname, Pageable first,
                Pageable second);


        // Must not use two Pageables
        Page<User> findByFirstname(String firstname, Sort first, Sort second);
    }
}