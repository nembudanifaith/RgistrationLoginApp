package com.mycompany.registrationloginapp;

import java.util.Scanner;

/**
 * QuickChat - main program.
 *
 * Part 1: register and log in.
 * Part 2: after a successful login, show a menu (while loop) and let the
 *         user enter a set number of messages (for loop).
 *
 * @author Masindi (ST10519562)
 */
public class RegistrationLoginApp {

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int MENU_SEND_MESSAGES = 1;
    private static final int MENU_SHOW_RECENT = 2;
    private static final int MENU_QUIT = 3;
    private static final int INVALID_NUMBER = -1;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Login user = registerUser(scanner);

            if (!logIn(user, scanner)) {
                System.out.println("Too many failed login attempts. Goodbye.");
                return; // messages can only be sent after a successful login
            }

            System.out.println();
            System.out.println("Welcome to QuickChat.");
            int messageLimit = readPositiveNumber(scanner,
                    "How many messages do you want to enter? ");
            runMenu(scanner, messageLimit);
        }
    }

    // =====================================================================
    // Part 1 - registration and login
    // =====================================================================

    /**
     * Keeps asking for details until the registration is valid.
     */
    private static Login registerUser(Scanner scanner) {
        System.out.println("=== User Registration ===");
        while (true) {
            String firstName = ask(scanner, "Enter first name: ");
            String lastName = ask(scanner, "Enter last name: ");
            String username = ask(scanner, "Enter username (contains '_' and max 5 characters): ");
            String password = ask(scanner,
                    "Enter password (min 8 characters, a capital, a number, a special character): ");
            String cellNumber = ask(scanner, "Enter cell number (e.g. +27831234567): ");

            Login user = new Login(firstName, lastName, username, password, cellNumber);
            System.out.println();
            System.out.println(user.registerUser());

            if (user.isRegistrationValid()) {
                return user;
            }
            System.out.println("Please try registering again." + System.lineSeparator());
        }
    }

    /**
     * Gives the user a few attempts to log in. Returns true if one works.
     */
    private static boolean logIn(Login user, Scanner scanner) {
        System.out.println();
        System.out.println("=== User Login ===");

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String username = ask(scanner, "Enter username: ");
            String password = ask(scanner, "Enter password: ");

            System.out.println(user.returnLoginStatus(username, password));
            if (user.loginUser(username, password)) {
                return true;
            }
        }
        return false;
    }

    // =====================================================================
    // Part 2 - menu (while loop)
    // =====================================================================

    /**
     * Shows the menu again and again until the user chooses Quit.
     */
    private static void runMenu(Scanner scanner, int messageLimit) {
        boolean messagesEntered = false;
        boolean running = true;

        while (running) {
            printMenu();
            int option = readNumber(scanner, "Choose an option: ");

            switch (option) {
                case MENU_SEND_MESSAGES:
                    if (messagesEntered) {
                        System.out.println("You have already entered all " + messageLimit + " messages.");
                    } else {
                        sendMessages(scanner, messageLimit);
                        messagesEntered = true;
                    }
                    break;
                case MENU_SHOW_RECENT:
                    System.out.println("Coming Soon.");
                    break;
                case MENU_QUIT:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2 or 3.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    // =====================================================================
    // Part 2 - sending messages (for loop)
    // =====================================================================

    /**
     * Uses a for loop to capture exactly the number of messages the user chose.
     * The loop counter doubles as the message number (0, 1, 2 ...).
     */
    private static void sendMessages(Scanner scanner, int messageLimit) {
        for (int messageNumber = 0; messageNumber < messageLimit; messageNumber++) {
            System.out.println();
            System.out.println("--- Message " + (messageNumber + 1) + " of " + messageLimit + " ---");

            Message message = readValidMessage(scanner, messageNumber);
            chooseWhatToDo(scanner, message);
        }

        System.out.println();
        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
    }

    /**
     * Asks for the recipient and text until both pass their checks.
     */
    private static Message readValidMessage(Scanner scanner, int messageNumber) {
        while (true) {
            String recipient = ask(scanner, "Recipient cell number (e.g. +27718693002): ");
            String text = ask(scanner, "Message (max 250 characters): ");
            Message message = new Message(messageNumber, recipient, text);

            if (message.isRecipientValid() && message.isMessageLengthValid()) {
                System.out.println(message.checkRecipientCell());
                System.out.println(message.checkMessageLength());
                return message;
            }

            if (!message.isRecipientValid()) {
                System.out.println(message.checkRecipientCell());
            }
            if (!message.isMessageLengthValid()) {
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.println(message.checkMessageLength());
            }
        }
    }

    /**
     * Lets the user send, disregard or store the message, then shows the result.
     */
    private static void chooseWhatToDo(Scanner scanner, Message message) {
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");

        int choice = readNumber(scanner, "Choose an option: ");
        while (choice < Message.OPTION_SEND || choice > Message.OPTION_STORE) {
            System.out.println("Invalid option. Please choose 1, 2 or 3.");
            choice = readNumber(scanner, "Choose an option: ");
        }

        System.out.println(message.sentMessage(choice));

        if (choice == Message.OPTION_SEND) {
            System.out.println();
            System.out.println(message.getMessageDetails());
        } else if (choice == Message.OPTION_DISREGARD) {
            String answer = ask(scanner, "> ");
            System.out.println(answer.equals("0") ? "Message deleted." : "Message not sent.");
        }
    }

    // =====================================================================
    // Small input helpers (so the same code is not repeated)
    // =====================================================================

    /** Prints a question and returns what the user typed. */
    private static String ask(Scanner scanner, String question) {
        System.out.print(question);
        return scanner.nextLine().trim();
    }

    /** Reads a whole number; returns -1 (instead of crashing) if the input is not a number. */
    private static int readNumber(Scanner scanner, String question) {
        try {
            return Integer.parseInt(ask(scanner, question));
        } catch (NumberFormatException error) {
            return INVALID_NUMBER;
        }
    }

    /** Keeps asking until the user types a number greater than zero. */
    private static int readPositiveNumber(Scanner scanner, String question) {
        int number = readNumber(scanner, question);
        while (number < 1) {
            System.out.println("Please enter a whole number greater than 0.");
            number = readNumber(scanner, question);
        }
        return number;
    }
}