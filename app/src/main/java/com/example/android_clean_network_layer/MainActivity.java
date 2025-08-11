package com.example.android_clean_network_layer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import lib.api.TodoApi;
import lib.model.Todo;
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
        startRequestsButton.setOnClickListener(v -> startRequests());
    }

    private void startRequests() {
        startRequestsButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText("İstekler başlatılıyor...");

        requestsCompleted = 0;
        totalRequests = 1;

        //GET item
//        NetworkManager exampleApiClient = NetworkManager.create("https://jsonplaceholder.typicode.com");
//
//        exampleApiClient.get("/todos/1", null, Todo.class, new NetworkCallback<Todo>() {
//            @Override
//            public void onResult(NetResult<Todo> result) {
//                Log.d(TAG, "Hatalı API isteği tamamlandı.");
//                handleResult(result);
//            }
//        });


        // GET LIST
//        Type todoListType = new TypeToken<List<Todo>>() {}.getType();
//
//        NetworkManager exampleApiClient = NetworkManager.create("https://jsonplaceholder.typicode.com");
//
//        exampleApiClient.get("/todos", null, null, todoListType, new NetworkCallback<List<Todo>>() {
//            @Override
//            public void onResult(NetResult<List<Todo>> result) {
//                // Log mesajını da doğru duruma göre düzenleyelim
//                Log.d(TAG, "API isteği tamamlandı.");
//                handleResult(result);
//            }
//        });

        TodoApi api = new TodoApi();

        api.getTodos(new NetworkCallback<List<Todo>>() {
            @Override
            public void onResult(NetResult<List<Todo>> result) {
//                if (result instanceof NetResult.Success) {
//                    List<Todo> todos = ((NetResult.Success<List<Todo>>) result).Data();
//                    System.out.println("Başarılı: " + todos.size() + " adet todo alındı.");
//
//                    // İlk 5 tanesini ekrana yazdıralım
//                    for (int i = 0; i < 5 && i < todos.size(); i++) {
//                        System.out.println(todos.get(i));
//                    }
//                } else if (result instanceof NetResult.Error) {
//                    NetResult.Error<?> error = (NetResult.Error<?>) result;
//                    int responseCode = error.getResponseCode();
//                    String errorBody = error.getErrorBody();
//
//                    if (responseCode == 404) {
//                        Log.e(TAG, "Hata oluştu: Kaynak bulunamadı (404) - " + errorBody);
//                        statusTextView.setText("Hata: Kaynak bulunamadı!");
//                    } else {
//                        Log.e(TAG, "Hata oluştu: " + error.getException().getMessage() + " (Kod: " + responseCode + ")");
//                        statusTextView.setText("Hata: " + error.getException().getMessage());
//                    }
//                }

                handleResult(result);
            }
        });

        //https://10.0.2.2:7122/api/Lookup?pageNumber=1&pageSize=10&withDeleted=false
//        NetworkManager localApi = NetworkManager.create("https://10.0.2.2:7122");
//        HashMap<String, String> queryParams = new HashMap<>();
//        queryParams.put("pageNumber", "1");
//        queryParams.put("pageSize", "10");
//        queryParams.put("withDeleted", "false");
//        Type lookupListType = new TypeToken<List<Lookup>>() {}.getType();
//
//        localApi.get(
//                "/api/Lookup",
//                queryParams,
//                null, // headers
//                lookupListType,
//                new NetworkCallback<List<Lookup>>() {
//                    @Override
//                    public void onResult(NetResult<List<Lookup>> result) {
//                        handleResult(result);
//                    }
//                }
//        );

    }

    private void handleResult(NetResult<?> result) {
        requestsCompleted++;

        if (result.isSuccess()) {
//            statusTextView.setText(String.format("İstek tamamlandı (%d/%d)", requestsCompleted, totalRequests));
//            Todo todo = (Todo) result.Data();
//            if (todo != null) {
//                Log.d(TAG, "Başarılı yanıt (ayrıştırılmış obje): " + todo.toString());
//            } else {
//                Log.d(TAG, "Başarılı yanıt, ancak veri boş geldi.");
//            }
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