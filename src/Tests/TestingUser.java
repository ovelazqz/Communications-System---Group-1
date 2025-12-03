package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Common.USER_ROLE;
import Common.User;

class TestingUser {

    @Test
    void constructor_setsFieldsCorrectly() {
        User u = new User("AaronCasas", "Cs401", USER_ROLE.EMPLOYEE);

        assertNotNull(u.getUniqueID());           // should be something like USER_6
        assertEquals("AaronCasas", u.getUsername());
        assertEquals("CS401", u.getPassword());
        assertEquals(USER_ROLE.EMPLOYEE, u.getRole());
    }

    @Test
    void setters_updateFields() {
        User u = new User("BrianHa", "CS401", USER_ROLE.EMPLOYEE);

        u.setUsername("BrianHa");
        u.setPassword("Cs401!");
        u.setRole(USER_ROLE.IT_ADMINISTRATOR);

        assertEquals("BrianHa", u.getUsername());
        assertEquals("Cs401!", u.getPassword());
        assertEquals(USER_ROLE.IT_ADMINISTRATOR, u.getRole());
    }

    @Test
    void toString_matchesExpectedFormat() {
        User u = new User("CarlosMartin", "Cs401!", USER_ROLE.LOCKED);

        String s = u.toString();
        // format: uniqueID,username,password,role
        assertTrue(s.contains("CarlosMartin"));
        assertTrue(s.contains("Cs401"));
        assertTrue(s.contains("LOCKED"));
        assertTrue(s.startsWith("USER_")); // because of uniqueID generation
    }
}
