package iooojik.dev.anon.api.rest.sockets

import iooojik.dev.anon.api.database.MessageModel
import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.services.MessagesService
import iooojik.dev.anon.api.services.StackService
import iooojik.dev.anon.api.services.UserService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Controller
class ChatSocketController(
    val simpMessagingTemplate: SimpMessagingTemplate,
    val userService: UserService,
    val stackService: StackService,
    val messagesService: MessagesService,
) {

    @MessageMapping("/end.chat.{uuid}")
    fun endChat(
        @Payload userModel: UserModel,
        @DestinationVariable("uuid") uuid: String
    ) {
        if (userModel.uuid.trim().isNotBlank() && uuid.trim().isNotBlank()) {
            stackService.findByUUID(uuid)?.forEach {
                it.isEnd = true
                stackService.update(it)
            }
            simpMessagingTemplate.convertAndSend(
                "/topic/$uuid/message.topic",
                EndChatModel(endChat = true)
            )
        }
    }

    @MessageMapping("/typing.{uuid}")
    fun typing(
        @Payload typingModel: TypingModel,
        @DestinationVariable("uuid") uuid: String
    ) {
        if (typingModel.typingUser.uuid.trim().isNotBlank() || uuid.trim().isNotBlank()) {
            simpMessagingTemplate.convertAndSend(
                "/topic/$uuid/message.topic",
                typingModel
            )
        }
    }

    @MessageMapping("/seen.{uuid}")
    fun messageSeen(
        @Payload seenModel: SeenModel,
        @DestinationVariable("uuid") uuid: String
    ) {
        if (seenModel.seenBy.uuid.trim().isNotBlank() || uuid.trim().isNotBlank()) {
            simpMessagingTemplate.convertAndSend(
                "/topic/$uuid/message.topic",
                seenModel
            )
        }
    }

    @MessageMapping("/update.reputation.{uuid}")
    fun updateReputation(
        @Payload userToUpdate : UserModel,
        @DestinationVariable("uuid") roomUUID: String
    ) {
        if (userToUpdate.uuid.trim().isNotBlank() && roomUUID.trim().isNotBlank()) {
            userService.updateUserReputation(userToUpdate)
        }
    }

    @MessageMapping("/send.message.{uuid}")
    fun sendMessage(
        @Payload messageModel: MessageModel,
        @DestinationVariable("uuid") uuid: String
    ) {
        if (messageModel.text.trim().isNotBlank() && messageModel.author != null) {
            val author = userService.getByUUID(messageModel.author!!.uuid)
            val searchStackModels = stackService.findByUUID(uuid)
            if (author != null && !searchStackModels.isNullOrEmpty()){
                messageModel.date = Date().toString()
                messageModel.stackModels = searchStackModels
                messageModel.author = author
                messageModel.uuid = UUID.randomUUID().toString()
                messagesService.save(messageModel)
                simpMessagingTemplate.convertAndSend(
                    "/topic/$uuid/message.topic",
                    messageModel
                )

            }
        }
    }
}

data class EndChatModel(
    val endChat: Boolean = false
)

data class TypingModel(
    val typing: Boolean = false,
    val typingUser: UserModel
)

data class SeenModel(
    val seen: Boolean = false,
    val seenBy: UserModel
)