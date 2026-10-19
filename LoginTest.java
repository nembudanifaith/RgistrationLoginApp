package com.mycompany.registrationloginapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Part 1 unit tests for the Login class.
 *
 * @author Masindi (ST10519562)
 */
public class LoginTest {

    /** Creates a user with valid details; each test then changes only what it needs. */
    private Login validUser() {
        return new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
    }

    // ---------- Username ----------

    @Test
    public void usernameCorrectlyFormatted() {
        assertTrue(validUser().checkUserName());
    }

    @Test
    public void usernameIncorrectlyFormatted() {
        Login user = new Login("Kyle", "Smith", "kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.checkUserName());
    }

    // ---------- Password ----------

    @Test
    public void passwordMeetsComplexity() {
        assertTrue(validUser().checkPasswordComplexity());
    }

    @Test
    public void passwordDoesNotMeetComplexity() {
        Login user = new Login("Kyle", "Smith", "kyl_1", "password", "+27838968976");
        assertFalse(user.checkPasswordComplexity());
    }

    // ---------- Cell phone number ----------

    @Test
    public void cellPhoneCorrectlyFormatted() {
        assertTrue(validUser().checkCellPhoneNumber());
    }

    @Test
    public void cellPhoneIncorrectlyFormatted() {
        Login user = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(user.checkCellPhoneNumber());
    }

    // ---------- registerUser() messages ----------

    @Test
    public void registerUserSuccessMessage() {
        assertEquals("Username, password, and cell phone number successfully captured. "
                + "User has been registered successfully.", validUser().registerUser());
    }

    @Test
    public void registerUserUsernameErrorMessage() {
        Login user = new Login("Kyle", "Smith", "kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username is not correctly formatted; please ensure that your username contains "
                + "an underscore and is no more than five characters in length.", user.registerUser());
    }

    @Test
    public void registerUserPasswordErrorMessage() {
        Login user = new Login("Kyle", "Smith", "kyl_1", "password", "+27838968976");
        assertEquals("Password is not correctly formatted; please ensure that the password contains "
                + "at least eight characters, a capital letter, a number, and a special character.",
                user.registerUser());
    }

    @Test
    public void registerUserCellErrorMessage() {
        Login user = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; "
                + "please correct the number and try again.", user.registerUser());
    }

    // ---------- Login ----------

    @Test
    public void loginSuccessful() {
        assertTrue(validUser().loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginFailed() {
        assertFalse(validUser().loginUser("kyl_1", "wrongPassword"));
    }

    @Test
    public void loginStatusWelcomeMessage() {
        assertEquals("Welcome Kyle,Smith it is great to see you.",
                validUser().returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginStatusFailureMessage() {
        assertEquals("Username or password incorrect, please try again.",
                validUser().returnLoginStatus("kyl_1", "wrongPassword"));
    }
}