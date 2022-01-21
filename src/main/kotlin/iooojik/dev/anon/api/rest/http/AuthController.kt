package iooojik.dev.anon.api.rest.http

import iooojik.dev.anon.api.ResponseGenerator
import iooojik.dev.anon.api.constants.ErrorMessages
import iooojik.dev.anon.api.database.SearchStackModel
import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.security.jwt.JwtProvider
import iooojik.dev.anon.api.services.StackService
import iooojik.dev.anon.api.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@Controller
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val stackService: StackService,
) : ResponseGenerator {

    @Autowired
    var authenticationManager: AuthenticationManager? = null

    @Autowired
    var tokenProvider: JwtProvider? = null

    @GetMapping("/")
    private fun getAll(): ResponseEntity<Any> = okResponse(userService.all())

    @PostMapping("/login/*", "/auth/*", "/authorization/*")
    private fun login(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        return if (userModel.password.length >= 6 || userModel.userLogin.trim().isNotBlank()) {
            val authedUser = userService.login(userModel)
            if (authedUser != null && authedUser.id >= 0) {
                authedUser.password = "It's a big secret"
                val jwt = tokenAuth(userModel = userModel)
                okResponse(AuthorizationResponse(user = authedUser, tokenData = Token(token = jwt)))
            } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
        } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
    }

    @PostMapping("/uuid.login/*")
    private fun uuidLogin(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        return if (userModel.uuid.trim().isNotBlank()) {
            val authedUser = userService.getByUUID(userModel.uuid)
            if (authedUser != null && authedUser.id >= 0 && authedUser.banned?.isBanned == false) {
                authedUser.lastLogin = Date().toString()
                val birthDate = authedUser.birthDate.split('.')
                authedUser.filter.myAge = UserService.getAge(
                    birthDate[2].toInt(),
                    birthDate[1].toInt(),
                    birthDate[0].toInt()
                )
                userService.save(authedUser)
                authedUser.password = "It's a big secret"
                okResponse(authedUser)
            } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
        } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)

    }

    @PostMapping("/uuid.chat.login/*")
    private fun uuidChatLogin(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        return if (userModel.uuid.trim().isNotBlank()) {
            var authedUser = userService.getByUUID(userModel.uuid)
            if (authedUser != null && authedUser.id >= 0) {
                authedUser.lastLogin = Date().toString()
                authedUser = userService.save(authedUser)
                authedUser!!.password = "It's a big secret"
                val chats = stackService.getChatsByUser(authedUser)
                return if (!chats.isNullOrEmpty()) {
                    okResponse(
                        AuthorizationResponse(
                            user = ChatLoginModel(
                                user = authedUser,
                                stackModel = stackService.getChatsByUser(authedUser)!!.last()
                            ),
                            tokenData = Token(token = tokenAuth(userModel = userModel))
                        )

                    )
                } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
            } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
        } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
    }

    @PostMapping("/register/*", "/registration/*", "/reg/*")
    private fun registration(@RequestBody userModel: UserModel): ResponseEntity<Any> {
        //не проходит получение токена после регистрации, поэтому сначала регистрируем, а затем получаем токен из метода login
        return if (userModel.password.length >= 6 && userModel.userLogin.trim().isNotBlank()) {
            val authedUser = userService.register(userModel)
            if (authedUser != null && authedUser.id >= 0) {
                authedUser.password = "It's a big secret"
                okResponse(authedUser)
            } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
        } else errorResponse(ErrorMessages.ERROR_AUTHENTICATION)
    }

    private fun tokenAuth(userModel: UserModel): String {
        val authentication: Authentication = authenticationManager!!
            .authenticate(UsernamePasswordAuthenticationToken(userModel.userLogin, userModel.password))
        SecurityContextHolder.getContext().authentication = authentication
        return tokenProvider!!.generateToken(authentication)
    }
}

data class AuthorizationResponse(
    val user: Any,
    val tokenData: Token
)

data class Token(
    val token: String,
    val tokenHeader: String = "Bearer"
)

data class ChatLoginModel(
    val user: UserModel,
    val stackModel: SearchStackModel
)