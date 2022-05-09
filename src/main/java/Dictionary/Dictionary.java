package Dictionary;

import java.util.ArrayList;

public class Dictionary {
    public static ArrayList<Word> wordInDictionary = new ArrayList<Word>();

    public void addWord(Word word) {
        wordInDictionary.add(word);
    }

    public Dictionary() {

    }

}