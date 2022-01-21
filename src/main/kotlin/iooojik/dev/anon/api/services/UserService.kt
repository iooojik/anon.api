package iooojik.dev.anon.api.services

import iooojik.dev.anon.api.database.Ban
import iooojik.dev.anon.api.database.FilterModel
import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period
import java.util.*


@Service
class UserService(private val userRepository: UserRepository) {

    val passwordEncoder = BCryptPasswordEncoder()

    fun getByUUID(uuid: String): UserModel? = userRepository.findByUuid(uuid)

    fun save(user: UserModel): UserModel? = userRepository.save(user)

    fun all() : List<UserModel> = userRepository.findAll()

    fun updateUserReputation(userToUpdate: UserModel) {
        val foundUser = userRepository.findByUuid(userToUpdate.uuid)
        if (foundUser != null){
            foundUser.negativeReputation = userToUpdate.negativeReputation
            foundUser.positiveReputation = userToUpdate.positiveReputation
            userRepository.save(foundUser)
        }
    }

    fun register(user: UserModel): UserModel? {
        if (user.password.trim().isNotEmpty()) {
            user.password = passwordEncoder.encode(user.password)
            user.uuid = UUID.randomUUID().toString()
            user.filter = FilterModel(user = user)
            user.banned = Ban(user = user)
            val birthDate = user.birthDate.trim().split('.')
            user.filter.myAge = getAge(birthDate[2].toInt(), birthDate[1].toInt(), birthDate[0].toInt())
            return userRepository.save(user)
        }
        return null
    }

    fun login(user: UserModel): UserModel? {
        if (user.password.trim().isNotEmpty()) {
            val model = userRepository.findByUserLogin(user.userLogin)
            if (model != null) {
                val birthDate = model.birthDate.trim().split('.')
                model.filter.myAge = getAge(birthDate[2].toInt(), birthDate[1].toInt(), birthDate[0].toInt())
                model.lastLogin = Date().toString()
                if (model.banned == null)
                    model.banned = Ban(user = model)
                else if (model.banned?.isBanned == true)
                    return null

                return userRepository.save(model)
            }
        }
        return null
    }

    companion object {
        @JvmStatic
        fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
            return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
            ).years
        }
    }

}