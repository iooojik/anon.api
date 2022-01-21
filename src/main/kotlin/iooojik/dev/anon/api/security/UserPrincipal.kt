package iooojik.dev.anon.api.security

import iooojik.dev.anon.api.database.UserModel
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class UserPrincipal : UserDetails {
    private var username: String? = null
    private var password: String? = null
    private var enabled = false
    private var authorities: Collection<GrantedAuthority?>? = null

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authorities!!
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return username!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    fun setUsername(username: String?) {
        this.username = username
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun setAuthorities(authorities: Collection<GrantedAuthority?>?) {
        this.authorities = authorities
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

class UserMapper {
    enum class RoleName {
        USER
    }
    companion object {
        fun userToPrincipal(user: UserModel): UserPrincipal {
            val userPrincipal = UserPrincipal()
            val authorities: List<SimpleGrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_" + RoleName.USER))
            userPrincipal.setUsername(user.userLogin)
            userPrincipal.setPassword(user.password)
            userPrincipal.isEnabled = true
            userPrincipal.setAuthorities(authorities)
            return userPrincipal
        }
    }
}
