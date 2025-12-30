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

    @Column(unique = true, nullable = false)
    var name: String = "",

    @Column(unique = true, nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var password: String = ""
)