package test.integ.be.e_contract.ai.arquillian.chat.impl;

import test.integ.be.e_contract.ai.arquillian.chat.StopChatScopeEvent;
import test.integ.be.e_contract.ai.arquillian.chat.StartChatScopeEvent;
import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;
import jakarta.enterprise.context.spi.AlterableContext;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Bean;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jakarta.enterprise.concurrent.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatScopeContext implements AlterableContext, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatScopeContext.class);

    private static final ThreadLocal<String> CHAT_THREAD_LOCAL = new ThreadLocal<>();

    private static final Map<String, Map<Class, ChatScopeInstance>> INSTANCES = new HashMap<>();

    private static final Map<String, InvocationContextCallable> INVOCATION_CONTEXTS = new HashMap<>();

    @Override
    public Class<? extends Annotation> getScope() {
        return ChatScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        T beanInstance = get(contextual);
        if (null != beanInstance) {
            return beanInstance;
        }
        String identifier = CHAT_THREAD_LOCAL.get();
        if (null == identifier) {
            LOGGER.error("no chat scope active");
            return null;
        }
        Bean bean = (Bean) contextual;
        Class beanClass = bean.getBeanClass();
        LOGGER.info("creating bean instance of type: {} for chat {}", beanClass.getName(), identifier);
        beanInstance = (T) bean.create(creationalContext);
        ChatScopeInstance chatScopeInstance = new ChatScopeInstance();
        chatScopeInstance.bean = bean;
        chatScopeInstance.creationalContext = creationalContext;
        chatScopeInstance.instance = beanInstance;
        Map<Class, ChatScopeInstance> classInstances = INSTANCES.get(identifier);
        if (null == classInstances) {
            classInstances = new HashMap<>();
            INSTANCES.put(identifier, classInstances);
        }
        classInstances.put(beanClass, chatScopeInstance);
        return beanInstance;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        String identifier = CHAT_THREAD_LOCAL.get();
        if (null == identifier) {
            return null;
        }
        Bean bean = (Bean) contextual;
        Class beanClass = bean.getBeanClass();
        Map<Class, ChatScopeInstance> classInstances = INSTANCES.get(identifier);
        if (null == classInstances) {
            return null;
        }
        ChatScopeInstance<T> instance = classInstances.get(beanClass);
        if (null == instance) {
            return null;
        }
        return instance.instance;
    }

    @Override
    public boolean isActive() {
        String identifier = CHAT_THREAD_LOCAL.get();
        return null != identifier;
    }

    public void handleStartChatEvent(@Observes StartChatScopeEvent event) {
        String identifier = event.getIdentifier();
        LOGGER.info("start chat scope: {}", identifier);
        CHAT_THREAD_LOCAL.set(identifier);
        Map<Class, ChatScopeInstance> classInstances = INSTANCES.get(identifier);
        if (null == classInstances) {
            classInstances = new HashMap<>();
            INSTANCES.put(identifier, classInstances);
        }
        ContextService contextService = event.getContextService();
        if (null != contextService) {
            InvocationContextCallable callable = contextService.createContextualProxy(new InvocationContextCallableImpl(), InvocationContextCallable.class);
            INVOCATION_CONTEXTS.put(identifier, callable);
        }
    }

    public void handleStopChatEvent(@Observes StopChatScopeEvent event) {
        String identifier = event.getIdentifier();
        LOGGER.info("stop chat scope: {}", identifier);
        Map<Class, ChatScopeInstance> classInstances = INSTANCES.remove(identifier);
        if (null == classInstances) {
            LOGGER.error("no class instances found for chat: {}", identifier);
            return;
        }
        Collection<ChatScopeInstance> instances = classInstances.values();
        for (ChatScopeInstance instance : instances) {
            Bean bean = instance.bean;
            Class beanClass = bean.getBeanClass();
            LOGGER.info("destroying bean instance of type: {}", beanClass.getName());
            bean.destroy(instance.instance, instance.creationalContext);
        }
        INVOCATION_CONTEXTS.remove(identifier);
    }

    public static InvocationContextCallable getInvocationContextCallable() {
        String identifier = CHAT_THREAD_LOCAL.get();
        if (null == identifier) {
            return new InvocationContextCallableImpl();
        }
        InvocationContextCallable callable = INVOCATION_CONTEXTS.get(identifier);
        if (null == callable) {
            return new InvocationContextCallableImpl();
        }
        return callable;
    }

    @Override
    public void destroy(Contextual<?> contextual) {
        Bean bean = (Bean) contextual;
        LOGGER.debug("destroy");
    }

    private static class ChatScopeInstance<T> {

        private Bean<T> bean;
        private T instance;
        private CreationalContext<T> creationalContext;
    }
}
