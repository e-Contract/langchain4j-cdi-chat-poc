# Proof-of-concept for CDI based chat scope

What do we want to achieve?
* using CDI events instead of direct usage of `TokenStream`. This way the Jakarta EE "model", where the AI code lives, has a way to communicate towards the frond-end code.
* introduce a `@ChatScoped` CDI scope that corresponds with the lifecycle of the chat session.
* ensure that all fired CDI events and tool invocations run within the right Jakarta EE context.

## Integration Tests

Running the integration tests requires a local running WildFly.

Run the integration tests via:
```
mvn clean test -Pintegration-tests
```

You might need to change the `StreamingChatModel` configuration within `ChatTestBean.java`.
