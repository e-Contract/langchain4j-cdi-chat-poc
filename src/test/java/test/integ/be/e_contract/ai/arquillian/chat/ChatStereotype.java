package test.integ.be.e_contract.ai.arquillian.chat;

import jakarta.enterprise.inject.Stereotype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import test.integ.be.e_contract.ai.arquillian.chat.impl.ChatBinding;

@ChatScoped
@ChatBinding
@Stereotype
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatStereotype {

}
