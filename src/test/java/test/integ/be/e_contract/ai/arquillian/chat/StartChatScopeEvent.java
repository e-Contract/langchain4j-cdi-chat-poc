package test.integ.be.e_contract.ai.arquillian.chat;

import jakarta.enterprise.concurrent.ContextService;

public class StartChatScopeEvent {

    private final String identifier;

    private final ContextService contextService;

    private final boolean onManagedThread;

    public StartChatScopeEvent(String identifier, ContextService contextService, boolean onManagedThread) {
        this.identifier = identifier;
        this.contextService = contextService;
        this.onManagedThread = onManagedThread;
    }

    public StartChatScopeEvent(String identifier, boolean onManagedThread) {
        this(identifier, null, onManagedThread);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public ContextService getContextService() {
        return this.contextService;
    }

    public boolean isOnManagedThread() {
        return this.onManagedThread;
    }
}
