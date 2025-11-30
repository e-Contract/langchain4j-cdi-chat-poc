package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.CompleteResponseEvent;
import test.integ.be.e_contract.ai.arquillian.chat.PartialResponseEvent;
import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChatScoped
public class ChatObserverBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatObserverBean.class);

    @Inject
    private TransitiveBean transitiveBean;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("post construct");
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info("pre destroy");
    }

    public void observePartialResponseEvent(@Observes PartialResponseEvent event) {
        LOGGER.info("partial response: {}", event.getPartialResponse());
        this.transitiveBean.append(event.getPartialResponse());
    }

    public void observeCompleteResponseEvent(@Observes CompleteResponseEvent event) {
        LOGGER.info("complete response: {}", event.getChatResponse().aiMessage().text());
        LOGGER.info("complete concat message: {}", this.transitiveBean.getResult());
        assertEquals(event.getChatResponse().aiMessage().text(), this.transitiveBean.getResult());
    }

    public void observeAddTestEvent(@Observes AddTestEvent event) {
        LOGGER.info("{} + {} = {}", event.getA(), event.getB(), event.getResult());
    }
}
