package com.mycompany.registrationloginapp;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Part 3 - Store Data and Display.
 *
 * MessageManager keeps all the arrays for QuickChat and the features that
 * search and display them.
 *
 * There are two groups of arrays:
 *
 * 1. CATEGORY arrays - the text of every message, split by what the user did with it:
 *    sentMessages, disregardedMessages, storedMessages.
 *
 * 2. PARALLEL arrays - one entry for EVERY message. Position (index) 0 in each
 *    array describes the same message, position 1 describes the next message, and so on:
 *    messageIDs, messageHashes, recipients, messageTexts, messageFlags.
 *
 * Reading the JSON file into an array uses the Gson library.
 * Reference: Google (2023) Gson User Guide, "Array Examples". Available at:
 * https://github.com/google/gson/blob/main/UserGuide.md (Accessed: 18 November 2026).
 *
 * @author Masindi (ST10519562)
 */
public class MessageManager {

    // ---------- Flags: what happened to a message ----------
    public static final String FLAG_SENT = "Sent";
    public static final String FLAG_DISREGARD = "Disregard";
    public static final String FLAG_STORED = "Stored";

    // ---------- Messages shown to the user ----------
    private static final String NO_STORED_MESSAGES = "There are no stored messages.";
    private static final String NO_MESSAGES_FOR_REPORT = "There are no messages to report on.";
    private static final String ID_NOT_FOUND = "Message ID not found.";
    private static final String HASH_NOT_FOUND = "Message hash not found.";

    // ---------- Category arrays (the text of each message) ----------
    private final List<String> sentMessages = new ArrayList<>();
    private final List<String> disregardedMessages = new ArrayList<>();
    private final List<String> storedMessages = new ArrayList<>();

    // ---------- Parallel arrays (same index = same message) ----------
    private final List<String> messageIDs = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> recipients = new ArrayList<>();
    private final List<String> messageTexts = new ArrayList<>();
    private final List<String> messageFlags = new ArrayList<>();

    /** The person who sends the messages (the logged-in QuickChat user). */
    private final String sender;

    /**
     * Creates empty arrays for one user.
     *
     * @param sender the name shown as the sender of every message
     */
    public MessageManager(String sender) {
        this.sender = sender;
    }

    // =====================================================================
    // Populating the arrays (no hard-coding: the data comes from the user or the JSON file)
    // =====================================================================

    /**
     * Turns the menu choice (1, 2 or 3) from Part 2 into a flag.
     */
    public static String flagForChoice(int choice) {
        switch (choice) {
            case Message.OPTION_SEND:
                return FLAG_SENT;
            case Message.OPTION_DISREGARD:
                return FLAG_DISREGARD;
            case Message.OPTION_STORE:
                return FLAG_STORED;
            default:
                throw new IllegalArgumentException("Unknown message option: " + choice);
        }
    }

    /**
     * Adds ONE message to every array that should hold it.
     * The parallel arrays are all added to in the same method call, so their
     * positions always stay lined up.
     */
    public void addMessage(Message message, String flag) {
        messageIDs.add(message.getMessageID());
        messageHashes.add(message.getMessageHash());
        recipients.add(message.getRecipient());
        messageTexts.add(message.getMessageText());
        messageFlags.add(flag);
        categoryArrayFor(flag).add(message.getMessageText());
    }

    /**
     * Reads the JSON file written by Message.storeMessage() into the arrays.
     * Gson turns the JSON list straight into an array of Message objects.
     * Code adapted from the Gson User Guide (see class comment).
     *
     * @return how many messages were read from the file (0 if there is no file yet)
     */
    public int loadStoredMessages(Path jsonFile) throws IOException {
        if (!Files.exists(jsonFile)) {
            return 0;
        }
        String json = Files.readString(jsonFile, StandardCharsets.UTF_8);
        if (json.isBlank()) {
            return 0;
        }

        Message[] messagesInFile = new Gson().fromJson(json, Message[].class);
        if (messagesInFile == null) {
            return 0;
        }
        for (Message message : messagesInFile) {
            addMessage(message, FLAG_STORED);
        }
        return messagesInFile.length;
    }

    /**
     * Picks the category array that matches a flag.
     */
    private List<String> categoryArrayFor(String flag) {
        switch (flag) {
            case FLAG_SENT:
                return sentMessages;
            case FLAG_DISREGARD:
                return disregardedMessages;
            case FLAG_STORED:
                return storedMessages;
            default:
                throw new IllegalArgumentException("Unknown flag: " + flag);
        }
    }

    // =====================================================================
    // Menu option 4 (a) - sender and recipient of all stored messages
    // =====================================================================

