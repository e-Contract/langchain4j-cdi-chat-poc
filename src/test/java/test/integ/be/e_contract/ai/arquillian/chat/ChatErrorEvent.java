package test.integ.be.e_contract.ai.arquillian.chat;

public class ChatErrorEvent {

    private final String identifier;

    private final Throwable throwable;

    private boolean endChatScope;

    public ChatErrorEvent(String identifier, Throwable throwable) {
        this.identifier = identifier;
        this.throwable = throwable;
        this.endChatScope = true;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public boolean isEndChatScope() {
        return this.endChatScope;
    }

    public void setEndChatScope(boolean endChatScope) {
        this.endChatScope = endChatScope;
    }
}
