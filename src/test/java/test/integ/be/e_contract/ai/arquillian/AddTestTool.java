package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ai.arquillian.chat.ChatId;

@ChatScoped
public class AddTestTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddTestTool.class);

    private boolean called;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Event<AddTestEvent> addTestEvent;

    @Inject
    @ChatId
    private String chatIdentifier;

    @Tool("Adds two numbers.")
    @Transactional
    public int addNumbers(int a, int b) {
        LOGGER.info("chat identifier: {}", this.chatIdentifier);
        LOGGER.info("adding number {} and {}", a, b);
        this.called = true;
        int result = a + b;
        TestEntity entity = new TestEntity("result: " + result);
        this.entityManager.persist(entity);
        // check whether we can get a CDI instance
        CDI.current();
        this.addTestEvent.fire(new AddTestEvent(a, b, result));
        return result;
    }

    public boolean isCalled() {
        return this.called;
    }
}
