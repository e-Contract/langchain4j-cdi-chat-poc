package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.interceptor.InvocationContext;
import jakarta.transaction.UserTransaction;

public class InvocationContextCallableImpl implements InvocationContextCallable {

    private InvocationContext invocationContext;

    private UserTransaction userTransaction;

    @Override
    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    @Override
    public void setInvocationContext(InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
    }

    @Override
    public Object call() throws Exception {
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
