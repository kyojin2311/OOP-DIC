package Dictionary;

public class DictionaryCommandLine  {
    public void showAllWords() {
        System.out.println("No\t| English\t|Vietnamese");
        for (int i = 0; i < Dictionary.wordInDictionary.size(); i++) {
            System.out.println((i + 1) + "\t" + Dictionary.wordInDictionary.get(i).toString());
        }
    }

    public void dictionaryBasic() {
        DictionaryManagement insertDictionary = new DictionaryManagement();
        insertDictionary.insertFromCommandline();
        this.showAllWords();
    }

    public void dictionaryAdvanced() {
        DictionaryManagement dictionary2 = new DictionaryManagement();
        dictionary2.insertFromFile();
        this.showAllWords();
        dictionary2.dictionaryLookup();
    }
}