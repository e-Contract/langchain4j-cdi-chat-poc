package test.integ.be.e_contract.ai.arquillian.chat.impl;

import java.util.concurrent.Callable;
import jakarta.interceptor.InvocationContext;

public interface InvocationContextCallable extends Callable<Object> {

    void setInvocationContext(InvocationContext invocationContext, String identifier);
}
