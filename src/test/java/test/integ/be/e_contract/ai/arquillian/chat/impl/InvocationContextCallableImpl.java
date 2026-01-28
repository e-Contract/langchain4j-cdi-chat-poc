package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvocationContextCallableImpl implements InvocationContextCallable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationContextCallableImpl.class);

    private InvocationContext invocationContext;

    private String identifier;

    @Override
    public void setInvocationContext(InvocationContext invocationContext, String identifier) {
        this.invocationContext = invocationContext;
        this.identifier = identifier;
    }

    @Override
    public Object call() throws Exception {
        ChatScopeContext.activateChatScope(this.identifier, null, true);
        return this.invocationContext.proceed();
    }
}
