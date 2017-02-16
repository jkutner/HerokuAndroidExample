package com.example.jkutner.herokuandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.models.StormpathError;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

        String baseUrl = "https://shrouded-brook-35188.herokuapp.com";
        StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                .baseUrl(baseUrl)
                .build();
        Stormpath.init(this, stormpathConfiguration);

        Stormpath.login("foo@bar.com", "Pa55word", new StormpathCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) { }

            @Override
            public void onFailure(StormpathError error) { }
        });

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        System.out.println("token: " + Stormpath.getAccessToken());
                        ongoing.addHeader("Accept", "application/json");
                        ongoing.addHeader("Authorization", "Bearer " + Stormpath.getAccessToken());
                        return chain.proceed(ongoing.build());
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
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
