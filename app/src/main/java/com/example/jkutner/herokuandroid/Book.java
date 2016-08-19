package com.example.jkutner.herokuandroid;

import com.google.gson.annotations.SerializedName;

public class Book {

    @SerializedName("id")
    int id;

    @SerializedName("isbn")
    String isbn;

    public Book(int id, String isbn) {
        this.id = id;
        this.isbn = isbn;
    }

    public Book(String isbn) {
        this.isbn = isbn;
    }
}
