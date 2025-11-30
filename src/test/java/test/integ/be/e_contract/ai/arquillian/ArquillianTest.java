package test.integ.be.e_contract.ai.arquillian;

import test.integ.be.e_contract.ai.arquillian.chat.impl.ChatScopeExtension;
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
    private ChatTestBean chatTestBean;

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
                .addPackage(ArquillianTest.class.getPackage())
                .addAsWebInfResource(
                        ArquillianTest.class
                                .getResource("/test-beans.xml"),
                        "beans.xml")
                .addAsResource(
                        ArquillianTest.class
                                .getResource("/test-persistence.xml"),
                        "META-INF/persistence.xml")
                .addAsServiceProvider(Extension.class.getName(), ChatScopeExtension.class.getName());
        return war;
    }

    @Test
    public void testTokenStream() throws Exception {
        this.chatTestBean.chat();
    }
}
