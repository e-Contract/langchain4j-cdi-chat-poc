package test.integ.be.e_contract.ai.arquillian.chat;

public class StopChatScopeEvent {

    private final String identifier;

    public StopChatScopeEvent(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
