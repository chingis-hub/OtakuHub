package com.chingis.animehub.service

import com.chingis.animehub.entity.User

import java.util.*
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service


@Service
class JwtService(

    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val expiration: Long
) {
    // превращает строку secret → байты
    // проверяет, что длина подходит для HS256
    // создаёт SecretKey, который безопасно использовать в криптографии
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

// Header формируется почти всегда автоматически
// Payload — это то, что ты явно добавляешь через setSubject, claim, setIssuedAt, setExpiration
// Signature создаётся автоматически библиотекой

        return Jwts.builder()
            .setSubject(user.name)
            .claim("roles", user.role.name)
            .claim("permissions", user.role.permissions().map { it.name })
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            // проверяет подпись токена и проверяет, что токен не истёк
            // если всё ок → возвращает Claims
            .parseClaimsJws(token)
            // достаёт sub = username
            .body
            .subject

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username
    }

}