package com.mycompany.registrationloginapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Part 2 unit tests for the Message class, using the test data from the brief.
 *
 * @author Masindi (ST10519562)
 */
public class MessageTest {

    // ---------- Test Data for Message 1 ----------
    private static final String ID_1 = "0012345678";
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    // ---------- Test Data for Message 2 ----------
    private static final String ID_2 = "0098765432";
    private static final String RECIPIENT_2 = "08575975889"; // no international code
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempFolder; // JUnit gives each test a fresh folder, so tests never touch real files

    /** Start every test with a clean list of sent messages and a temporary JSON file. */
    @BeforeEach
    public void setUp() {
        Message.clearSentMessages();
        Message.setStorageFile(tempFolder.resolve("messages.json"));
    }

    private Message message1() {
        return new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
    }

    private Message message2() {
        return new Message(ID_2, 1, RECIPIENT_2, TEXT_2);
    }

    // =====================================================================
    // Message length (max 250 characters)
    // =====================================================================

    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", message1().checkMessageLength());
    }

    @Test
    public void messageLengthFailure() {
        String longText = "a".repeat(255); // 5 characters too many
        Message message = new Message(ID_1, 0, RECIPIENT_1, longText);
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.",
                message.checkMessageLength());
    }

    @Test
    public void messageOfExactly250CharactersIsAccepted() {
        Message message = new Message(ID_1, 0, RECIPIENT_1, "a".repeat(250));
        assertTrue(message.isMessageLengthValid());
    }

    // =====================================================================
    // Recipient number
    // =====================================================================

    @Test
    public void recipientCellSuccess() {
        assertEquals("Cell phone number successfully captured.", message1().checkRecipientCell());
    }

    @Test
    public void recipientCellFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message2().checkRecipientCell());
    }

    // =====================================================================
    // Message hash
    // =====================================================================

    @Test
    public void messageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", message1().createMessageHash());
    }

    @Test
    public void messageHashesInALoop() {
        // Test the rest of the hashes in a loop (as the brief asks)
        String[] ids = {ID_1, ID_2};
        String[] texts = {TEXT_1, TEXT_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int number = 0; number < texts.length; number++) {
            Message message = new Message(ids[number], number, RECIPIENT_1, texts[number]);
            assertEquals(expectedHashes[number], message.createMessageHash());
        }
    }

    // =====================================================================
    // Message ID
    // =====================================================================

    @Test
    public void messageIDIsCreated() {
        assertEquals("Message ID generated: " + ID_1, message1().messageIDStatus());
    }

    @Test
    public void randomMessageIDIsTenCharacters() {
        Message message = new Message(0, RECIPIENT_1, TEXT_1);
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.checkMessageID());
    }

    @Test
    public void messageIDLongerThanTenCharactersFailsCheck() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
        assertFalse(message.checkMessageID());
    }

    // =====================================================================
    // MessageSent options (1 = send, 2 = disregard, 3 = store)
    // =====================================================================

    @Test
    public void messageSentOptionSend() {
        assertEquals("Message successfully sent.", message1().sentMessage(Message.OPTION_SEND));
    }

    @Test
    public void messageSentOptionDisregard() {
        assertEquals("Press 0 to delete the message.", message2().sentMessage(Message.OPTION_DISREGARD));
    }

    @Test
    public void messageSentOptionStore() {
        assertEquals("Message successfully stored.", message1().sentMessage(Message.OPTION_STORE));
    }

    @Test
    public void messageSentInvalidOption() {
        assertEquals("Invalid option. Please choose 1, 2 or 3.", message1().sentMessage(9));
    }

    // =====================================================================
    // Totals and printing (Message 1 is sent, Message 2 is disregarded)
    // =====================================================================

    @Test
    public void totalMessagesAfterSendAndDisregard() {
        message1().sentMessage(Message.OPTION_SEND);
        message2().sentMessage(Message.OPTION_DISREGARD);
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void totalMessagesStartsAtZero() {
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void printMessagesShowsSentMessageDetailsInOrder() {
        Message sent = message1();
        sent.sentMessage(Message.OPTION_SEND);

        String expected = "Message ID: " + ID_1 + System.lineSeparator()
                + "Message Hash: 00:0:HITONIGHT" + System.lineSeparator()
                + "Recipient: " + RECIPIENT_1 + System.lineSeparator()
                + "Message: " + TEXT_1;
        assertEquals(expected, Message.printMessages());
    }

    @Test
    public void printMessagesWhenNothingSent() {
        assertEquals("No messages have been sent yet.", Message.printMessages());
    }

    // =====================================================================
    // JSON storage
    // =====================================================================

    @Test
    public void storeMessageWritesJsonFile() throws IOException {
        message1().storeMessage();

        Path jsonFile = tempFolder.resolve("messages.json");
        assertTrue(Files.exists(jsonFile));

        String json = Files.readString(jsonFile, StandardCharsets.UTF_8);
        assertTrue(json.contains("\"messageID\": \"" + ID_1 + "\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
    }

    @Test
    public void storeMessageAppendsInsteadOfOverwriting() throws IOException {
        message1().storeMessage();
        message2().storeMessage();

        String json = Files.readString(tempFolder.resolve("messages.json"), StandardCharsets.UTF_8);
        assertTrue(json.contains(ID_1));
        assertTrue(json.contains(ID_2));
    }
}