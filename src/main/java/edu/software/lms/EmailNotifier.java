package edu.software.lms;

import java.util.ArrayList;
import java.util.List;

public class EmailNotifier implements Observer {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void notify(User user, String message) {
        String formatted = "To: " + user.getEmail() + " | " + message;

        sentMessages.add(formatted);
    }

    
    public List<String> getSentMessages() {
        return sentMessages;
    }
}
