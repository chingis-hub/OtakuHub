package com.chingis.animehub.entity

import jakarta.persistence.*

enum class Role {
    USER {
        override fun permissions() = setOf(
            Permission.ANIME_READ,
            Permission.REVIEW_READ,
            Permission.REVIEW_CREATE
        )
    },
    ADMIN {
        override fun permissions() = Permission.entries.toSet()
    },
    ANONYMOUS {
        override fun permissions() = setOf(
            Permission.ANIME_READ
        )
    };

    abstract fun permissions(): Set<Permission>
}


enum class Permission {
    ANIME_READ,
    ANIME_CREATE,
    ANIME_UPDATE,
    ANIME_DELETE,

    REVIEW_READ,
    REVIEW_CREATE,
    REVIEW_UPDATE,
    REVIEW_DELETE,

    USER_READ,
    USER_MANAGE
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