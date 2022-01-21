package iooojik.dev.anon.api.repositories

import iooojik.dev.anon.api.database.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserModel, Long> {
    fun findByUserLogin(login : String) : UserModel?
    fun findByUuid(uuid : String) : UserModel?
}