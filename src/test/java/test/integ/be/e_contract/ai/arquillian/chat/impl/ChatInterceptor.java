package test.integ.be.e_contract.ai.arquillian.chat.impl;

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
        LOGGER.info("{}.{}", invocationContext.getMethod().getDeclaringClass().getName(),
                invocationContext.getMethod().getName());
        InvocationContextCallable callable = ChatScopeContext.getInvocationContextCallable();
        if (null == callable) {
            callable = new InvocationContextCallableImpl();
        }
        callable.setInvocationContext(invocationContext);
        Transactional txAnnotation = invocationContext.getMethod().getAnnotation(Transactional.class);
        if (null != txAnnotation) {
            callable.setUserTransaction(this.userTransaction);
        } else {
            callable.setUserTransaction(null);
        }
        return this.managedExecutorService.submit(callable).get();
    }
}
