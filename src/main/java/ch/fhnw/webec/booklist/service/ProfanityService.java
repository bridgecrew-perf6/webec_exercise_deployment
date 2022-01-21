package ch.fhnw.webec.booklist.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class ProfanityService {
    private List<String> badWords = Arrays.asList(
        "what the heck",
        "heck yeah",
        "heck",
        "arse",
        "bum",
        "darn",
        "dang",
        "son of a gun",
        "bull snot"
    );

    public boolean containsBadWords(String text) {
        return this.badWords.stream().anyMatch(badWord -> text.toLowerCase().contains(badWord.toLowerCase()));
    }

    public List<String> getBadWords() {
        return badWords;
    }

    public void setBadWords(List<String> badWords) {
        this.badWords = badWords;
    }

    public void addBadWord(String badWord) {
        this.badWords.add(badWord);
    }
}
