package iooojik.dev.anon.api.services

import iooojik.dev.anon.api.database.MessageModel
import iooojik.dev.anon.api.repositories.MessagesRepository
import org.springframework.stereotype.Service

@Service
class MessagesService(val messagesRepository: MessagesRepository) {
    fun save(messageModel: MessageModel) : MessageModel? = messagesRepository.save(messageModel)
}