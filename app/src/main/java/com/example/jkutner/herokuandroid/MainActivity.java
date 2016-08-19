package com.example.jkutner.herokuandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText isbnInput;
    private TextView textView;
    private Button button;
    private Button viewAllButton;
    private TextView allBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        isbnInput = (EditText) findViewById(R.id.isbnInput);
        viewAllButton = (Button) findViewById(R.id.viewAllButton);
        allBooks = (TextView) findViewById(R.id.allBooks);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://2d34cd05.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final BookService service = retrofit.create(BookService.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book(isbnInput.getText().toString());
                Call<Book> createCall = service.create(book);
                createCall.enqueue(new Callback<Book>() {
                    @Override
                    public void onResponse(Call<Book> _, Response<Book> response) {
                        Book newBook = response.body();
                        textView.setText("Created Book with ISBN: " + newBook.isbn);
                    }

                    @Override
                    public void onFailure(Call<Book> _, Throwable t) {
                        t.printStackTrace();
                        textView.setText(t.getMessage());
                    }
                });
            }
        });

        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Book>> createCall = service.all();
                createCall.enqueue(new Callback<List<Book>>() {
                    @Override
                    public void onResponse(Call<List<Book>> _, Response<List<Book>> response) {
                        allBooks.setText("ALL BOOKS by ISBN:\n");
                        for (Book b : response.body()) {
                            allBooks.append(b.isbn + "\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Book>> _, Throwable t) {
                        t.printStackTrace();
                        allBooks.setText(t.getMessage());
                    }
                });
            }
        });
    }
}
