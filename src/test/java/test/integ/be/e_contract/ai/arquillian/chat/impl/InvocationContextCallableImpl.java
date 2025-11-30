package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.interceptor.InvocationContext;
import jakarta.transaction.UserTransaction;
import test.integ.be.e_contract.ai.arquillian.chat.StartChatScopeEvent;

public class InvocationContextCallableImpl implements InvocationContextCallable {

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
        CDI.current().getBeanManager().getEvent().fire(new StartChatScopeEvent(identifier));
        if (null == this.userTransaction) {
            return this.invocationContext.proceed();
        }
        this.userTransaction.begin();
        try {
            return this.invocationContext.proceed();
        } finally {
            this.userTransaction.commit();
        }
    }
}
