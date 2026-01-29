package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import test.integ.be.e_contract.ai.arquillian.chat.ChatId;

@Dependent
public class ChatIdProducer {

    @Produces
    @ChatId
    public String produceChatId() {
        String identifier = ChatScopeContext.getChatIdentifier();
        return identifier;
    }
}
