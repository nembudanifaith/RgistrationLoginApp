/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Student
 */

public class LoginTest {

    // ---------- USERNAME TESTS ----------

    @Test
    public void testUsernameCorrectlyFormatted() {
        // Test Data: "kyl_1" -> has an underscore, only 5 characters -> should be TRUE
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        // Test Data: "kyle!!!!!!" -> no underscore, too long -> should be FALSE
        Login user = new Login("Kyle", "Ndlovu", "kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.checkUserName());
    }

    // ---------- PASSWORD TESTS ----------

    @Test
    public void testPasswordMeetsComplexity() {
        // Test Data: "Ch&&sec@ke99!" -> capital, number, special char, 8+ length -> TRUE
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        // Test Data: "password" -> no capital, no number, no special char -> FALSE
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "password", "+27838968976");
        assertFalse(user.checkPasswordComplexity());
    }

    // ---------- CELL PHONE TESTS ----------

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        // Test Data: +27838968976 -> has +27 country code -> TRUE
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        // Test Data: 08966553 -> no country code -> FALSE
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(user.checkCellPhoneNumber());
    }

    // ---------- registerUser() MESSAGE TESTS ----------

    @Test
    public void testRegisterUserSuccessMessage() {
                Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username, password, and cell phone number successfully captured. "
                + "User has been registered successfully.", user.registerUser());
    }

    @Test
    public void testRegisterUserUsernameErrorMessage() {
        Login user = new Login("Kyle", "Ndlovu", "kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username is not correctly formatted; please ensure that your username contains "
                + "an underscore and is no more than five characters in length.", user.registerUser());
    }

    // ---------- LOGIN TESTS ----------

    @Test
    public void testLoginSuccessful() {
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.loginUser("kyl_1", "wrongPassword"));
    }

    // ---------- returnLoginStatus() MESSAGE TESTS ----------

    @Test
    public void testReturnLoginStatusWelcomeMessage() {
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Welcome Kyle,Ndlovu it is great to see you.",
                user.returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testReturnLoginStatusFailureMessage() {
        Login user = new Login("Kyle", "Ndlovu", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username or password incorrect, please try again.",
                user.returnLoginStatus("kyl_1", "wrongPassword"));
    }
}