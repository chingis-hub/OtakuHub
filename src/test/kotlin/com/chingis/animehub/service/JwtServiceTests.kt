package com.chingis.animehub.service

import com.chingis.animehub.entity.User
import com.chingis.animehub.security.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UserDetails
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class JwtServiceTests {

    private lateinit var jwtService: JwtService
    private lateinit var user: User
    private val secret = "testsecrettestsecrettestsecrettestsecrettestsecrettestsecret"
    private val expiration = 3600000L // 1 hour

    @BeforeEach
    fun setup() {
        jwtService = JwtService(secret, expiration)

        user = User(
            id = 1L,
            name = "testuser",
            email = "test@example.com",
            password = "encodedPassword",
            role = Role.USER
        )
    }

    @Test
    fun `generateToken should create valid JWT token`() {
        // When
        val token = jwtService.generateToken(user)

        // Then
        assertThat(token).isNotEmpty()

        // Parse token to verify claims
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        assertThat(claims.subject).isEqualTo("testuser")
        assertThat(claims["roles"]).isEqualTo("USER")
    }

    @Test
    fun `extractUsername should return username from token`() {
        // Given
        val token = jwtService.generateToken(user)

        // When
        val username = jwtService.extractUsername(token)

        // Then
        assertThat(username).isEqualTo("testuser")
    }

    @Test
    fun `isTokenValid should return true for valid token and matching user`() {
        // Given
        val token = jwtService.generateToken(user)
        val userDetails = mock<UserDetails>()
        whenever(userDetails.username).thenReturn("testuser")

        // When
        val isValid = jwtService.isTokenValid(token, userDetails)

        // Then
        assertThat(isValid).isTrue()
    }

    @Test
    fun `isTokenValid should return false for valid token but non-matching user`() {
        // Given
        val token = jwtService.generateToken(user)
        val userDetails = mock<UserDetails>()
        whenever(userDetails.username).thenReturn("otheruser")

        // When
        val isValid = jwtService.isTokenValid(token, userDetails)

        // Then
        assertThat(isValid).isFalse()
    }
}