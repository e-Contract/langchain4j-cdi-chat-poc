package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.CompleteResponseEvent;
import test.integ.be.e_contract.ai.arquillian.chat.PartialResponseEvent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBean implements TestInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBean.class);

    @Override
    public String hello(String message) {
        LOGGER.info("hello");
        CDI.current().getBeanManager();
        return message;
    }

    public void observePartialResponseEvent(@Observes PartialResponseEvent event) {
        LOGGER.info("partial response: {}", event.getPartialResponse());
    }

    public void observeCompleteResponseEvent(@Observes CompleteResponseEvent event) {
        LOGGER.info("complete response: {}", event.getChatResponse().aiMessage().text());
    }

    public void observeAddTestEvent(@Observes AddTestEvent event) {
        LOGGER.info("added {} + {} = {}", event.getA(), event.getB(), event.getResult());
    }
}
