package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.impl.ChatScopeExtension;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ai.arquillian.chat.ChatService;

@ExtendWith(ArquillianExtension.class)
public class ArquillianTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArquillianTest.class);

    @Inject
    private TestBean testBean;

    @Inject
    private ChatTestBean chatTestBean;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Resource
    private ContextService contextService;

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addAsLibraries(Maven.configureResolver().workOffline().loadPomFromFile("pom.xml")
                        .resolve("dev.langchain4j:langchain4j-ollama:jar:?")
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.configureResolver().workOffline().loadPomFromFile("pom.xml")
                        .resolve("dev.langchain4j:langchain4j:jar:?")
                        .withTransitivity().asFile())
                .addPackage(ChatService.class.getPackage())
                .addPackage(ChatScopeExtension.class.getPackage())
                .addPackage(TestBean.class.getPackage())
                .addAsWebInfResource(
                        TestBean.class
                                .getResource("/test-beans.xml"),
                        "beans.xml")
                .addAsResource(
                        TestBean.class
                                .getResource("/test-persistence.xml"),
                        "META-INF/persistence.xml")
                .addAsServiceProvider(Extension.class.getName(), ChatScopeExtension.class.getName());
        return war;
    }

    @Test
    public void testCDI() throws Exception {
        LOGGER.info("cdi test");
        List<String> items = Arrays.asList("A", "B", "C");
        TestInterface testInterface = this.contextService.createContextualProxy((String message) -> {
            return this.testBean.hello(message);
        }, TestInterface.class);
        items.parallelStream().forEach(item -> {
            try {
                this.managedExecutorService.submit(() -> {
                    testInterface.hello(item);
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error("error: " + ex.getMessage(), ex);
            }
        });
    }

    @Test
    public void testTokenStream() throws Exception {
        this.chatTestBean.chat();
    }
}
