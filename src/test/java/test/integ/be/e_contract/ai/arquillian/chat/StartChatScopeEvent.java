package test.integ.be.e_contract.ai.arquillian.chat;

import jakarta.enterprise.concurrent.ContextService;

public class StartChatScopeEvent {

    private final String identifier;

    private final ContextService contextService;

    public StartChatScopeEvent(String identifier, ContextService contextService) {
        this.identifier = identifier;
        this.contextService = contextService;
    }

    public StartChatScopeEvent(String identifier) {
        this(identifier, null);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public ContextService getContextService() {
        return this.contextService;
    }
}
