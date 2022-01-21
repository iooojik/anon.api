package iooojik.dev.anon.api.security.jwt

import io.jsonwebtoken.*
import iooojik.dev.anon.api.security.UserPrincipal
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@Component
class JwtProvider(
    @Value("\${jwt.password}")
    private val jwtSecret: String
) {

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date.from(
            LocalDateTime.now().plusDays(30)
                .atZone(ZoneId.systemDefault()).toInstant()
        )
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUserUsernameFromJWT(token: String?): String {
        val claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(authToken: String?): Boolean {
        try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
        } catch (ex: MalformedJwtException) {
        } catch (ex: ExpiredJwtException) {
        } catch (ex: UnsupportedJwtException) {
        } catch (ex: IllegalArgumentException) {
        }
        return false
    }
}
