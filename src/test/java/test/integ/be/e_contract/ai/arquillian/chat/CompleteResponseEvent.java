package test.integ.be.e_contract.ai.arquillian.chat;

import dev.langchain4j.model.chat.response.ChatResponse;

public class CompleteResponseEvent {

    private final String identifier;

    private final ChatResponse chatResponse;

    private boolean endChatScope;

    public CompleteResponseEvent(String identifier, ChatResponse chatResponse) {
        this.identifier = identifier;
        this.chatResponse = chatResponse;
        this.endChatScope = true;
    }

    public ChatResponse getChatResponse() {
        return this.chatResponse;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public boolean isEndChatScope() {
        return this.endChatScope;
    }

    public void setEndChatScope(boolean endChatScope) {
        this.endChatScope = endChatScope;
    }
}
