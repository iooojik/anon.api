package iooojik.dev.anon.api.listeners

import org.hibernate.annotations.common.util.impl.LoggerFactory
import org.jboss.logging.Logger
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketEventListener {
	companion object {
		val logger: Logger = LoggerFactory.logger(WebSocketEventListener::class.java)
	}
	
	@EventListener
	fun handleWebSocketConnectListener(event: SessionConnectedEvent?) {
		logger.info("Received a new web socket connection ${event.toString()}")
	}
	
	@EventListener
	fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
		val headerAccessor = StompHeaderAccessor.wrap(event.message)
		val username = headerAccessor.sessionAttributes!!["username"] as String?
		if (username != null) {
			logger.info("User Disconnected : $username")
		}
	}
}