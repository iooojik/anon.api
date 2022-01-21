package iooojik.dev.anon.api.rest.sockets

import iooojik.dev.anon.api.ResponseGenerator
import iooojik.dev.anon.api.database.SearchStackModel
import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.services.StackService
import iooojik.dev.anon.api.services.UserService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

//@RestController
@Controller
//@RequestMapping("/anon.chat")
class SearchUserSocketController(
    val simpMessagingTemplate: SimpMessagingTemplate,
    val userService: UserService,
    val stackService: StackService
) : ResponseGenerator {

    @MessageMapping("/find.{uuid}")
    fun findInterlocutor(
        @Payload userModel: UserModel,
        @DestinationVariable("uuid") userUUID: String
    ) {
        if (userModel.uuid == userUUID) {
            var foundModel = userService.getByUUID(userUUID)
            if (foundModel != null) {
                val oldFilters = foundModel.filter
                oldFilters.interlocutorSex = userModel.filter.interlocutorSex
                oldFilters.interlocutorAges = userModel.filter.interlocutorAges
                oldFilters.myAge = userModel.filter.myAge
                oldFilters.mySex = userModel.filter.mySex
                foundModel = userService.save(foundModel)

                if (foundModel != null) {
                    val searchModel = stackService.save(SearchStackModel(participants = mutableSetOf(foundModel)))
                    if (searchModel != null) {
                        getAndSendFoundModel(foundModel, searchModel)
                    }
                }
            }
        }
    }

    @MessageMapping("/cancel.chat.{uuid}")
    fun cancelFindingProcess(
        @Payload @RequestBody userModel: UserModel,
        @DestinationVariable("uuid") userUUID: String
    ) {
        if (userModel.uuid == userUUID) {
            val foundModel = userService.getByUUID(userUUID)
            if (foundModel != null) {
                stackService.makeUnavailable(foundModel)
            }
        }
    }

    private fun getAndSendFoundModel(user: UserModel, searchModel: SearchStackModel?) {
        //me + other
        if (searchModel != null) {
            val fm = stackService.findChat(me = user, mySearchStackModel = searchModel, simpMessagingTemplate = simpMessagingTemplate)
            if (fm != null) {
                if (!fm.participants.isNullOrEmpty())
                //обмен информации о собеседниках
                    simpMessagingTemplate.convertAndSend(
                        "/topic/${fm.participants?.toList()?.get(0)!!.uuid}/find",
                        fm
                    )
                simpMessagingTemplate.convertAndSend(
                    "/topic/${fm.participants?.toList()?.get(1)!!.uuid}/find",
                    fm
                )
                return
            }
        }
    }
}