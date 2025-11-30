package test.integ.be.e_contract.ai.arquillian.chat;

import dev.langchain4j.service.TokenStream;

public interface ChatService {

    String decorate(TokenStream tokenStream);
}
