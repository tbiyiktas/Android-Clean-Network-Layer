Android Clean Network Layer
A lightweight, clean, and third-party–free HTTP client for Android.
Built on Java’s HttpURLConnection, it uses a Producer–Consumer queue with worker threads, supports generic responses, cancellation, bounded backpressure, per-request timeouts, retries with exponential backoff, gzip, and a clean callback interface.

Requirements
Android: API 21+

Java: 8+

Add the Internet permission:

xml
Copy
Edit
<uses-permission android:name="android.permission.INTERNET" />
Packaging: This repo is source-first. Copy the module into your project or package it as an AAR and include it.

Features
No third-party HTTP libs — pure HttpURLConnection.

Producer–Consumer queue (bounded) — prevents OOM under load.

Cancellation — every call returns a RequestHandle → cancel().

Per-request timeouts — override connect/read timeouts when needed.

Retry & backoff — for idempotent methods (GET/PUT/DELETE) on IOException, 429 (honors Retry-After), 503.

Gzip — handles compressed responses.

PATCH compatibility — reflection fallback for older Android versions.

Typed responses — NetResult<T> with Success / Error branches.

Quick Start
1) Initialize (optional singleton)
java
Copy
Edit
public class MyApplication extends Application {
    private static MyApplication instance;
    private NetworkManager network;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        network = NetworkManager.create("https://jsonplaceholder.typicode.com");
    }

    public static NetworkManager network() { return instance.network; }
}
Or create ad-hoc:

java
Copy
Edit
NetworkManager api = NetworkManager.create("https://jsonplaceholder.typicode.com");
2) Make requests (matches your API)
GET

java
Copy
Edit
api.get("/todos/1", /* queryParams */ null, Todo.class,
    new NetworkCallback<Todo>() {
        @Override public void onResult(NetResult<Todo> result) {
            if (result.isSuccess()) {
                Todo data = result.getData();
                Log.d("API", "Title: " + data.getTitle());
            } else {
                Log.e("API", result.getError());
            }
        }
    });
Cancelable GET

java
Copy
Edit
RequestHandle handle = api.get("/todos/1", null, Todo.class,
    new NetworkCallback<Todo>() {
        @Override public void onResult(NetResult<Todo> result) { /* ... */ }
    });
// later
handle.cancel();
POST (body is String JSON by design)

java
Copy
Edit
String body = new org.json.JSONObject()
        .put("title", "Algebra 1")
        .put("completed", false)
        .toString();

api.post("/todos", body, Todo.class,
    new NetworkCallback<Todo>() {
        @Override public void onResult(NetResult<Todo> result) {
            if (result.isSuccess()) {
                Log.d("API", "Created id: " + result.getData().getId());
            }
        }
    });
Per-request timeouts (if you exposed overloads)

java
Copy
Edit
api.post("/todos", body, /* connect */ 5_000, /* read */ 10_000,
    Todo.class, new NetworkCallback<Todo>() { @Override public void onResult(NetResult<Todo> r) { /* ... */ } });
PUT / PATCH / DELETE

java
Copy
Edit
api.put("/todos/1",  body,  Todo.class,   new NetworkCallback<Todo>()   { @Override public void onResult(NetResult<Todo>   r) { /* ... */ }});
api.patch("/todos/1", body,  Todo.class,   new NetworkCallback<Todo>()   { @Override public void onResult(NetResult<Todo>   r) { /* ... */ }});
api.delete("/todos/1", null, String.class, new NetworkCallback<String>() { @Override public void onResult(NetResult<String> r) { /* ... */ }});
Error Handling
Every callback receives NetResult<T>:

result.isSuccess() / result.isError()

result.getData() on success

result.getError() (message), getResponseCode(), and getException() on error

Queue full: returns an immediate error (e.g., “Queue full / 429”)

Configuration (defaults live in NetworkConfig)
Setting	Purpose
QUEUE_CAPACITY	Bounded queue size to prevent memory pressure.
THREAD_POOL_SIZE	Number of worker threads.
CONNECT_TIMEOUT_MS / READ_TIMEOUT_MS	Default timeouts; can be overridden per request.
RETRY_LIMIT, INITIAL_RETRY_DELAY_MS	Exponential backoff for idempotent requests; honors Retry-After.

Architecture (PlantUML)
Paste into docs/architecture.puml or keep here and render with PlantUML/Kroki.

plantuml
Copy
Edit
@startuml
skinparam classAttributeIconSize 0

interface "NetworkCallback<T>" as NetworkCallbackT
abstract class "NetResult<T>" as NetResultT
class "NetResultSuccess<T>" as NetResultSuccessT
class "NetResultError<T>" as NetResultErrorT

class NetworkManager {
  - requestExecutor : ExecutorService
  - requestQueue : BlockingQueue<RequestTask>
  - mainHandler : Handler
  - responseHandler : ResponseHandler
  - connectionFactory : IHttpConnectionFactory
  - basePath : String
  + get(...), post(...), put(...), patch(...), delete(...), upload(...)
}

NetworkCallbackT : + onResult(result : NetResultT)
NetResultSuccessT --|> NetResultT
NetResultErrorT   --|> NetResultT

NetworkManager *-- ResponseHandler        : has
NetworkManager o-- IHttpConnectionFactory : uses
NetworkManager ..> ACommand               : enqueues
ResponseHandler --> IResponseParser       : delegates
HttpConnectionAdapter ..|> IHttpConnection
HttpUrlConnectionFactory ..|> IHttpConnectionFactory
HttpUrlConnectionFactory --> HttpConnectionAdapter : creates
GetCommand --|> ACommand
PostCommand --|> ACommand
PutCommand --|> ACommand
PatchCommand --|> ACommand
DeleteCommand --|> ACommand
MultipartCommand --|> ACommand
@enduml
Contributing
Fork → branch → PR.
Please include a brief description and, if relevant, a small test or sample.

License
MIT
