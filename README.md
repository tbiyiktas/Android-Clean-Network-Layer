# Android Clean Network Layer 🚀

This repository provides a robust and clean network layer implementation for Android applications, designed with modularity, testability, and maintainability in mind. It avoids third-party HTTP libraries, relying purely on `HttpURLConnection` for core network operations.

---

## 📦 Packaging

This repository is source-first. You can integrate it into your project by copying the `lib` module directly into your project's `app/src/main/java/` directory or by packaging it as an AAR (Android Archive) and including it as a dependency.

---

## ✨ Features

* **No third-party HTTP libs:** Built entirely using pure `HttpURLConnection` for core networking.
* **Producer–Consumer queue (bounded):** Prevents Out Of Memory (OOM) errors under heavy load by limiting concurrent requests.
* **Cancellation:** Every network call returns a `RequestHandle` allowing you to cancel ongoing requests with `.cancel()`.
* **Per-request timeouts:** Override global connect/read timeouts for specific requests when needed.
* **Retry & backoff:** Implements automatic retry with exponential backoff for idempotent methods (GET, PUT, DELETE) on `IOException`, HTTP 429 (Too Many Requests – honoring `Retry-After` header), and 503 (Service Unavailable) responses.
* **Gzip support:** Automatically handles compressed responses (Content-Encoding: gzip) for both success and error streams.
* **PATCH compatibility:** Includes a reflection-based fallback for `PATCH` method support on older Android versions that might not natively support it.
* **Typed responses:** Responses are encapsulated in a `NetResult<T>` sealed class, providing clear `Success` and `Error` branches for easy handling.

---

## 🚀 Quick Start

### 1) Initialize (Optional Singleton)

You can initialize `NetworkManager` as a singleton within your `Application` class:

