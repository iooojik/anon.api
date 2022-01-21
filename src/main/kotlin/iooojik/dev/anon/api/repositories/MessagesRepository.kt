package iooojik.dev.anon.api.repositories

import iooojik.dev.anon.api.database.MessageModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessagesRepository : JpaRepository<MessageModel, Long>