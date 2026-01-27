package test.integ.be.e_contract.ai.arquillian.chat.impl;

import test.integ.be.e_contract.ai.arquillian.chat.ChatScoped;
import jakarta.enterprise.context.spi.AlterableContext;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
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

    private static final ThreadLocal<ChatInfo> CHAT_THREAD_LOCAL = new ThreadLocal<>();

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
        ChatInfo chatInfo = CHAT_THREAD_LOCAL.get();
        if (null == chatInfo) {
            LOGGER.error("no chat scope active");
            return null;
        }
        String identifier = chatInfo.identifier;
        if (null == identifier) {
            LOGGER.error("no chat scope active");
            return null;
        }
        Bean bean = (Bean) contextual;
        Class beanClass = bean.getBeanClass();
        LOGGER.info("creating bean instance of type: {} for chat {}", beanClass.getName(), identifier);
        beanInstance = (T) bean.create(creationalContext);
        ChatScopeInstance chatScopeInstance = new ChatScopeInstance(bean, beanInstance, creationalContext);
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
        ChatInfo chatInfo = CHAT_THREAD_LOCAL.get();
        if (null == chatInfo) {
            return null;
        }
        String identifier = chatInfo.identifier;
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
        ChatInfo chatInfo = CHAT_THREAD_LOCAL.get();
        return null != chatInfo;
    }

    public static void activateChatScope(String identifier, ContextService contextService, boolean onManagedThread) {
        LOGGER.info("activate chat scope: {} (on managed thread: {})", identifier, onManagedThread);
        ChatInfo chatInfo = new ChatInfo(identifier, onManagedThread);
        CHAT_THREAD_LOCAL.set(chatInfo);
        Map<Class, ChatScopeInstance> classInstances = INSTANCES.get(identifier);
        if (null == classInstances) {
            classInstances = new HashMap<>();
            INSTANCES.put(identifier, classInstances);
        }
        if (null != contextService) {
            InvocationContextCallable callable = contextService.createContextualProxy(new InvocationContextCallableImpl(), InvocationContextCallable.class);
            INVOCATION_CONTEXTS.put(identifier, callable);
        }
    }

    public static void destroyChatScope(String identifier) {
        LOGGER.info("destroy chat scope: {}", identifier);
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
        ChatInfo chatInfo = CHAT_THREAD_LOCAL.get();
        if (null == chatInfo) {
            return new InvocationContextCallableImpl();
        }
        String identifier = chatInfo.identifier;
        if (null == identifier) {
            return new InvocationContextCallableImpl();
        }
        InvocationContextCallable callable = INVOCATION_CONTEXTS.get(identifier);
        if (null == callable) {
            return new InvocationContextCallableImpl();
        }
        return callable;
    }

    public static String getChatIdentifier() {
        ChatInfo chatInfo = CHAT_THREAD_LOCAL.get();
        if (null == chatInfo) {
            return null;
        }
        String identifier = chatInfo.identifier;
        return identifier;
    }

    public static boolean isOnManagedThread() {
        ChatInfo chatInfo = CHAT_THREAD_LOCAL.get();
        if (null == chatInfo) {
            return false;
        }
        return chatInfo.onManagedThread;
    }

    @Override
    public void destroy(Contextual<?> contextual) {
        Bean bean = (Bean) contextual;
        LOGGER.debug("destroy");
    }

    private static record ChatInfo(String identifier, boolean onManagedThread) {

    }

    private static record ChatScopeInstance<T>(Bean<T> bean, T instance, CreationalContext<T> creationalContext) {

    }
}
