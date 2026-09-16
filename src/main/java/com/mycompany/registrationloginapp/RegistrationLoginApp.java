/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registrationloginapp;
import java.util.Scanner;
/**
 *
 * @author Student
 */

public class RegistrationLoginApp {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== User Registration ===");

        System.out.print("Enter first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter last name: ");
        String lastName = input.nextLine();

        System.out.print("Enter username (must contain '_' and be max 5 characters): ");
        String username = input.nextLine();

        System.out.print("Enter password (min 8 chars, 1 capital, 1 number, 1 special char): ");
        String password = input.nextLine();

        System.out.print("Enter cell number (e.g. +27831234567): ");
        String cellNumber = input.nextLine();

        // Create the Login object with everything the user typed in
        Login user = new Login(firstName, lastName, username, password, cellNumber);

        // registerUser() checks everything and tells us what went wrong (if anything)
        System.out.println();
        System.out.println(user.registerUser());

        // Only let them try to log in if registration actually succeeded
        boolean registeredSuccessfully = user.checkUserName()
                && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber();

        if (registeredSuccessfully) {
            System.out.println();
            System.out.println("=== User Login ===");

            System.out.print("Enter username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = input.nextLine();

            System.out.println();
            System.out.println(user.returnLoginStatus(loginUsername, loginPassword));
        }

        input.close();
    }
}