package test.integ.be.e_contract.ai.arquillian;

import dev.langchain4j.service.TokenStream;

public interface TestAIService {

    TokenStream chat(String message);
}
