# Proof-of-concept for CDI based chat scope

What do we want to achieve?
* using CDI events instead of direct usage of `TokenStream`. This way the Jakarta EE "model", where the AI code lives, has a way to communicate towards the frond-end code.
* introduce a `@ChatScoped` CDI scope that corresponds with the lifecycle of the chat session.
* ensure that all fired CDI events and tool invocations run within the right Jakarta EE context.


## Architecture

The problem is that `TokenStream` callbacks get executed on threads not related to Java EE.
Hence if you directly try to interact with Java EE from within these callbacks, things quickly go south.

Via the `ChatService` we decorate a `TokenStream` to be able to have the `TokenStream` callbacks propagate execution on Java EE threads with the proper context. Within the right thread/context we let `TokenStream` fire CDI events that can be observed anywhere within the application.

As part of a chat session, we manage a corresponding `@ChatScoped` CDI scope.
This `@ChatScoped` CDI scope is activated via `StartChatScopeEvent` each time the `TokenStream` wants to interact via the corresponding callbacks since most likely `ManagedExecutorService` will give us a different thread each time.
The `@ChatScoped` CDI scope is only destroyed via `StopChatScopeEvent` once the chat ends with `TokenStream.onCompleteResponse` or `TokenStream.onError`.
This mechanism allows all `@ChatScoped` beans to keep state as part of the chat session.


## Integration Tests

Running the integration tests requires a local running WildFly.
We tested on WildFly version 38.0.1.Final.
Start WildFly as follows:
```
cd wildfly-38.0.1.Final/bin
./standalone.sh --server-config=standalone-full.xml
```

Next to that we need an LLM.
Serve `gpt-oss` locally via:
```
llama-server -hf unsloth/gpt-oss-20b-GGUF:Q4_K_M --port 8081
```

Run the integration tests via:
```
mvn clean test -Pintegration-tests
```

You might need to change the `StreamingChatModel` configuration within `ChatTestBean.java` to use another LLM provider.
