package edu.software.lms;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Observer} implementation that simulates sending email notifications.
 *
 * <p>Instead of actually sending emails, messages are stored internally
 * in a list for logging, testing, or inspection.</p>
 *
 * <p>Each notification logs:
 * <ul>
 *     <li>The user's email</li>
 *     <li>The message content</li>
 * </ul>
 * </p>
 */
public class EmailNotifier implements Observer {

    /** Stores all formatted messages "sent" by this notifier. */
    private final List<String> sentMessages = new ArrayList<>();

    /**
     * Handles a notification request for a user.
     *
     * <p>The message is formatted as:
     * <pre>To: {email} | {message}</pre>
     *
     * and appended to the internal sentMessages list.</p>
     *
     * @param user    the recipient user
     * @param message the notification message
     */
    @Override
    public void notify(User user, String message) {
        String formatted = "To: " + user.getEmail() + " | " + message;
        sentMessages.add(formatted);
    }

    /**
     * Returns the list of all messages recorded by this notifier.
     *
     * @return list of formatted sent messages
     */
    public List<String> getSentMessages() {
        return sentMessages;
    }
}
