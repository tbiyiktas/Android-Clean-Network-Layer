# Android Clean Network Layer

This library provides a **URLConnection**-based network layer for Android, without using any third-party HTTP libraries. It implements a **Producer–Consumer pattern** with generic type support for request and response handling.

## Features

* Generic type support for `TRequest` and `TResponse`
* Request queue management using Producer–Consumer pattern
* Thread-safe architecture
* Cancellable requests (`RequestHandle.cancel()`)
* Simple JSON conversion
* Configurable timeouts

## Usage

### Initialization

```java
NetworkManager api = NetworkManager.create("https://jsonplaceholder.typicode.com");
```

### GET

```java
api.get("/todos/1", null, Todo.class, new NetworkCallback<Todo>() {
    @Override public void onResult(NetResult<Todo> result) {
        if (result.isSuccess()) {
            Todo data = result.getData();
            Log.d("API", "Success: " + data.getTitle());
        } else {
            Log.e("API", "Error: " + result.getError());
        }
    }
});
```

### Cancellable GET

```java
RequestHandle handle = api.get("/todos/1", null, Todo.class, new NetworkCallback<Todo>() {
    @Override public void onResult(NetResult<Todo> result) {
        // handle result
    }
});

handle.cancel();
```

### POST (String JSON)

```java
String body = new JSONObject()
        .put("title", "Algebra 1")
        .put("completed", false)
        .toString();

api.post("/todos", body, Todo.class, new NetworkCallback<Todo>() {
    @Override public void onResult(NetResult<Todo> result) {
        if (result.isSuccess()) {
            Log.d("API", "Created: " + result.getData().getId());
        }
    }
});
```

### PUT / PATCH

```java
api.put("/todos/1", body, Todo.class, new NetworkCallback<Todo>() { /* ... */ });
api.patch("/todos/1", body, Todo.class, new NetworkCallback<Todo>() { /* ... */ });
```

### DELETE

```java
api.delete("/todos/1", null, Void.class, new NetworkCallback<Void>() {
    @Override public void onResult(NetResult<Void> result) {
        if (result.isSuccess()) {
            Log.d("API", "Deleted");
        }
    }
});
```

## Error Handling

* Use `isError()` and `getError()` to get error details.
* If the request queue is full, an error is returned immediately.

## License

MIT
