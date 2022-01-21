package iooojik.dev.anon.api.security

import iooojik.dev.anon.api.security.jwt.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SpringSecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    private val userDetailsService: UserDetailsService? = null

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter? {
        return JwtAuthenticationFilter()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    @Throws(java.lang.Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .headers().frameOptions().sameOrigin()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/login/", "/auth/registration/").permitAll()
            .antMatchers("/", "/policy", "/terms", "/app-ads.txt", "/terms_and_policy").permitAll()
            .anyRequest().fullyAuthenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { _: HttpServletRequest?, res: HttpServletResponse, ex: AuthenticationException ->
                res.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "UNAUTHORIZED : " + ex.message
                )
            }
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

}