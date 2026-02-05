package com.chingis.animehub.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_anime_lists")
class UserAnimeList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    var anime: Anime = Anime(),

    @Enumerated(EnumType.STRING)
    var status: WatchStatus = WatchStatus.PLAN_TO_WATCH,
)