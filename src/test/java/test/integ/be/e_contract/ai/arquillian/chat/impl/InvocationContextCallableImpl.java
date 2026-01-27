package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.interceptor.InvocationContext;
import jakarta.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvocationContextCallableImpl implements InvocationContextCallable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationContextCallableImpl.class);

    private InvocationContext invocationContext;

    private UserTransaction userTransaction;

    private String identifier;

    @Override
    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    @Override
    public void setInvocationContext(InvocationContext invocationContext, String identifier) {
        this.invocationContext = invocationContext;
        this.identifier = identifier;
    }

    @Override
    public Object call() throws Exception {
        ChatScopeContext.activateChatScope(this.identifier, null, true);
        if (null == this.userTransaction) {
            return this.invocationContext.proceed();
        }
        this.userTransaction.begin();
        try {
            Object result = this.invocationContext.proceed();
            this.userTransaction.commit();
            return result;
        } catch (Exception t) {
            LOGGER.error("error: " + t.getMessage(), t);
            this.userTransaction.rollback();
            throw t;
        }
    }
}
