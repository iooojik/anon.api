package iooojik.dev.anon.api.services

import iooojik.dev.anon.api.database.SearchStackModel
import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.repositories.StackRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*
import kotlin.concurrent.thread

@Service
class StackService(private val stackRepository: StackRepository, private val userService: UserService) {

    fun makeUnavailable(userModel: UserModel) {

        userModel.stack?.forEach { st ->
            st.available = false
            stackRepository.save(st)
        }
    }

    fun update(stackModel: SearchStackModel): SearchStackModel? = stackRepository.save(stackModel)

    fun save(stackModel: SearchStackModel, updateAvailable: Boolean = true): SearchStackModel? {
        var stack = stackModel
        if (updateAvailable) {
            stack.participants?.forEach {
                val u = userService.getByUUID(it.uuid)
                if (u != null && !u.stack.isNullOrEmpty()) {
                    makeUnavailable(u)
                }
            }
        }
        stack.uuid = UUID.randomUUID().toString()
        stack = stackRepository.save(stack)
        stack.participants?.forEach {
            if (it.stack.isNullOrEmpty()) {
                it.stack = mutableSetOf(stack)
            } else it.stack!!.add(stack)
            userService.save(it)
        }
        return stackRepository.save(stack)
    }

    fun findChat(me: UserModel, mySearchStackModel: SearchStackModel, simpMessagingTemplate: SimpMessagingTemplate? = null): SearchStackModel? {
        var foundModels = stackRepository.findAllByAvailable(available = true)
        simpMessagingTemplate?.convertAndSend(
            "/topic/${me.uuid}/find",
            SearchProcessModel(if (foundModels == null) 0 else if(foundModels.isEmpty()) 0 else foundModels.size - 1)
        )
        if (!foundModels.isNullOrEmpty()) {
            var returnModel: SearchStackModel? = null
            val myFilters = me.filter
            val myFilterAges = myFilters.interlocutorAges.split('/')
            var myModel: SearchStackModel? = mySearchStackModel
            while (returnModel == null && myModel != null && myModel.available) {
                foundModels = stackRepository.findAllByAvailable(available = true)?.reversed()
                myModel = stackRepository.findByIdOrNull(myModel.id)
                //println("$foundModels $myModel")
                if (myModel == null) break
                foundModels?.forEach {
                    if (!it.participants.isNullOrEmpty()) {
                        val participants = it.participants!!.toList()
                        val foundUser =
                            if (participants.size == 1 && participants[0].uuid != me.uuid) participants[0] else null

                        if (foundUser != null) {
                            val foundModelFilter = foundUser.filter
                            val foundModelAges = foundModelFilter.interlocutorAges.split('/')
                            if (foundModelAges[0].toInt() >= myFilterAges[0].toInt() && foundModelAges[1].toInt() <= myFilterAges[1].toInt()) {
                                if (myFilters.interlocutorSex != "nm") {
                                    if (foundModelFilter.interlocutorSex == myFilters.mySex && foundModelFilter.mySex == myFilters.interlocutorSex
                                        && foundUser.uuid != me.uuid
                                    )
                                        returnModel = it
                                } else if (foundUser.uuid != me.uuid && (foundUser.filter.interlocutorSex == "nm" || foundUser.filter.interlocutorSex == myFilters.mySex)) {
                                    returnModel = it
                                }
                            }
                        }
                    }
                    if (returnModel != null) return@forEach
                }
                //println(returnModel)
                simpMessagingTemplate?.convertAndSend(
                    "/topic/${me.uuid}/find",
                    SearchProcessModel(if (foundModels == null) 0 else if(foundModels.isEmpty()) 0 else foundModels.size - 1)
                )
                Thread.sleep(3000)

            }

            return if (returnModel != null && myModel != null) {
                returnModel = stackRepository.findById(returnModel!!.id).get()
                myModel = stackRepository.findById(myModel.id).get()
                returnModel!!.available = false
                if (myModel.uuid != returnModel!!.uuid){
                    returnModel!!.uuid = UUID.randomUUID().toString()
                    myModel.uuid = returnModel!!.uuid
                }
                myModel.available = false
                stackRepository.save(myModel)
                //me.stack!!.remove(mySearchStackModel)
                //userService.save(me)
                returnModel!!.participants!!.add(me)
                returnModel = stackRepository.save(returnModel!!)

                returnModel
            } else if(myModel != null && myModel.available) {
                Thread.sleep(3000)
                findChat(me, myModel)
                null
            } else null
        } else return null
    }

    fun findByUUID(uuid: String): List<SearchStackModel>? = stackRepository.findAllByUuid(uuid)

    fun getChatsByUser(userModel: UserModel): List<SearchStackModel>? = null
    //stackRepository.findAllByUser(userModel = userModel)

}

data class SearchProcessModel(val inSearchUsers:Int = 0)