package edu.software.lms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashingTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void hashPassword() {
        String username1="user1",username2="user2";
        //to make sure it generates the same hash given the same password and salt (username)
        assertEquals(CustomUtilities.hashPassword("pwd",username1),CustomUtilities.hashPassword("pwd",username1));
        //to make sure it generates different hashes given the same password with different salts
        assertNotEquals(CustomUtilities.hashPassword("pwd",username1),CustomUtilities.hashPassword("pwd",username2));
        //to make sure it generates different hashes given different passwords with different salts
        assertNotEquals(CustomUtilities.hashPassword("pwd1",username1),CustomUtilities.hashPassword("pwd2",username2));

    }
}