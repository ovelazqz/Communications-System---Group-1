package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import Common.User;
import Common.USER_ROLE;
import Common.UserCollection;

class TestingUserCollection {

    @Test
    void addOrModifyUser_addsNewUserAndCanRetrieve() {
        UserCollection uc = new UserCollection();
        int startCount = uc.getNumUsers();

        uc.addOrModifyUser("AaronCasas", "CS401", USER_ROLE.EMPLOYEE);

        assertEquals(startCount + 1, uc.getNumUsers());

        User u = uc.getUser("AaronCasas");
        assertNotNull(u);
        assertEquals("AaronCasas", u.getUsername());
        assertEquals("CS401", u.getPassword());
        assertEquals(USER_ROLE.EMPLOYEE, u.getRole());
    }

    @Test
    void addOrModifyUser_modifiesExistingUser() {
        UserCollection uc = new UserCollection();

        uc.addOrModifyUser("BrianHa", "CS401", USER_ROLE.EMPLOYEE);
        User before = uc.getUser("BrianHa");
        assertNotNull(before);
        String uniqueIdBefore = before.getUniqueID();

        // modify same username with new password and role
        uc.addOrModifyUser("BrianHa", "Cs401!", USER_ROLE.IT_ADMINISTRATOR);

        User after = uc.getUser("BrianHa");
        assertNotNull(after);
        assertEquals(uniqueIdBefore, after.getUniqueID());   // same user
        assertEquals("Cs401!", after.getPassword());
        assertEquals(USER_ROLE.IT_ADMINISTRATOR, after.getRole());
    }

    @Test
    void removeUser_removesUserAndDecrementsCount() {
        UserCollection uc = new UserCollection();

        uc.addOrModifyUser("CarlosMartin", "CS401", USER_ROLE.EMPLOYEE);
        int countAfterAdd = uc.getNumUsers();

        uc.removeUser("CarlosMartin");

        assertEquals(countAfterAdd - 1, uc.getNumUsers());
        assertNull(uc.getUser("CarlosMartin"));
    }

    @Test
    void getUser_returnsNullForUnknownUser() {
        UserCollection uc = new UserCollection();

        assertNull(uc.getUser("nonexistent_user_123"));
    }

    @Test
    void getAllUsernames_containsAddedUsers() {
        UserCollection uc = new UserCollection();

        uc.addOrModifyUser("DianaCampos", "CS401", USER_ROLE.EMPLOYEE);
        uc.addOrModifyUser("ChristopherSmith", "Cs401!", USER_ROLE.IT_ADMINISTRATOR);

        String[] names = uc.getAllUsernames();
        List<String> nameList = Arrays.asList(names);

        assertTrue(nameList.contains("DianaCampos"));
        assertTrue(nameList.contains("ChristopherSmith"));
    }

    @Test
    void isValidUsername_checksForNullAndEmpty() {
        UserCollection uc = new UserCollection();

        assertFalse(uc.isValidUsername(null));
        assertFalse(uc.isValidUsername(""));
        assertFalse(uc.isValidUsername("   "));

        assertTrue(uc.isValidUsername("validUser"));
    }

    @Test
    void getUserByID_returnsUserWhenIdMatches() {
        UserCollection uc = new UserCollection();

        uc.addOrModifyUser("GiaLy", "CS401", USER_ROLE.EMPLOYEE);
        User u = uc.getUser("GiaLy");
        assertNotNull(u);

        String id = u.getUniqueID();
        User byId = uc.getUserByID(id);

        assertNotNull(byId);
        assertEquals("GiaLy", byId.getUsername());
    }
}
