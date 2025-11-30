package test.integ.be.e_contract.ai.arquillian.chat.impl;

import jakarta.enterprise.inject.spi.CDI;

public class FireEventContextImpl implements FireEventContext {

    @Override
    public void fire(Object event) {
        CDI.current().getBeanManager().getEvent().fire(event);
    }
}
