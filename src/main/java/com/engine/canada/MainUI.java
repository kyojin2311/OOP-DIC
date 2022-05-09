package com.engine.canada;

import Dictionary.DictionaryManagement;
import Dictionary.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.engine.canada.EnglishToPigLatin.toPigLatin;
import static com.engine.canada.PigLatinToEnglish.toEnglish;

public class MainUI extends DictionaryManagement {

    @FXML
    private TextField WordSearch = new TextField();

    @FXML
    private WebView WordExplain = new WebView();

    //event to search
    @FXML
    public void Search (ActionEvent event) {
        listView.setVisible(false);
        String word_search = WordSearch.getText();
        WebEngine webEngine = WordExplain.getEngine();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "SELECT *" + "FROM av WHERE word = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, word_search);
            ResultSet rs = stmt.executeQuery();
            String vie_word = rs.getString("html");
            webEngine.loadContent(vie_word, "text/html");
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //ListView
    @FXML
    private ListView<String> listView = new ListView<>();

    ObservableList<String> listWord = FXCollections.observableArrayList();

    public void loadPre(String preWord) {
        listWord.clear();
        ArrayList<String> preWords = Main.dm.preLookup(preWord);
        for(int i=0; i< preWords.size(); i++) {
            listWord.add(preWords.get(i));
        }
    }

    //TO DO
    public void initialize() {
        listView.setVisible(false);
        listView.setItems(listWord);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        WordSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                loadPre(newValue);
                listView.setVisible(true);
            } else {
                listWord.clear();
            }
        });
    }

    //select word from listview
    public void listViewPushed(MouseEvent event) {
        String wordSearch = listView.getSelectionModel().getSelectedItem();
        WordSearch.setText(wordSearch);
        WebEngine webEngine = WordExplain.getEngine();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "SELECT *" + "FROM av WHERE word = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, wordSearch);
            ResultSet rs = stmt.executeQuery();
            String vie_word = rs.getString("html");
            webEngine.loadContent(vie_word, "text/html");
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        listView.setVisible(false);
    }

    //Tab Add
    @FXML
    private TextField addWord = new TextField();
    @FXML
    private TextField addWordExplain = new TextField();

    //add a word
    public void add(ActionEvent event) {
        Word add_word = new Word(addWord.getText(),addWordExplain.getText());
        Main.dm.add(add_word);
        //Main.dm.addTodm(add_word);
        addWord.clear();
        addWordExplain.clear();
        WebEngine webEngine = WordExplain.getEngine();
        webEngine.loadContent("");
    }

    //Tab Edit
    @FXML
    private TextField editWord = new TextField();
    @FXML
    private TextField editWordExplain = new TextField();

    //edit a word
    public void edit(ActionEvent event) {
        String original_explain = Main.dm.lookup(WordSearch.getText());
        Word original_word = new Word(WordSearch.getText(), original_explain);
        Word edit_word = new Word(editWord.getText(), editWordExplain.getText());
        Main.dm.edit(edit_word, original_word);
        editWord.clear();
        editWordExplain.clear();
        WordSearch.clear();
        WebEngine webEngine = WordExplain.getEngine();
        webEngine.loadContent("");
    }

    //delete
    public void delete(ActionEvent event) {
        String remove_explain = Main.dm.lookup(WordSearch.getText());
        Word remove_word = new Word(WordSearch.getText(), remove_explain);
        Main.dm.delete(remove_word);
        WordSearch.clear();
        WebEngine webEngine = WordExplain.getEngine();
        webEngine.loadContent("");
    }

    //translate Tab
    @FXML
    TextField search = new TextField();
    @FXML
    TextArea explain = new TextArea();

    //get api from google translate english to Vietnamese
    public void translateEV(ActionEvent event) throws IOException {
        String e_word = search.getText();
        String output = "";
        output = toPigLatin(e_word);
        explain.setText(output);
    }

    //get api from google translate Vietnamese to english
    public void translateVE(ActionEvent event) throws IOException {
        String vi_word = search.getText();
        String output = "";
        output = toEnglish(vi_word);
        explain.setText(output);
    }
}
