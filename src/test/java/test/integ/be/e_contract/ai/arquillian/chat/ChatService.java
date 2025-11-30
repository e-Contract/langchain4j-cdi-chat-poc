package test.integ.be.e_contract.ai.arquillian.chat;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ai.arquillian.chat.impl.FireEventContext;
import test.integ.be.e_contract.ai.arquillian.chat.impl.FireEventContextImpl;

public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Resource
    private ContextService contextService;

    public String decorate(TokenStream tokenStream) {
        String identifier = UUID.randomUUID().toString();
        FireEventContext fireEventContext = this.contextService.createContextualProxy(new FireEventContextImpl(),
                FireEventContext.class);
        fireEventContext.fire(new StartChatScopeEvent(identifier, this.contextService));
        tokenStream
                .onPartialResponse((String partialResponse) -> {
                    try {
                        this.managedExecutorService.submit(() -> {
                            fireEventContext.fire(new StartChatScopeEvent(identifier));
                            fireEventContext.fire(new PartialResponseEvent(identifier, partialResponse));
                        }).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error("error: " + ex.getMessage(), ex);
                    }
                })
                .onIntermediateResponse((ChatResponse chatResponse) -> {
                    // next is required for the tool invocations
                    // so it has to be fired on this thread
                    fireEventContext.fire(new StartChatScopeEvent(identifier));
                })
                .onCompleteResponse((ChatResponse chatResponse) -> {
                    try {
                        this.managedExecutorService.submit(() -> {
                            fireEventContext.fire(new StartChatScopeEvent(identifier));
                            CompleteResponseEvent event = new CompleteResponseEvent(identifier, chatResponse);
                            fireEventContext.fire(event);
                            if (event.isEndChatScope()) {
                                fireEventContext.fire(new StopChatScopeEvent(identifier));
                            }
                        }).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error("error: " + ex.getMessage(), ex);
                    }
                })
                .onError((Throwable throwable) -> {
                    LOGGER.error("error: " + throwable.getMessage(), throwable);
                    try {
                        this.managedExecutorService.submit(() -> {
                            ChatErrorEvent event = new ChatErrorEvent(identifier, throwable);
                            fireEventContext.fire(event);
                            if (event.isEndChatScope()) {
                                fireEventContext.fire(new StopChatScopeEvent(identifier));
                            }
                        }).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error("error: " + ex.getMessage(), ex);
                    }
                });
        return identifier;
    }
}
