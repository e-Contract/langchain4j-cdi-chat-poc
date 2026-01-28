package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChatBinding
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200 - 1)
public class ChatInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatInterceptor.class);

    @Resource
    private ManagedExecutorService managedExecutorService;

    @AroundInvoke
    public Object observeMethod(InvocationContext invocationContext)
            throws Exception {
        return invoke(invocationContext);
    }

    @PostConstruct
    public void observePostConstruct(InvocationContext invocationContext) {
        try {
            invoke(invocationContext);
        } catch (Exception ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void observePreDestroy(InvocationContext invocationContext) {
        try {
            invoke(invocationContext);
        } catch (Exception ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
        }
    }

    private Object invoke(InvocationContext invocationContext) throws Exception {
        boolean onManagedThread = ChatScopeContext.isOnManagedThread();
        if (onManagedThread) {
            return invocationContext.proceed();
        }
        InvocationContextCallable callable = ChatScopeContext.getInvocationContextCallable();
        String identifier = ChatScopeContext.getChatIdentifier();
        callable.setInvocationContext(invocationContext, identifier);
        return this.managedExecutorService.submit(callable).get();
    }
}
