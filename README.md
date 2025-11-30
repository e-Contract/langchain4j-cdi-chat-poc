# Proof-of-concept for CDI based chat scope

Running the integration tests requires a local running WildFly.

Run the integration tests via:
```
mvn clean test -Pintegration-tests
```

You might need to change the `StreamingChatModel` configuration with `ChatTestBean.java`.
