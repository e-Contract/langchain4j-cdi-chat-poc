package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChatBinding
@Interceptor
public class ChatInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatInterceptor.class);

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Inject
    private UserTransaction userTransaction;

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
        if (null != invocationContext.getMethod()) {
            LOGGER.info("{}.{}", invocationContext.getMethod().getDeclaringClass().getName(),
                    invocationContext.getMethod().getName());
        }
        InvocationContextCallable callable = ChatScopeContext.getInvocationContextCallable();
        if (null == callable) {
            callable = new InvocationContextCallableImpl();
        }
        String identifier = ChatScopeContext.getChatIdentifier();
        callable.setInvocationContext(invocationContext, identifier);
        if (null != invocationContext.getMethod()) {
            Transactional txAnnotation = invocationContext.getMethod().getAnnotation(Transactional.class);
            if (null != txAnnotation) {
                callable.setUserTransaction(this.userTransaction);
            } else {
                callable.setUserTransaction(null);
            }
        } else {
            callable.setUserTransaction(null);
        }
        boolean onManagedThread = ChatScopeContext.isOnManagedThread();
        if (onManagedThread) {
            return callable.call();
        }
        // only schedule when not already on a managed thread
        return this.managedExecutorService.submit(callable).get();
    }
}
