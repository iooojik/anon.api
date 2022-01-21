package iooojik.dev.anon.api.rest.http

import iooojik.dev.anon.api.ResponseGenerator
import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.services.StackService
import iooojik.dev.anon.api.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Controller
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val stackService: StackService
) : ResponseGenerator {

    @PostMapping("/uuid/*")
    fun getByUUID(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        if (userModel.uuid.trim().isNotBlank()) {
            val foundUser = userService.getByUUID(userModel.uuid)
            if (foundUser != null)
                return okResponse(foundUser)
        }
        return errorResponse()
    }

    @PostMapping("/update/filter/*")
    fun updateFilters(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        val foundUser = userService.getByUUID(userModel.uuid)
        if (foundUser != null) {
            val filter = foundUser.filter
            val newFilter = userModel.filter
            filter.myAge = newFilter.myAge
            if (filter.mySex.trim().isNotEmpty() && (filter.mySex == "male" || filter.mySex == "female"))
                filter.mySex = userModel.filter.mySex
            return okResponse(userService.save(foundUser)!!)
        }
        return errorResponse()
    }

    @PostMapping("/chat/history/*")
    fun getChatHistory(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        val foundUser = userService.getByUUID(userModel.uuid)
        return if(foundUser != null){
            var chats = stackService.getChatsByUser(foundUser)
            if (chats.isNullOrEmpty()) chats = listOf()
            okResponse(chats)
        } else errorResponse()
    }



}