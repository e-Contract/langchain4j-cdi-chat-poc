package test.integ.be.e_contract.ai.arquillian.chat.impl;

import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import java.io.Serializable;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;

public class ChatScopeExtension implements Extension, Serializable {

    public void addScope(@Observes BeforeBeanDiscovery event) {
        event.addScope(ChatScoped.class, true, false);
    }

    public void registerContext(@Observes AfterBeanDiscovery event) {
        event.addContext(new ChatScopeContext());
    }

    public <T> void onProcessAnnotatedType(@Observes @WithAnnotations(ChatScoped.class) ProcessAnnotatedType<T> event) {
        event.configureAnnotatedType().add(new ChatBindingLiteral());
    }
}
