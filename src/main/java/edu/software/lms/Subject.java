package edu.software.lms;

/**
 * Subject interface for the observer pattern.
 *
 * <p>Implemented by {@link ReminderService} to support notification
 * subscriptions by observers.</p>
 */
public interface Subject {

    /**
     * Registers a new observer.
     *
     * @param observer observer instance
     */
    void addObserver(Observer observer);

    /**
     * Unregisters an observer.
     *
     * @param observer observer instance to remove
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all observers with the given user and message.
     *
     * @param user    user associated with the notification
     * @param message content of the notification
     */
    void notifyObservers(User user, String message);
}
