package com.mycompany.registrationloginapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Part 3 unit tests for the MessageManager class (the arrays and the Stored Messages features).
 * The five test messages come from the brief.
 *
 * Note on the test data: the brief gives no Message ID for most messages, so fixed
 * ten-digit IDs are used here. For Test Data Message 4 the brief shows 0838884567 in the
 * "Developer" row and searches for it as a message ID, so it is used as that message's ID.
 *
 * @author Masindi (ST10519562)
 */
public class MessageManagerTest {

    // ---------- Test Data Message 1 (Sent) ----------
    private static final String ID_1 = "1000000001";
    private static final String RECIPIENT_1 = "+27834557896";
    private static final String TEXT_1 = "Did you get the cake?";

    // ---------- Test Data Message 2 (Stored) ----------
    private static final String ID_2 = "2000000002";
    private static final String RECIPIENT_2 = "+27838884567";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";

    // ---------- Test Data Message 3 (Disregard) ----------
    private static final String ID_3 = "3000000003";
    private static final String RECIPIENT_3 = "+27834484567";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";

    // ---------- Test Data Message 4 (Sent) ----------
    private static final String ID_4 = "0838884567";
    private static final String RECIPIENT_4 = "+27831234567";
    private static final String TEXT_4 = "It is dinner time!";

    // ---------- Test Data Message 5 (Stored) ----------
    private static final String ID_5 = "5000000005";
    private static final String RECIPIENT_5 = "+27838884567";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    // ---------- Hashes that the Message class builds from the data above ----------
    private static final String HASH_1 = "10:0:DIDCAKE";
    private static final String HASH_2 = "20:1:WHERETIME";
    private static final String HASH_3 = "30:2:YOHOOOOGATE";
    private static final String HASH_4 = "08:3:ITTIME";
    private static final String HASH_5 = "50:4:OKYOU";

    @TempDir
    Path tempFolder; // JUnit gives each test a fresh folder, so tests never touch real files

    private MessageManager manager;
    private Path originalStorageFile;

    /** Before every test: fill a fresh MessageManager with the five test messages. */
    @BeforeEach
    public void setUp() {
        originalStorageFile = Message.getStorageFile();
        manager = new MessageManager("Developer");

        manager.addMessage(new Message(ID_1, 0, RECIPIENT_1, TEXT_1), MessageManager.FLAG_SENT);
        manager.addMessage(new Message(ID_2, 1, RECIPIENT_2, TEXT_2), MessageManager.FLAG_STORED);
        manager.addMessage(new Message(ID_3, 2, RECIPIENT_3, TEXT_3), MessageManager.FLAG_DISREGARD);
        manager.addMessage(new Message(ID_4, 3, RECIPIENT_4, TEXT_4), MessageManager.FLAG_SENT);
        manager.addMessage(new Message(ID_5, 4, RECIPIENT_5, TEXT_5), MessageManager.FLAG_STORED);
    }

    /** After every test: put the real JSON file location back. */
    @AfterEach
    public void tearDown() {
        Message.setStorageFile(originalStorageFile);
    }

    // =====================================================================
    // Arrays correctly populated
    // =====================================================================

