package com.chingis.animehub.service

import com.chingis.animehub.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.*
import org.springframework.stereotype.Service

// UserDetailsService — это слой Spring Security, который не обязан возвращать HTTP-ответы напрямую
// UsernameNotFoundException используется внутри Spring Security
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    // Загружает сведения о пользователе для проверки подлинности Spring Security
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByName(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        // создаем список разрешений
        val authorities = mutableListOf<SimpleGrantedAuthority>()

        // добавляем в список роль юзера
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        // добавляем все разрешения юзера которые определяются его ролью
        user.role.permissions().forEach { permission ->
            authorities.add(SimpleGrantedAuthority("PERMISSION_${permission.name}"))
        }

        return User.builder()
            .username(user.name)
            .password(user.password)
            .authorities(authorities)
            .build()
    }
}