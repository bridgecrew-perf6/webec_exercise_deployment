package ch.fhnw.webec.booklist.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfanityServiceUnitTest {
    private ProfanityService profanityService;

    @BeforeAll
    public void setUp() {
        this.profanityService = new ProfanityService();
        this.profanityService.setBadWords(Arrays.asList(
            "darn",
            "heck"
        ));
    }

    @Test
    public void testBadWords() {
        assertTrue(this.profanityService.containsBadWords("This book is darn good!"));
        assertFalse(this.profanityService.containsBadWords("This book is great!"));
    }

    @Test
    public void testBadWordsCaseInsensitive() {
        assertTrue(this.profanityService.containsBadWords("This book is dArN good!"));
        assertTrue(this.profanityService.containsBadWords("What the HECK?"));
    }
}
