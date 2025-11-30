package test.integ.be.e_contract.ai.arquillian.chat.impl;

import java.util.concurrent.Callable;
import jakarta.interceptor.InvocationContext;
import jakarta.transaction.UserTransaction;

public interface InvocationContextCallable extends Callable<Object> {

    void setUserTransaction(UserTransaction userTransaction);

    void setInvocationContext(InvocationContext invocationContext);
}
