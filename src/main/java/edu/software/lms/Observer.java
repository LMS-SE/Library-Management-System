package edu.software.lms;

/**
 * Observer interface used in the notification system.
 *
 * <p>Classes implementing this interface receive notification callbacks from
 * subjects such as {@link ReminderService} when overdue events occur.</p>
 */
public interface Observer {

    /**
     * Called when a subject notifies observers.
     *
     * @param user    the user related to the notification
     * @param message the message describing the notification
     */
    void notify(User user, String message);
}