    public String displaySenderAndRecipient() {
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < messageFlags.size(); index++) {
            if (messageFlags.get(index).equals(FLAG_STORED)) {
                lines.add("Sender: " + sender + " | Recipient: " + recipients.get(index));
            }
        }
        if (lines.isEmpty()) {
            return NO_STORED_MESSAGES;
        }
        return String.join(System.lineSeparator(), lines);
    }

    // =====================================================================
    // Menu option 4 (b) - longest stored message
    // =====================================================================

    /**
     * Looks at every stored message and keeps the longest one found so far.
     */
    public String findLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return NO_STORED_MESSAGES;
        }
        String longest = storedMessages.get(0);
        for (String text : storedMessages) {
            if (text.length() > longest.length()) {
                longest = text;
            }
        }
        return longest;
    }

    /**
     * A numbered list of the stored messages (used to show what was read from the JSON file).
     */
    public String displayStoredMessages() {
        if (storedMessages.isEmpty()) {
            return NO_STORED_MESSAGES;
        }
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < storedMessages.size(); index++) {
            lines.add((index + 1) + ". " + storedMessages.get(index));
        }
        return String.join(System.lineSeparator(), lines);
    }

    // =====================================================================
    // Menu option 4 (c) - search by message ID
    // =====================================================================

    /**
     * Finds the position of the ID in the messageIDs array, then uses that same
     * position in the other parallel arrays to get the recipient and message.
     */
    public String searchByMessageID(String messageID) {
        int index = messageIDs.indexOf(messageID); // -1 means "not found"
        if (index == -1) {
            return ID_NOT_FOUND;
        }
        return "Recipient: " + recipients.get(index) + System.lineSeparator()
                + "Message: " + messageTexts.get(index);
    }

    // =====================================================================
    // Menu option 4 (d) - search by recipient
    // =====================================================================

    /**
     * Returns the text of every SENT or STORED message for this recipient
     * (disregarded messages are ignored).
     */
    public List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (int index = 0; index < recipients.size(); index++) {
            String flag = messageFlags.get(index);
            boolean wasSentOrStored = flag.equals(FLAG_SENT) || flag.equals(FLAG_STORED);

            if (recipients.get(index).equals(recipient) && wasSentOrStored) {
                found.add(messageTexts.get(index));
            }
        }
        return found;
    }

    // =====================================================================
    // Menu option 4 (e) - delete by message hash
    // =====================================================================

    /**
     * Finds the hash, then removes that position from every parallel array
     * and removes the text from its category array.
     */
    public String deleteMessageByHash(String messageHash) {
        int index = messageHashes.indexOf(messageHash.trim().toUpperCase());
        if (index == -1) {
            return HASH_NOT_FOUND;
        }

        String deletedText = messageTexts.get(index);
        categoryArrayFor(messageFlags.get(index)).remove(deletedText);

        messageIDs.remove(index);
        messageHashes.remove(index);
        recipients.remove(index);
        messageTexts.remove(index);
        messageFlags.remove(index);

        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    // =====================================================================
    // Menu option 4 (f) - report
    // =====================================================================

    /**
     * A report with the full details of every sent and stored message.
     */
    public String displayReport() {
        List<String> entries = new ArrayList<>();
        for (int index = 0; index < messageFlags.size(); index++) {
            String flag = messageFlags.get(index);
            if (flag.equals(FLAG_SENT) || flag.equals(FLAG_STORED)) {
                entries.add("Message ID: " + messageIDs.get(index) + System.lineSeparator()
                        + "Message Hash: " + messageHashes.get(index) + System.lineSeparator()
                        + "Sender: " + sender + System.lineSeparator()
                        + "Recipient: " + recipients.get(index) + System.lineSeparator()
                        + "Message: " + messageTexts.get(index) + System.lineSeparator()
                        + "Status: " + flag);
            }
        }
        if (entries.isEmpty()) {
            return NO_MESSAGES_FOR_REPORT;
        }
        return String.join(System.lineSeparator() + System.lineSeparator(), entries);
    }

    // =====================================================================
    // Getters (read-only views, so other classes cannot change the arrays by accident)
    // =====================================================================

    public List<String> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }

    public List<String> getDisregardedMessages() {
        return Collections.unmodifiableList(disregardedMessages);
    }

    public List<String> getStoredMessages() {
        return Collections.unmodifiableList(storedMessages);
    }

    public List<String> getMessageHashes() {
        return Collections.unmodifiableList(messageHashes);
    }

    public List<String> getMessageIDs() {
        return Collections.unmodifiableList(messageIDs);
    }
}