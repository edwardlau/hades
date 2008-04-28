package org.synyx.hades.dao.test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.Role;
import org.synyx.hades.domain.User;


/**
 * Base integration test class for <code>PersonDao</code>. Extend this class
 * and provide and application context by overriding
 * <code>getConfigLocations()</code>.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public abstract class AbstractUserDaoTest extends AbstractJpaTests {

    // CUT
    private UserDao userDao;

    // Test fixture
    private User firstUser;
    private User secondUser;
    private Integer id;


    /**
     * Setter to inject <code>UserDao</code> instance.
     * 
     * @param userDao
     */
    public void setUserDao(UserDao userDao) {

        this.userDao = userDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception {

        firstUser = new User("Oliver", "Gierke", "gierke@synyx.de");
        secondUser = new User("Joachim", "Arrasz", "arrasz@synyx.de");
    }


    /**
     * Simple test for context creation.
     */
    public void testContextCreation() {

        assertNotNull(getApplicationContext());
    }


    public void testCreation() {

        flushTestUsers();
    }


    /**
     * @throws Exception
     */
    public void testRead() throws Exception {

        flushTestUsers();

        User foundPerson = userDao.readByPrimaryKey(id);
        assertEquals(firstUser.getFirstname(), foundPerson.getFirstname());
    }


    /**
     * Tests updating a person.
     */
    public void testUpdate() {

        flushTestUsers();

        User foundPerson = userDao.readByPrimaryKey(id);
        foundPerson.setLastname("Schlicht");

        User updatedPerson = userDao.readByPrimaryKey(id);
        assertEquals(foundPerson.getFirstname(), updatedPerson.getFirstname());
    }


    /**
     * Test
     */
    public void testDelete() {

        flushTestUsers();

        userDao.delete(firstUser);
        assertNull(userDao.readByPrimaryKey(id));
    }


    /**
     * Tests, that searching by the email address of the reference user returns
     * exactly that instance.
     * 
     * @throws Exception
     */
    public void testFindByName() throws Exception {

        flushTestUsers();

        List<User> byName = userDao.findByEmailAddress("gierke@synyx.de");

        assertTrue(byName.size() == 1);
        assertEquals(firstUser, byName.get(0));
    }


    public void testReadAll() {

        flushTestUsers();

        List<User> reference = Arrays.asList(firstUser, secondUser);
        assertTrue(userDao.readAll().containsAll(reference));
    }


    public void testCascadesPersisting() {

        // Create link prior to persisting
        firstUser.addColleague(secondUser);

        // Persist
        flushTestUsers();

        // Fetches first user from .. bdatabase
        User firstReferenceUser = userDao.readByPrimaryKey(firstUser.getId());
        assertEquals(firstUser, firstReferenceUser);

        // Fetch colleagues and assert link
        Set<User> colleagues = firstReferenceUser.getColleagues();
        assertEquals(1, colleagues.size());
        assertTrue(colleagues.contains(secondUser));
    }


    /**
     * Tests, that persisting a relationsship without cascade attributes throws
     * a <code>DataAccessException</code>.
     */
    public void testPreventsCascadingRolePersisting() {

        firstUser.addRole(new Role("USER"));

        try {
            flushTestUsers();
            fail("Expected DataAccessException!");
        } catch (DataAccessException e) {

        }
    }


    public void testMergingCascadesCollegueas() {

        firstUser.addColleague(secondUser);
        flushTestUsers();

        firstUser.addColleague(new User("Florian", "Hopf", "hopf@synyx.de"));
        userDao.save(firstUser);

        User reference = userDao.readByPrimaryKey(firstUser.getId());
        Set<User> colleagues = reference.getColleagues();

        assertNotNull(colleagues);
        assertEquals(2, colleagues.size());
    }


    public void testMergingDoesNotCascadeRoles() {

        flushTestUsers();

        firstUser.addRole(new Role("USER"));

        try {
            userDao.saveAndFlush(firstUser);
            fail("Expected DataAccessException!");
        } catch (DataAccessException e) {

        }
    }


    /**
     * Tests, that the generic dao implements count correctly.
     */
    public void testCountsCorrectly() {

        Long count = userDao.count();

        User user = new User();
        user.setEmailAddress("gierke@synyx.de");
        userDao.save(user);

        assertTrue(userDao.count().equals(count + 1));
    }


    /**
     * Flushes test users to the database.
     */
    private void flushTestUsers() {

        userDao.save(firstUser);
        userDao.save(secondUser);

        userDao.flush();

        id = firstUser.getId();

        assertNotNull(id);
        assertNotNull(secondUser.getId());
    }
}