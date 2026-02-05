package com.chingis.animehub

import com.chingis.animehub.security.Role
import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.admin.email}") private val adminEmail: String,
    @Value("\${app.admin.name}") private val adminName: String,
    @Value("\${app.admin.password}") private val adminPassword: String
) {

    @PostConstruct
    fun initAdmin() {
        if (userRepository.findByEmail(adminEmail) == null) {
            val admin = User(
                email = adminEmail,
                name = adminName,
                password = passwordEncoder.encode(adminPassword).toString(),
                role = Role.ADMIN
            )
            userRepository.save(admin)
        }
    }
}
