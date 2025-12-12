package edu.software.lms;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotifierSendEmailCoverageTest {

    static class TestableEmailNotifier extends EmailNotifier {
        Message lastSent;
        boolean throwOnSend = false;

        TestableEmailNotifier() {
            super("smtp.test", 587, "user", "pass", "from@test.com");
        }

        @Override
        protected void doSend(Message msg) throws MessagingException {
            if (throwOnSend) throw new MessagingException("boom");
            this.lastSent = msg;
        }
    }

    @Test
    void notify_whenSmtpConfigured_callsSend_andDoesNotThrow() {
        TestableEmailNotifier n = new TestableEmailNotifier();
        User u = new User("u", "p", "to@test.com", false);

        n.notify(u, "Hello");

        assertNotNull(n.lastSent);
        assertEquals(1, n.getSentMessages().size());
    }

    @Test
    void notify_whenSendThrows_stillDoesNotThrow_andStillRecords() {
        TestableEmailNotifier n = new TestableEmailNotifier();
        n.throwOnSend = true;

        User u = new User("u", "p", "to@test.com", false);

        assertDoesNotThrow(() -> n.notify(u, "Hello"));

        assertEquals(1, n.getSentMessages().size());
    }
}
