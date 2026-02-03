package com.chingis.animehub.service

import com.chingis.animehub.dto.auth_dto.LoginRequestDTO
import com.chingis.animehub.dto.auth_dto.RegisterRequestDTO
import com.chingis.animehub.entity.User
import com.chingis.animehub.entity.Role
import com.chingis.animehub.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

@ExtendWith(MockitoExtension::class)
class AuthServiceTests {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtService: JwtService

    @InjectMocks
    private lateinit var authService: AuthService

    private lateinit var user: User

    @BeforeEach
    fun setup() {
        user = User(
            id = 1L,
            name = "testuser",
            email = "test@example.com",
            password = "encodedPassword",
            role = Role.USER
        )
    }

    @Test
    fun `register should create new user when username and email don't exist`() {
        // Given
        val registerDTO = RegisterRequestDTO(
            username = "newuser",
            email = "new@example.com",
            password = "password123"
        )

        whenever(userRepository.findByName("newuser")).thenReturn(null)
        whenever(userRepository.findByEmail("new@example.com")).thenReturn(null)
        whenever(passwordEncoder.encode("password123")).thenReturn("encodedPassword")
        whenever(userRepository.save(any())).thenReturn(user.copy(
            name = "newuser",
            email = "new@example.com"
        ))

        // When
        val result = authService.register(registerDTO)

        // Then
        assertThat(result.name).isEqualTo("newuser")
        assertThat(result.email).isEqualTo("new@example.com")

        verify(userRepository).findByName("newuser")
        verify(userRepository).findByEmail("new@example.com")
        verify(passwordEncoder).encode("password123")
        verify(userRepository).save(any())
    }

    @Test
    fun `register should throw exception when username already exists`() {
        // Given
        val registerDTO = RegisterRequestDTO(
            username = "testuser",
            email = "new@example.com",
            password = "password123"
        )

        whenever(userRepository.findByName("testuser")).thenReturn(user)

        // When/Then
        assertThrows<ResponseStatusException> {
            authService.register(registerDTO)
        }

        verify(userRepository).findByName("testuser")
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `register should throw exception when email already exists`() {
        // Given
        val registerDTO = RegisterRequestDTO(
            username = "newuser",
            email = "test@example.com",
            password = "password123"
        )

        whenever(userRepository.findByName("newuser")).thenReturn(null)
        whenever(userRepository.findByEmail("test@example.com")).thenReturn(user)

        // When/Then
        assertThrows<ResponseStatusException> {
            authService.register(registerDTO)
        }

        verify(userRepository).findByName("newuser")
        verify(userRepository).findByEmail("test@example.com")
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `login should return JWT token when credentials are valid`() {
        // Given
        val loginDTO = LoginRequestDTO(
            username = "testuser",
            password = "password123"
        )

        whenever(userRepository.findByName("testuser")).thenReturn(user)
        whenever(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true)
        whenever(jwtService.generateToken(user)).thenReturn("jwt.token.here")

        // When
        val result = authService.login(loginDTO)

        // Then
        assertThat(result.token).isEqualTo("jwt.token.here")

        verify(userRepository).findByName("testuser")
        verify(passwordEncoder).matches("password123", "encodedPassword")
        verify(jwtService).generateToken(user)
    }

    @Test
    fun `login should throw exception when user not found`() {
        // Given
        val loginDTO = LoginRequestDTO(
            username = "nonexistent",
            password = "password123"
        )

        whenever(userRepository.findByName("nonexistent")).thenReturn(null)

        // When/Then
        assertThrows<ResponseStatusException> {
            authService.login(loginDTO)
        }

        verify(userRepository).findByName("nonexistent")
        verify(jwtService, never()).generateToken(any())
    }

    @Test
    fun `login should throw exception when password is incorrect`() {
        // Given
        val loginDTO = LoginRequestDTO(
            username = "testuser",
            password = "wrongpassword"
        )

        whenever(userRepository.findByName("testuser")).thenReturn(user)
        whenever(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false)

        // When/Then
        assertThrows<ResponseStatusException> {
            authService.login(loginDTO)
        }

        verify(userRepository).findByName("testuser")
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword")
        verify(jwtService, never()).generateToken(any())
    }
}