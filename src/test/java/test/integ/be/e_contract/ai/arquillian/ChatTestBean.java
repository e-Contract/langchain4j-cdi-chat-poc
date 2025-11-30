package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.ChatService;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatTestBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatTestBean.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ChatService chatService;

    @Inject
    private AddTestTool addTestTool;

    @Inject
    private StopBean stopBean;

    public void chat() throws Exception {
        StreamingChatModel model = OllamaStreamingChatModel.builder()
                .baseUrl("https://ai.e-contract.be/ollama")
                .modelName("gpt-oss:120b")
                .logRequests(true)
                .logResponses(true)
                .customHeaders(Map.of("Authorization", "Bearer put-token-here"))
                .temperature(0.0)
                .build();

        TestAIService aiService = AiServices.builder(TestAIService.class)
                .streamingChatModel(model)
                .tools(this.addTestTool)
                .build();
        TokenStream tokenStream = aiService.chat("Add 1024 and 1024.");
        String identifier = this.chatService.decorate(tokenStream);
        LOGGER.info("chat identifier: {}", identifier);

        CountDownLatch stopLatch = new CountDownLatch(1);
        CountDownLatch noErrorLatch = new CountDownLatch(1);

        this.stopBean.init(stopLatch, noErrorLatch);

        tokenStream.start();

        stopLatch.await();

        assertEquals(1, noErrorLatch.getCount(), "error occurred");

        Query query = this.entityManager.createQuery("SELECT te FROM TestEntity AS te");
        assertEquals(1, query.getResultList().size());
        for (TestEntity testEntity : (List<TestEntity>) query.getResultList()) {
            LOGGER.info("entity: {}", testEntity.getName());
        }
    }
}
