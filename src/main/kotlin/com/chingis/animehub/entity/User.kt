package com.chingis.animehub.entity

import jakarta.persistence.*

enum class Role {
    ANONYMOUS,
    USER,
    ADMIN
}

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    var role: Role = Role.ANONYMOUS,

    var name: String = "",
    var email: String = "",
    var password: String = ""
)