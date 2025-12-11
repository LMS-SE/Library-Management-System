package edu.software.lms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CDTest {

    @Test
    void cdIsABook_andCanBeAddedToRepo() {
        InMemoryBooks repo = new InMemoryBooks();
        CD cd = new CD(100, "Best Of", "Various", "CD-100");
        boolean added = repo.addBook(cd);
        assertTrue(added, "CD should be added to repository like a Book");

        Book fetched = repo.getBookById(100);
        assertNotNull(fetched);
        assertInstanceOf(CD.class, fetched, "Fetched item should be a CD instance");
        assertEquals("Best Of", fetched.getName());
        assertFalse(fetched.isBorrowed());
    }
    @Test
    void testToString_notBorrowed() {
        CD cd = new CD(10, "MegaHits", "Various Artists", "CD-10");
        cd.setBorrowed(false);

        String expected = "CD{id=10, title='MegaHits', author='Various Artists', isbn='CD-10', borrowed=false}";
        assertEquals(expected, cd.toString());
    }

    @Test
    void testToString_borrowedTrue() {
        CD cd = new CD(20, "Rock Legends", "Best Band", "CD-20");
        cd.setBorrowed(true);

        String expected = "CD{id=20, title='Rock Legends', author='Best Band', isbn='CD-20', borrowed=true}";
        assertEquals(expected, cd.toString());
    }
}
