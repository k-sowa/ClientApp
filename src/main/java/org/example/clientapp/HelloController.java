package org.example.clientapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HelloController {
    //Adnotacja @FXML do każdej zmiennej z view.fxml
    @FXML
    private ListView<String> wordList; // Komponent ListView w interfejsie użytkownika,
    // który wyświetla listę słów otrzymanych z serwera

    @FXML
    private Label wordCountLabel; // Komponent Label, który wyświetla ilość słów (lub inne informacje),
    // może być aktualizowany na bieżąco

    @FXML
    private TextField filterField; // Pole tekstowe, w którym użytkownik wpisuje filtr,
    // na podstawie którego lista słów jest filtrowana

    private List<String> allWords = new ArrayList<>();
    //Lista z wszystkimi słowami
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    //Stworzenie prostego formatu czasowego aby wyświetlić czas w formacie HH:mm:ss

    public void initialize() {
        // Dodajemy nasłuchiwacza (listener) na zmianę tekstu w polu tekstowym 'filterField'
        // Kiedy użytkownik wpisze coś nowego w polu tekstowym, metoda 'filterWords' zostanie wywołana z nową wartością filtra (newValue)
        filterField.textProperty().addListener((observable, oldValue, newValue) -> filterWords(newValue));

        // Inicjujemy połączenie z serwerem - metoda 'connectToServer' uruchamia wątek, który nawiązuje połączenie i odbiera dane
        connectToServer();
    }
    //inicjalizacja aplikacji
    private void connectToServer() {
        new Thread(() -> {
            //Utworzenie nowego wątku
            try (Socket socket = new Socket("localhost", 5000);
                 //podłączenie do serwera na porcie 5000
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String word;
                while ((word = reader.readLine()) != null) {
                    //Pętla do odbierania danych
                    String timestampedWord = timeFormat.format(new Date()) + " " + word;
                    allWords.add(timestampedWord);
                    //Każda linia danych (word) otrzymuje znacznik czasu (w formacie daty i czasu).
                    // Ta linia zostaje dodana do listy allWords.
                    // Ponieważ modyfikowanie elementów interfejsu użytkownika
                    // musi odbywać się w głównym wątku aplikacji (np. w przypadku aplikacji JavaFX)
                    Platform.runLater(() -> updateWordList());
                    //Platform.runLater() używana jest do wywołania metody updateWordList()
                    // , która zaktualizuje widok listy słów.
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateWordList() {
        // Aktualizacja licznika
        wordCountLabel.setText(String.valueOf(allWords.size()));

        // Filtrowanie i sortowanie
        filterWords(filterField.getText());
    }

    private void filterWords(String filter) {
        // Tworzymy nową listę, która będzie przechowywać przefiltrowane słowa
        List<String> filteredWords = new ArrayList<>();

        // Przechodzimy przez wszystkie słowa z listy allWords
        for (String word : allWords) {
            // Rozdzielamy tekst na dwie części: czas i faktyczne słowo
            String[] parts = word.split(" ", 2);
            String actualWord = parts[1]; // Słowo po czasie

            // Sprawdzamy, czy filtr jest pusty, lub czy słowo zaczyna się od podanego filtru
            if (filter.isEmpty() || actualWord.startsWith(filter)) {
                // Jeśli spełnia warunki, dodajemy słowo do listy przefiltrowanych
                filteredWords.add(word);
            }
        }

        // Sortujemy listę przefiltrowanych słów alfabetycznie względem faktycznych słów (ignorując część z czasem)
        Collections.sort(filteredWords, (a, b) -> a.split(" ", 2)[1].compareTo(b.split(" ", 2)[1]));

        // Aktualizujemy widok listy słów w interfejsie użytkownika, ustawiając nową, przefiltrowaną listę
        wordList.getItems().setAll(filteredWords);
    }
}