package test.integ.be.e_contract.ai.arquillian;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;

@ChatScoped
public class TransitiveBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransitiveBean.class);

    private StringBuilder message;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("post construct");
        this.message = new StringBuilder();
    }

    public void append(String message) {
        LOGGER.info("append: {}", message);
        this.message.append(message);
    }

    public String getResult() {
        return this.message.toString();
    }
}
