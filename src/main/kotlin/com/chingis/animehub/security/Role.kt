package com.chingis.animehub.security

enum class Role {
    USER {
        override fun permissions() = setOf(
            Permission.ANIME_READ,
            Permission.REVIEW_READ,
            Permission.REVIEW_CREATE,
            Permission.REVIEW_UPDATE,
            Permission.REVIEW_DELETE,
            Permission.ANIME_LIST_READ,
            Permission.ANIME_LIST_CREATE,
            Permission.ANIME_LIST_UPDATE,
            Permission.ANIME_LIST_DELETE
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
