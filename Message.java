package com.mycompany.registrationloginapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Part 2 - Sending Messages.
 *
 * One Message object represents one chat message. The class also keeps a
 * list of every message that has been SENT while the program is running.
 *
 * JSON storage uses the Gson library.
 * Reference: Google (2023) Gson User Guide. Available at:
 * https://github.com/google/gson/blob/main/UserGuide.md (Accessed: 18 October 2026).
 *
 * @author Masindi (ST10519562)
 */
public class Message {

    // ---------- Rules ----------
    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MAX_ID_LENGTH = 10;

    // ---------- Options used by sentMessage() ----------
    public static final int OPTION_SEND = 1;
    public static final int OPTION_DISREGARD = 2;
    public static final int OPTION_STORE = 3;

    // ---------- Shared data: every message sent while the program runs ----------
    private static final List<Message> sentMessages = new ArrayList<>();
    private static Path storageFile = Paths.get("messages.json");

    // ---------- Data for ONE message ----------
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /**
     * Normal constructor: the ten-digit Message ID is generated automatically.
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    /**
     * Constructor that lets you supply the Message ID (used by the unit tests
     * so the hash can be predicted).
     */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash(); // needs the fields above, so it is last
    }

    // =====================================================================
    // Message ID
    // =====================================================================

    /**
     * Creates a random ten-digit number. %010d keeps leading zeros,
     * so the ID is always exactly ten characters.
     */
    private static String generateMessageID() {
        long randomNumber = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", randomNumber);
    }

    /**
     * True when the Message ID is no more than ten characters.
     */
    public boolean checkMessageID() {
        return messageID.length() <= MAX_ID_LENGTH;
    }

    /**
     * Text that confirms the ID was generated.
     */
    public String messageIDStatus() {
        if (checkMessageID()) {
            return "Message ID generated: " + messageID;
        }
        return "Message ID is incorrect: it must not be more than ten characters.";
    }

    // =====================================================================
    // Recipient and message length checks
    // =====================================================================

    /**
     * True when the recipient has an international code and is not too long.
     * Re-uses the rule written in Part 1 (Login class).
     */
    public boolean isRecipientValid() {
        return Login.isValidCellNumber(recipient);
    }

    /**
     * Returns the success or failure message for the recipient number.
     */
    public String checkRecipientCell() {
        if (isRecipientValid()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /**
     * True when the message is 250 characters or fewer.
     */
    public boolean isMessageLengthValid() {
        return messageText.length() <= MAX_MESSAGE_LENGTH;
    }

    /**
     * Returns "ready to send", or how many characters the message is over by.
     */
    public String checkMessageLength() {
        if (isMessageLengthValid()) {
            return "Message ready to send.";
        }
        int tooMany = messageText.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + tooMany + "; please reduce the size.";
    }

    // =====================================================================
    // Message hash
    // =====================================================================

    /**
     * Builds the hash:  first two digits of the ID : message number : FIRSTLAST
     * Example: ID 0012345678, number 0, "Hi Mike ... tonight?"  ->  00:0:HITONIGHT
     */
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = removePunctuation(words[0]);
        String lastWord = removePunctuation(words[words.length - 1]);
        String idStart = messageID.substring(0, Math.min(2, messageID.length()));

        return (idStart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Keeps letters and digits only, so "tonight?" becomes "tonight".
     */
    private static String removePunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // =====================================================================
    // Send / disregard / store
    // =====================================================================

    /**
     * Carries out the user's choice (1 = send, 2 = disregard, 3 = store)
     * and returns the message to show on screen.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case OPTION_SEND:
                sentMessages.add(this);
                return "Message successfully sent.";
            case OPTION_DISREGARD:
                return "Press 0 to delete the message.";
            case OPTION_STORE:
                try {
                    storeMessage();
                    return "Message successfully stored.";
                } catch (IOException error) {
                    return "Message could not be stored: " + error.getMessage();
                }
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    /**
     * Adds this message to the JSON file (the file keeps a list of messages).
     * Method adapted from the Gson User Guide (see class comment).
     */
    public void storeMessage() throws IOException {
        // 1. Read the messages already saved (or start a new empty list)
        JsonArray storedMessages = new JsonArray();
        if (Files.exists(storageFile)) {
            String existingJson = Files.readString(storageFile, StandardCharsets.UTF_8);
            if (!existingJson.isBlank()) {
                storedMessages = JsonParser.parseString(existingJson).getAsJsonArray();
            }
        }

        // 2. Add this message and write everything back, nicely formatted
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        storedMessages.add(gson.toJsonTree(this));
        Files.writeString(storageFile, gson.toJson(storedMessages), StandardCharsets.UTF_8);
    }

    // =====================================================================
    // Displaying messages
    // =====================================================================

    /**
     * The full details in the required order:
     * Message ID, Message Hash, Recipient, Message.
     */
    public String getMessageDetails() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + messageHash + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText;
    }

    /**
     * Returns every message sent while the program has been running.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }

        StringBuilder allMessages = new StringBuilder();
        for (Message message : sentMessages) {
            allMessages.append(message.getMessageDetails())
                       .append(System.lineSeparator())
                       .append(System.lineSeparator());
        }
        return allMessages.toString().trim();
    }

    /**
     * Total number of messages sent so far.
     */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // =====================================================================
    // Helpers (getters and test support)
    // =====================================================================

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }

    /** Empties the sent list - used by the unit tests. */
    public static void clearSentMessages() {
        sentMessages.clear();
    }

    /** Changes the JSON file used by storeMessage() - used by the unit tests. */
    public static void setStorageFile(Path newFile) {
        storageFile = newFile;
    }
}