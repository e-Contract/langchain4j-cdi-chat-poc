package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;
import test.integ.be.e_contract.ai.arquillian.chat.ChatErrorEvent;
import java.util.concurrent.CountDownLatch;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChatScoped
public class StopBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopBean.class);

    private CountDownLatch stopLatch;

    private CountDownLatch noErrorLatch;

    public void init(CountDownLatch stopLatch, CountDownLatch noErrorLatch) {
        this.stopLatch = stopLatch;
        this.noErrorLatch = noErrorLatch;
    }

    public void observeError(@Observes ChatErrorEvent event) {
        LOGGER.error("error: " + event.getThrowable().getMessage(), event.getThrowable());
        this.noErrorLatch.countDown();
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info("pre destroy");
        this.stopLatch.countDown();
    }
}
