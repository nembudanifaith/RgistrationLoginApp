/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginapp;

/**
 *
 * @author Student
 */

public class Login {

    // ---------- FIELDS (the data this object stores) ----------
    // "private" means only this class can touch these directly.
    // Other classes must go through methods (this is called "encapsulation").
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellNumber;

    // ---------- CONSTRUCTOR ----------
    // This runs when you create a new Login object, e.g.
    // Login user = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
    public Login(String firstName, String lastName, String username, String password, String cellNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
    }

    // ---------- 1. checkUserName() ----------
    // Rule: username must contain an underscore "_" and be no more than 5 characters long.
    // We use a "regular expression" (regex) - a pattern language for matching text.
    //
    // Regex breakdown: ^(?=.*_)[a-zA-Z0-9_]{1,5}$
    //   ^                 - start of the string
    //   (?=.*_)           - "lookahead": somewhere in the string there must be an underscore
    //   [a-zA-Z0-9_]{1,5} - only letters, numbers, and underscores, between 1 and 5 characters
    //   $                 - end of the string
    public boolean checkUserName() {
        return username.matches("^(?=.*_)[a-zA-Z0-9_]{1,5}$");
    }

    // ---------- 2. checkPasswordComplexity() ----------
    // Rule: at least 8 characters, 1 capital letter, 1 number, 1 special character.
    //
    // Regex breakdown: ^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\-_=+\[\]{}|;:'",.<>/?]).{8,}$
    //   (?=.*[A-Z])           - somewhere there is a capital letter
    //   (?=.*[0-9])           - somewhere there is a digit
    //   (?=.*[special chars]) - somewhere there is a special character
    //   .{8,}                 - the whole string is at least 8 characters long
    public boolean checkPasswordComplexity() {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>/?]).{8,}$";
        return password.matches(passwordPattern);
    }

    // ---------- 3. checkCellPhoneNumber() ----------
    // Rule: must contain the international country code (South Africa = +27)
    // followed by the number, and the number part must be no more than 10 characters.
    //
    // Regex breakdown: ^\+27[0-9]{1,10}$
    //   ^\+27       - must start with +27 (the + must be "escaped" with \ because
    //                 + is a special regex symbol meaning "one or more")
    //   [0-9]{1,10} - between 1 and 10 digits after the country code
    //   $           - end of string
    public boolean checkCellPhoneNumber() {
        return cellNumber.matches("^\\+27[0-9]{1,10}$");
    }

    // ---------- 4. registerUser() ----------
    // Checks everything in order and returns the correct message.
    // Notice it returns a String (a message), not true/false.
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains "
                 + "an underscore and is no more than five characters in length.";
        } else if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains "
                 + "at least eight characters, a capital letter, a number, and a special character.";
        } else if (!checkCellPhoneNumber()) {
            return "Cell number is incorrectly formatted or does not contain an international code; "
                 + "please correct the number and try again.";
        } else {
            return "Username, password, and cell phone number successfully captured. "
                 + "User has been registered successfully.";
        }
    }

    // ---------- 5. loginUser() ----------
    // Compares what was TYPED at login with what was STORED at registration.
    // Returns true only if both match exactly.
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
    }

    // ---------- 6. returnLoginStatus() ----------
    // Uses loginUser() internally and returns the correct message for the user to see.
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + "," + lastName + " it is great to see you.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // ---------- Simple "getters" so other classes can read the data if needed ----------
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
}