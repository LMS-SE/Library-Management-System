package edu.software.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongPwdTest {
    private void assertStrong(String pwd)  { assertTrue (CustomUtilities.isStrongPassword(pwd)); }
    private void assertWeak  (String pwd)  { assertFalse(CustomUtilities.isStrongPassword(pwd)); }
    @BeforeEach
    void setUp() {
    }

    @Test
    void isStrongPassword() {
        assertWeak      ("123456789");
        assertWeak      ("test123123");
        assertWeak      ("testtest");
        assertWeak      ("TESTTEST");
        assertWeak      ("!@#@#$@#$%");
        assertWeak      ("Password!@#");
        assertWeak      ("Password123");
        assertWeak      ("12345!@#");
        assertWeak      ("TEST!@#");
        assertWeak      ("");
        assertWeak      ("abcABC123");
        assertWeak      ("helloworld1$");
        assertWeak      ("HELLOWORLD1$");
        assertWeak      ("HelloWorld!");

        assertStrong    ("Zx9@");
        assertStrong    ("HelloWorld1$");
        assertStrong    ("1aA@");
        assertStrong    ("123abcABC!@#");
        assertStrong    ("Aa1$%^&*()");
        assertStrong    ("Abc!123Def");

    }
}