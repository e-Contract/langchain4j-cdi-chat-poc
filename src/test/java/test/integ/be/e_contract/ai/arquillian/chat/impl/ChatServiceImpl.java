package test.integ.be.e_contract.ai.arquillian.chat.impl;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.BeforeToolExecution;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ai.arquillian.chat.ChatErrorEvent;
import test.integ.be.e_contract.ai.arquillian.chat.ChatService;
import test.integ.be.e_contract.ai.arquillian.chat.CompleteResponseEvent;
import test.integ.be.e_contract.ai.arquillian.chat.PartialResponseEvent;

public class ChatServiceImpl implements ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Resource
    private ContextService contextService;

    @Override
    public String decorate(TokenStream tokenStream) {
        String identifier = UUID.randomUUID().toString();
        FireEventContext fireEventContext = this.contextService.createContextualProxy(new FireEventContextImpl(),
                FireEventContext.class);
        ChatScopeContext.activateChatScope(identifier, this.contextService, true);
        tokenStream
                .onPartialResponse((String partialResponse) -> {
                    try {
                        this.managedExecutorService.submit(() -> {
                            ChatScopeContext.activateChatScope(identifier, null, true);
                            fireEventContext.fire(new PartialResponseEvent(identifier, partialResponse));
                        }).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error("error: " + ex.getMessage(), ex);
                    }
                })
                .beforeToolExecution((BeforeToolExecution beforeToolExecution) -> {
                    // next is required for the tool invocations
                    // so it has to be fired on this thread
                    ChatScopeContext.activateChatScope(identifier, null, false);
                })
                .onCompleteResponse((ChatResponse chatResponse) -> {
                    try {
                        this.managedExecutorService.submit(() -> {
                            ChatScopeContext.activateChatScope(identifier, null, true);
                            CompleteResponseEvent event = new CompleteResponseEvent(identifier, chatResponse);
                            fireEventContext.fire(event);
                            if (event.isEndChatScope()) {
                                ChatScopeContext.destroyChatScope(identifier);
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
                            ChatScopeContext.activateChatScope(identifier, null, true);
                            ChatErrorEvent event = new ChatErrorEvent(identifier, throwable);
                            fireEventContext.fire(event);
                            if (event.isEndChatScope()) {
                                ChatScopeContext.destroyChatScope(identifier);
                            }
                        }).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error("error: " + ex.getMessage(), ex);
                    }
                });
        return identifier;
    }
}
