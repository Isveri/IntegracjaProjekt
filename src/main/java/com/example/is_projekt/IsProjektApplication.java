package com.example.is_projekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IsProjektApplication {
    /**
     * Działanie apki:
     * Pobranie danych z csv i XML i zapisanie ich do bazy danych, mozliwosc pobrania plików XML i JSON zawierających dane z bazy
     * ORM relacja ManyToOne w statystykach,
     * export i import z bazy danych done.
     * //TODO zrobic SOAP do przesyłania wielkosci wojewodztwa w pliku JSON
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(IsProjektApplication.class, args);
    }

}
