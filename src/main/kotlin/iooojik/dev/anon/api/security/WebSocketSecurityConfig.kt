package iooojik.dev.anon.api.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer


@Configuration
class WebSocketSecurityConfig : AbstractSecurityWebSocketMessageBrokerConfigurer() {
    override fun configureInbound(messages: MessageSecurityMetadataSourceRegistry) {
        messages
            .simpDestMatchers("/topic/**", "/app/**").authenticated()
            .anyMessage().authenticated()
    }

    override fun sameOriginDisabled(): Boolean {
        return true
    }
}