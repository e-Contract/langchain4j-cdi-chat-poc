package test.integ.be.e_contract.ai.arquillian.chat;

import jakarta.enterprise.context.NormalScope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@NormalScope
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ChatScoped {

}
