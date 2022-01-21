package iooojik.dev.anon.api.security

import iooojik.dev.anon.api.database.UserModel
import iooojik.dev.anon.api.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class UserDetailsServiceImpl : UserDetailsService {
    @Autowired
    private val userRepository: UserRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: UserModel = userRepository!!.findByUserLogin(username) ?: throw UsernameNotFoundException("User NOT Found")
        return UserMapper.userToPrincipal(user)
    }
}