package iooojik.dev.anon.api.configurations

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
	override fun registerStompEndpoints(registry: StompEndpointRegistry) {
		registry.addEndpoint("/ws").withSockJS()
	}
	
	override fun configureMessageBroker(registry: MessageBrokerRegistry) {
		//registry.enableSimpleBroker("/secured/history");
		//registry.setApplicationDestinationPrefixes("/spring-security-mvc-socket");

		registry.setApplicationDestinationPrefixes("/app")
		val taskScheduler = ThreadPoolTaskScheduler()
		taskScheduler.afterPropertiesSet()
		registry.enableSimpleBroker("/topic").setHeartbeatValue(longArrayOf(10000L, 10000L))
			.setTaskScheduler(taskScheduler)
	}
}