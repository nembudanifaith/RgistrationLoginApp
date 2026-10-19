package com.mycompany.registrationloginapp;

/**
 * Part 1 - Registration and Login.
 *
 * A Login object stores one user's details, checks that they follow the
 * rules, and checks a login attempt against the stored details.
 *
 * @author Masindi (ST10519562)
 */
public class Login {

    // ---------- Rules (constants so there are no "magic numbers" in the code) ----------
    private static final int MAX_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String COUNTRY_CODE_PATTERN = "\\+27[0-9]{1,10}"; // +27 then 1-10 digits

    // ---------- Messages shown to the user ----------
    private static final String USERNAME_ERROR =
            "Username is not correctly formatted; please ensure that your username contains "
            + "an underscore and is no more than five characters in length.";
    private static final String PASSWORD_ERROR =
            "Password is not correctly formatted; please ensure that the password contains "
            + "at least eight characters, a capital letter, a number, and a special character.";
    private static final String CELL_ERROR =
            "Cell number is incorrectly formatted or does not contain an international code; "
            + "please correct the number and try again.";
    private static final String REGISTER_SUCCESS =
            "Username, password, and cell phone number successfully captured. "
            + "User has been registered successfully.";
    private static final String LOGIN_FAILED =
            "Username or password incorrect, please try again.";

    // ---------- Fields (private = only this class can change them) ----------
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final String cellNumber;

    /**
     * Creates a user with the details typed in at registration.
     */
    public Login(String firstName, String lastName, String username,
                 String password, String cellNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
    }

    /**
     * Username rule: contains an underscore and is no more than 5 characters.
     */
    public boolean checkUserName() {
        return username.contains("_") && username.length() <= MAX_USERNAME_LENGTH;
    }

    /**
     * Password rule: at least 8 characters, with a capital letter,
     * a number and a special character.
     */
    public boolean checkPasswordComplexity() {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        // Look at each character once and tick off what we find
        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                hasCapital = true;
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else if (!Character.isLetter(character) && !Character.isWhitespace(character)) {
                hasSpecial = true;
            }
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Cell number rule: starts with the international code +27 followed
     * by no more than 10 digits.
     */
    public boolean checkCellPhoneNumber() {
        return isValidCellNumber(cellNumber);
    }

    /**
     * Static so that the Message class can REUSE the same rule for the
     * recipient number (Part 2 hint: "reuse the method and tests").
     */
    public static boolean isValidCellNumber(String number) {
        return number != null && number.matches(COUNTRY_CODE_PATTERN);
    }

    /**
     * True when the username, password and cell number are all valid.
     */
    public boolean isRegistrationValid() {
        return checkUserName() && checkPasswordComplexity() && checkCellPhoneNumber();
    }

    /**
     * Checks the details in order and returns the matching message.
     */
    public String registerUser() {
        if (!checkUserName()) {
            return USERNAME_ERROR;
        }
        if (!checkPasswordComplexity()) {
            return PASSWORD_ERROR;
        }
        if (!checkCellPhoneNumber()) {
            return CELL_ERROR;
        }
        return REGISTER_SUCCESS;
    }

    /**
     * True only if BOTH the typed username and password match the stored ones.
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    /**
     * Returns the welcome message on success or the error message on failure.
     */
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + "," + lastName + " it is great to see you.";
        }
        return LOGIN_FAILED;
    }
}