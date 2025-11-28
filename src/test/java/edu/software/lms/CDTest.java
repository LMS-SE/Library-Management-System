package edu.software.lms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CDTest {

    @Test
    void cdIsABook_andCanBeAddedToRepo() {
        InMemoryBooks repo = new InMemoryBooks();
        CD cd = new CD(100, "Best Of", "Various", "CD-100");
        boolean added = repo.addBook(cd);
        assertTrue(added, "CD should be added to repository like a Book");

        Book fetched = repo.getBookById(100);
        assertNotNull(fetched);
        assertTrue(fetched instanceof CD, "Fetched item should be a CD instance");
        assertEquals("Best Of", fetched.getName());
        assertFalse(fetched.isBorrowed());
    }
}