```java
public class MyApplication extends Application {
    private static MyApplication instance;
    private NetworkManager network;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        // Replace with your actual base URL
        network = NetworkManager.create("[https://jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com)"); 
    }

    public static NetworkManager network() { 
        return instance.network; 
    }
}
Or, create an ad-hoc instance wherever needed:

Java

NetworkManager api = NetworkManager.create("[https://jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com)");
2) Make Requests
Ensure you have your data models (e.g., Todo class) defined in your model package.

GET Request
Java

// Assuming 'api' is an instance of NetworkManager or ABaseApi subclass
api.get("/todos/1", /* queryParams */ null, /* headers */ null, Todo.class,
    new NetworkCallback<Todo>() {
        @Override 
        public void onResult(NetResult<Todo> result) {
            if (result.isSuccess()) {
                Todo data = result.Data(); // Use .Data() as per NetResult class
                Log.d("API", "Title: " + data.title);
            } else {
                Log.e("API", "Error: " + result.getErrorBody() + " Code: " + result.getResponseCode());
            }
        }
    });
Cancelable GET Request
Java

RequestHandle handle = api.get("/todos/1", null, null, Todo.class,
    new NetworkCallback<Todo>() {
        @Override 
        public void onResult(NetResult<Todo> result) { 
            /* Handle result */ 
        }
    });

// ...later in your app logic
handle.cancel(); // Cancel the ongoing request
POST Request (Body as JSON String)
Java

String body = new org.json.JSONObject()
        .put("title", "Android Network Layer")
        .put("completed", false)
        .toString();

api.post("/todos", body, /* headers */ null, Todo.class,
    new NetworkCallback<Todo>() {
        @Override 
        public void onResult(NetResult<Todo> result) {
            if (result.isSuccess()) {
                Log.d("API", "Created todo with id: " + result.Data().id);
            } else {
                Log.e("API", "Error creating todo: " + result.getErrorBody());
            }
        }
    });
PUT / PATCH / DELETE Requests
Java

// PUT Request
api.put("/todos/1", body, /* headers */ null, Todo.class, new NetworkCallback<Todo>()  { 
    @Override 
    public void onResult(NetResult<Todo> r) { /* Handle result */ }
});

// PATCH Request
api.patch("/todos/1", body, /* headers */ null, Todo.class, new NetworkCallback<Todo>() { 
    @Override 
    public void onResult(NetResult<Todo> r) { /* Handle result */ }
});

// DELETE Request
api.delete("/todos/1", /* headers */ null, String.class, new NetworkCallback<String>() { 
    @Override 
    public void onResult(NetResult<String> r) { /* Handle result */ }
});
🚦 Error Handling
Every network callback receives a NetResult<T> object, which is a sealed class representing either a success or an error:

result.isSuccess() / result.isError(): Check the status of the request.

result.Data(): Access the parsed data on success.

result.getException(): Get the underlying exception on error.

result.getResponseCode(): Get the HTTP status code on error.

result.getErrorBody(): Get the raw error response body (if available) on error.

If the request queue is full, enqueueRequest returns an immediate NetResult.Error with a 429 status code (e.g., "Queue full / 429").

⚙️ Configuration
Default network settings are defined in the NetworkConfig class. These can be easily adjusted to suit your application's needs.

Setting	Purpose
QUEUE_CAPACITY	Bounded queue size to prevent memory pressure.
THREAD_POOL_SIZE	Number of worker threads to process requests concurrently.
CONNECT_TIMEOUT_MS	Default connection timeout in milliseconds; can be overridden per request.
READ_TIMEOUT_MS	Default read timeout in milliseconds; can be overridden per request.
RETRY_LIMIT	Maximum number of retries for idempotent requests.
INITIAL_RETRY_DELAY_MS	Initial delay before the first retry (exponential backoff). Honors Retry-After HTTP header.

E-Tablolar'a aktar
📐 Architecture (PlantUML)
This diagram illustrates the main components and their interactions within the network layer.

Kod snippet'i

@startuml
skinparam classAttributeIconSize 0

interface "NetworkCallback<T>" as NetworkCallbackT
abstract class "NetResult<T>" as NetResultT
class "NetResult.Success<T>" as NetResultSuccessT
class "NetResult.Error<T>" as NetResultErrorT

class NetworkManager {
  - requestExecutor : ExecutorService
  - requestQueue : BlockingQueue<RequestTask>
  - mainHandler : Handler
  - responseHandler : ResponseHandler
  - connectionFactory : IHttpConnectionFactory
  - basePath : String
  + get(...)
  + post(...)
  + put(...)
  + patch(...)
  + delete(...)
  + upload(...)
  - enqueueRequest(...)
}

abstract class "ACommand" as ACommand
class "GetCommand" as GetCommand
class "PostCommand" as PostCommand
class "PutCommand" as PutCommand
class "PatchCommand" as PatchCommand
class "DeleteCommand" as DeleteCommand
class "MultipartCommand" as MultipartCommand

interface "IHttpConnection" as IHttpConnection
class "HttpConnectionAdapter" as HttpConnectionAdapter
class "HttpsConnectionAdapter" as HttpsConnectionAdapter
interface "IHttpConnectionFactory" as IHttpConnectionFactory
class "HttpUrlConnectionFactory" as HttpUrlConnectionFactory

class "ResponseHandler" as ResponseHandler
interface "IResponseParser" as IResponseParser
class "GsonResponseParser" as GsonResponseParser

class "UrlBuilder" as UrlBuilder
class "NetworkConfig" as NetworkConfig
class "RequestCancelledException" as RequestCancelledException

NetworkCallbackT : + onResult(result : NetResultT)
NetResultSuccessT --|> NetResultT
NetResultErrorT   --|> NetResultT

NetworkManager o-- ResponseHandler        : uses
NetworkManager o-- IHttpConnectionFactory : uses
NetworkManager --> ACommand             : enqueues/uses
NetworkManager ..> NetResultT           : returns

ResponseHandler o-- IResponseParser      : delegates to
GsonResponseParser ..|> IResponseParser

ACommand ..> IHttpConnection            : uses to execute
ACommand ..> NetworkConfig              : uses for timeouts/retry
ACommand ..> NetResultT                 : returns

HttpUrlConnectionFactory ..|> IHttpConnectionFactory
HttpUrlConnectionFactory --> HttpConnectionAdapter : creates Http connections
HttpUrlConnectionFactory --> HttpsConnectionAdapter : creates Https connections

HttpConnectionAdapter ..|> IHttpConnection
HttpsConnectionAdapter ..|> IHttpConnection

GetCommand --|> ACommand
PostCommand --|> ACommand
PutCommand --|> ACommand
PatchCommand --|> ACommand
DeleteCommand --|> ACommand
MultipartCommand --|> ACommand

NetworkManager o-- "RequestTask" : contains

@enduml
🤝 Contributing
We welcome contributions! Please follow these steps to contribute:

Fork the repository.

Create a new branch for your feature or bug fix.

Submit a Pull Request (PR) with your changes.

Please include a brief description of your changes, and if relevant, a small test or sample to demonstrate the new functionality or fix.

📄 License
This project is licensed under the MIT License. See the LICENSE file for more details.
