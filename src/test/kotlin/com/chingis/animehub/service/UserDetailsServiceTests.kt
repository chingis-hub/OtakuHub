package com.chingis.animehub.service

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
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

@ExtendWith(MockitoExtension::class)
class CustomUserDetailsServiceTests {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userDetailsService: CustomUserDetailsService

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
    fun `loadUserByUsername should return UserDetails when user exists`() {
        // Given
        whenever(userRepository.findByName("testuser")).thenReturn(user)

        // When
        val userDetails = userDetailsService.loadUserByUsername("testuser")

        // Then
        assertThat(userDetails.username).isEqualTo("testuser")
        assertThat(userDetails.password).isEqualTo("encodedPassword")
        assertThat(userDetails.authorities).hasSize(user.role.permissions().size + 1) // Role + permissions

        verify(userRepository).findByName("testuser")
    }

    @Test
    fun `loadUserByUsername should throw exception when user not found`() {
        // Given
        whenever(userRepository.findByName("nonexistent")).thenReturn(null)

        // When/Then
        assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("nonexistent")
        }

        verify(userRepository).findByName("nonexistent")
    }
}