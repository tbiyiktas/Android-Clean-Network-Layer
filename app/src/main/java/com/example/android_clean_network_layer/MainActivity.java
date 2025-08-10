package com.example.android_clean_network_layer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.io.IOException;

import lib.net.NetResult;
import lib.net.NetworkCallback;
import lib.net.NetworkManager;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }

    private static final String TAG = "MainActivity";
    private TextView statusTextView;
    private ProgressBar progressBar;
    private Button startRequestsButton;

    private int totalRequests = 0;
    private int requestsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);
        startRequestsButton = findViewById(R.id.startRequestsButton);

        //NetworkManager.startWorkers();

        startRequestsButton.setOnClickListener(v -> startRequests());
    }

    private void startRequests() {
        startRequestsButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText("İstekler başlatılıyor...");

        requestsCompleted = 0;
        totalRequests = 1;

        // 2. Bir Hata Durumu (404)
        NetworkManager exampleApiClient = NetworkManager.create("https://jsonplaceholder.typicode.com");

        exampleApiClient.get("/todos/1", null, Todo.class, new NetworkCallback<Todo>() {
            @Override
            public void onResult(NetResult<Todo> result) {
                Log.d(TAG, "Hatalı API isteği tamamlandı.");
                handleResult(result);
            }
        });
    }

    private void handleResult(NetResult<?> result) {
        requestsCompleted++;

        if (result.isSuccess()) {
            statusTextView.setText(String.format("İstek tamamlandı (%d/%d)", requestsCompleted, totalRequests));
            Todo todo = (Todo) result.Data();
            if (todo != null) {
                Log.d(TAG, "Başarılı yanıt (ayrıştırılmış obje): " + todo.toString());
            } else {
                Log.d(TAG, "Başarılı yanıt, ancak veri boş geldi.");
            }
        } else if (result.isError()) {
            NetResult.Error<?> error = (NetResult.Error<?>) result;
            int responseCode = error.getResponseCode();
            String errorBody = error.getErrorBody();

            if (responseCode == 404) {
                Log.e(TAG, "Hata oluştu: Kaynak bulunamadı (404) - " + errorBody);
                statusTextView.setText("Hata: Kaynak bulunamadı!");
            } else {
                Log.e(TAG, "Hata oluştu: " + error.getException().getMessage() + " (Kod: " + responseCode + ")");
                statusTextView.setText("Hata: " + error.getException().getMessage());
            }
        }

        if (requestsCompleted == totalRequests) {
            progressBar.setVisibility(View.GONE);
            startRequestsButton.setVisibility(View.VISIBLE);
            statusTextView.setText("Tüm istekler tamamlandı!");
        }
    }

}