package com.example.jkutner.herokuandroid;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookService {
    @GET("books")
    Call<List<Book>> all();

    @GET("books/{isbn}")
    Call<Book> getUser(@Path("isbn") String isbn);

    @POST("books/new")
    Call<Book> create(@Body Book book);
}
