package test.integ.be.e_contract.ai.arquillian.chat;

public class PartialResponseEvent {

    private final String identifier;

    private final String partialResponse;

    public PartialResponseEvent(String identifier, String partialResponse) {
        this.identifier = identifier;
        this.partialResponse = partialResponse;
    }

    public String getPartialResponse() {
        return this.partialResponse;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
