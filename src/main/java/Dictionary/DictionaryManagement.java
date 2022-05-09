package Dictionary;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import static Dictionary.Dictionary.wordInDictionary;

public class DictionaryManagement  {

    public DictionaryManagement() {
    }

    private int numOfWord = 0;

    public void setNumOfWord(int num) {
        this.numOfWord=num;
    }

    public int getNumOfWord() {
        return this.numOfWord;
    }

    public void insertFromCommandline() {
        String word_target, word_explain;
        Scanner ip = new Scanner(System.in);
        int numLine = ip.nextInt();
        ip.nextLine();
        System.out.println(numLine);
        for (int i = 0; i < numLine; i++) {
            word_target = ip.nextLine();
            word_explain = ip.nextLine();
            Word word = new Word(word_target, word_explain);
            wordInDictionary.add(word);
        }
    }

    //Read from file
    public static void insertFromFile() {
        try {
            File myFile = new File("src\\data\\dictionaries.txt");
            FileReader fr = new FileReader(myFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                int index = line.indexOf("\t");
                String word_target = line.substring(0, index);
                String word_explain = line.substring(index + 1);
                wordInDictionary.add(new Word(word_target, word_explain));
            }
            fr.close();
            br.close();
        } catch (Exception ex) {
            System.out.println("Loi doc file: " + ex);
        }
    }
    //Read from database
    public void insertFromDatabase() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM av;" );
            while(rs.next()) {
                String eng_word = rs.getString("word");
                String vie_word = rs.getString("description");
                Word newWord = new Word(eng_word, vie_word);
                wordInDictionary.add(newWord);
                ++numOfWord;
            }
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    //lookup commandline
    public static void dictionaryLookup() {
        System.out.println("Enter a word you want to look up:");
        Scanner scanner = new Scanner(System.in);
        String answer = "There's no such word";
        String wordLookUp = scanner.nextLine();
        for (Word word : wordInDictionary) {
            if (wordLookUp.equals(word.getWord_target())
                    || wordLookUp.equals(word.getWord_explain())) {
                answer = word.toString();
            }
        }
        System.out.println(answer);
    }

    //look up a word in database
    public String lookup(String search_word) {
        int i;
        for(i=0; i< getNumOfWord(); i++){
            if(wordInDictionary.get(i).getWord_target().equals(search_word)){
                break;
            }
        }
        return wordInDictionary.get(i).getWord_explain();
    }

    //look up words start with preWord
    public ArrayList preLookup (String preWord) {
        ArrayList<String> preWords = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "SELECT * FROM av;";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next() ) {
                String word = rs.getString("word");
                if(word.startsWith(preWord)) {
                    preWords.add(word);
                }
            }
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return preWords;
    }
    //add to txt
    public void addWordToDictionary() {
        System.out.println("Enter word target and word explain you want to add: ");
        Scanner scanner = new Scanner(System.in);
        String wordTarget = scanner.nextLine();
        String wordExplain = scanner.nextLine();
        wordInDictionary.add(new Word(wordTarget, wordExplain));
    }

    //add to database
    public void add(Word addWord) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "INSERT INTO av (id, word, html, description, pronounce) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(2, addWord.getWord_target());
            stmt.setString(3, addWord.getWord_explain());
            stmt.execute();
            stmt.close();
            c.commit();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //edit in txt
    public void editWordInDictionary() {
        System.out.println("Enter word target or word explain you want to edit:");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        boolean isExist = false;
        for (Word word : wordInDictionary) {
            if (str.equals(word.getWord_explain())) {
                System.out.println("Enter a new word target:");
                String newWordTarget = scanner.nextLine();
                word.setWord_target(newWordTarget);
                isExist = true;
                break;
            } else if (str.equals(word.getWord_target())) {
                System.out.println("Enter a new word explain:");
                String newWordExplain = scanner.nextLine();
                word.setWord_explain(newWordExplain);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            System.out.println("The word you enter is invalid.");
        }
    }

    //edit in database
    public void edit(Word editWord, Word originalWord) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql1 = "SELECT *" + "FROM av WHERE word = ?";
            PreparedStatement stmt1 = c.prepareStatement(sql1);
            stmt1.setString(1, originalWord.getWord_target());
            ResultSet rs = stmt1.executeQuery();
            int id = rs.getInt("id");
            String sql2 = "UPDATE av SET word = ?," + "html = ?" + "WHERE id = ?";
            PreparedStatement stmt2 = c.prepareStatement(sql2);
            stmt2.setString(1, editWord.getWord_target());
            stmt2.setString(2, editWord.getWord_explain());
            stmt2.setInt(3, id);
            stmt2.executeUpdate();
            stmt1.close();
            stmt2.close();
            rs.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //delete in txt
    public void deleteDictionary() {
        System.out.println("Enter a word you want to delete:");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        for (Word word : wordInDictionary) {
            if (str.equals(word.getWord_target())) {
                wordInDictionary.remove(word);
                break;
            }
        }
    }

    //delete in database
    public void delete(Word removeWord) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "DELETE FROM av WHERE word = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, removeWord.getWord_target());
            stmt.executeUpdate();
            stmt.close();
            c.commit();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

/*
    public void dictionarySearcher() {
        System.out.println("Enter a word you want to search:");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        List<Word> wordsList = new ArrayList<Word>();
        for (Word word : Dictionary.wordInDictionary) {
            if (word.getWord_target().startsWith(str)) {
                wordsList.add(new Word(word.getWord_target(), word.getWord_explain()));
            }
        }
        System.out.println("No\t| English\t|Vietnamese");
        for (int i = 0; i < wordsList.size(); i++) {
            System.out.println((i + 1) + "\t" + wordsList.get(i).toString());
        }
    }
*/
    public static ArrayList<String> dictionarySearcher(String key) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < wordInDictionary.size(); i++) {
            if (wordInDictionary.get(i).getWord_target().indexOf(key) == 0) {
                result.add(wordInDictionary.get(i).getWord_target());
            }
        }
        return result;
    }

    public static void dictionaryExportToFile() throws IOException {
        FileWriter writer = new FileWriter("src\\data\\dictionary.txt");
        for (Word word : wordInDictionary) {
            writer.write(String.format("%s\t%s\n", word.getWord_target(), word.getWord_explain()));
        }
        writer.close();
    }
}