    @Test
    public void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(TEXT_1, TEXT_4), manager.getSentMessages());
    }

    @Test
    public void disregardedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(TEXT_3), manager.getDisregardedMessages());
    }

    @Test
    public void storedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(TEXT_2, TEXT_5), manager.getStoredMessages());
    }

    @Test
    public void messageHashArrayCorrectlyPopulated() {
        assertEquals(List.of(HASH_1, HASH_2, HASH_3, HASH_4, HASH_5), manager.getMessageHashes());
    }

    @Test
    public void messageIDArrayCorrectlyPopulated() {
        assertEquals(List.of(ID_1, ID_2, ID_3, ID_4, ID_5), manager.getMessageIDs());
    }

    // =====================================================================
    // Display the longest message
    // =====================================================================

    @Test
    public void longestStoredMessageIsMessage2() {
        assertEquals(TEXT_2, manager.findLongestStoredMessage());
    }

    @Test
    public void longestStoredMessageWhenNothingIsStored() {
        MessageManager emptyManager = new MessageManager("Developer");
        assertEquals("There are no stored messages.", emptyManager.findLongestStoredMessage());
    }

    // =====================================================================
    // Search for message ID
    // =====================================================================

    @Test
    public void searchForMessageIDFound() {
        String expected = "Recipient: " + RECIPIENT_4 + System.lineSeparator()
                + "Message: It is dinner time!";
        assertEquals(expected, manager.searchByMessageID("0838884567"));
    }

    @Test
    public void searchForMessageIDNotFound() {
        assertEquals("Message ID not found.", manager.searchByMessageID("9999999999"));
    }

    // =====================================================================
    // Search all messages sent or stored for a recipient
    // =====================================================================

    @Test
    public void searchForRecipientFindsSentAndStoredMessages() {
        assertEquals(List.of(TEXT_2, TEXT_5), manager.searchByRecipient("+27838884567"));
    }

    @Test
    public void searchForRecipientIgnoresDisregardedMessages() {
        assertEquals(List.of(), manager.searchByRecipient(RECIPIENT_3));
    }

    // =====================================================================
    // Delete a message using the message hash
    // =====================================================================

    @Test
    public void deleteMessageUsingHashReturnsConfirmation() {
        assertEquals("Message: \"" + TEXT_2 + "\" successfully deleted.",
                manager.deleteMessageByHash(HASH_2));
    }

    @Test
    public void deleteMessageRemovesItFromEveryArray() {
        manager.deleteMessageByHash(HASH_2);

        assertEquals(List.of(TEXT_5), manager.getStoredMessages());
        assertEquals(List.of(HASH_1, HASH_3, HASH_4, HASH_5), manager.getMessageHashes());
        assertEquals(List.of(ID_1, ID_3, ID_4, ID_5), manager.getMessageIDs());
        assertEquals("Message ID not found.", manager.searchByMessageID(ID_2));
    }

    @Test
    public void deleteMessageWithUnknownHash() {
        assertEquals("Message hash not found.", manager.deleteMessageByHash("00:0:NOTHING"));
    }

    // =====================================================================
    // Sender and recipient of all stored messages
    // =====================================================================

    @Test
    public void senderAndRecipientOfStoredMessages() {
        String expected = "Sender: Developer | Recipient: " + RECIPIENT_2 + System.lineSeparator()
                + "Sender: Developer | Recipient: " + RECIPIENT_5;
        assertEquals(expected, manager.displaySenderAndRecipient());
    }

    // =====================================================================
    // Display report
    // =====================================================================

    @Test
    public void reportForOneMessageShowsAllDetails() {
        MessageManager oneMessage = new MessageManager("Developer");
        oneMessage.addMessage(new Message(ID_1, 0, RECIPIENT_1, TEXT_1), MessageManager.FLAG_SENT);

        String expected = "Message ID: " + ID_1 + System.lineSeparator()
                + "Message Hash: " + HASH_1 + System.lineSeparator()
                + "Sender: Developer" + System.lineSeparator()
                + "Recipient: " + RECIPIENT_1 + System.lineSeparator()
                + "Message: " + TEXT_1 + System.lineSeparator()
                + "Status: Sent";
        assertEquals(expected, oneMessage.displayReport());
    }

    @Test
    public void reportListsSentAndStoredMessagesButNotDisregarded() {
        String report = manager.displayReport();

        assertTrue(report.contains(HASH_1) && report.contains(TEXT_1));
        assertTrue(report.contains(HASH_2) && report.contains(TEXT_2));
        assertTrue(report.contains(HASH_4) && report.contains(TEXT_4));
        assertTrue(report.contains(HASH_5) && report.contains(TEXT_5));
        assertFalse(report.contains(TEXT_3));
    }

    @Test
    public void reportWhenThereAreNoMessages() {
        assertEquals("There are no messages to report on.", new MessageManager("Developer").displayReport());
    }

    // =====================================================================
    // Reading the JSON file into the Stored Messages array
    // =====================================================================

    @Test
    public void storedMessagesAreReadBackFromJsonFile() throws IOException {
        Path jsonFile = tempFolder.resolve("messages.json");
        Message.setStorageFile(jsonFile);
        new Message(ID_2, 1, RECIPIENT_2, TEXT_2).storeMessage();
        new Message(ID_5, 4, RECIPIENT_5, TEXT_5).storeMessage();

        MessageManager fromFile = new MessageManager("Developer");
        int count = fromFile.loadStoredMessages(jsonFile);

        assertEquals(2, count);
        assertEquals(List.of(TEXT_2, TEXT_5), fromFile.getStoredMessages());
        assertEquals(List.of(HASH_2, HASH_5), fromFile.getMessageHashes());
        assertEquals(List.of(ID_2, ID_5), fromFile.getMessageIDs());
    }

    @Test
    public void missingJsonFileGivesAnEmptyArray() throws IOException {
        MessageManager fromFile = new MessageManager("Developer");
        assertEquals(0, fromFile.loadStoredMessages(tempFolder.resolve("doesNotExist.json")));
        assertEquals(List.of(), fromFile.getStoredMessages());
    }

    // =====================================================================
    // Menu choice -> flag
    // =====================================================================

    @Test
    public void menuChoicesBecomeTheCorrectFlags() {
        assertEquals("Sent", MessageManager.flagForChoice(Message.OPTION_SEND));
        assertEquals("Disregard", MessageManager.flagForChoice(Message.OPTION_DISREGARD));
        assertEquals("Stored", MessageManager.flagForChoice(Message.OPTION_STORE));
    }
}