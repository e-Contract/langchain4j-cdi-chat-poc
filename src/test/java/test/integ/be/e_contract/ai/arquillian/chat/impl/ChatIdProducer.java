package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.enterprise.inject.Produces;
import test.integ.be.e_contract.ai.arquillian.chat.ChatId;

public class ChatIdProducer {

    @Produces
    @ChatId
    public String produceChatId() {
        String identifier = ChatScopeContext.getChatIdentifier();
        return identifier;
    }
}
