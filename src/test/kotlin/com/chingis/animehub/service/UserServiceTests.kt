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
import java.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

@ExtendWith(MockitoExtension::class)
class UserServiceTests {

    @Mock
    private lateinit var repository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

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
    fun `create should save user`() {
        // Given
        whenever(repository.save(user)).thenReturn(user)

        // When
        val result = userService.create(user)

        // Then
        assertThat(result).isEqualTo(user)
        verify(repository).save(user)
    }

    @Test
    fun `getAll should return list of users`() {
        // Given
        whenever(repository.findAll()).thenReturn(listOf(user))

        // When
        val result = userService.getAll()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(user)
        verify(repository).findAll()
    }

    @Test
    fun `getById should return user when found`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.of(user))

        // When
        val result = userService.getById(1L)

        // Then
        assertThat(result).isEqualTo(user)
        verify(repository).findById(1L)
    }

    @Test
    fun `getById should return null when user not found`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        // When
        val result = userService.getById(1L)

        // Then
        assertThat(result).isNull()
        verify(repository).findById(1L)
    }

    @Test
    fun `update should update user when found`() {
        // Given
        val updatedUser = user.copy(
            name = "updateduser",
            email = "updated@example.com"
        )

        whenever(repository.findById(1L)).thenReturn(Optional.of(user))
        whenever(repository.save(any())).thenReturn(updatedUser)

        // When
        val result = userService.update(1L, updatedUser)

        // Then
        assertThat(result?.name).isEqualTo("updateduser")
        assertThat(result?.email).isEqualTo("updated@example.com")
        verify(repository).findById(1L)
        verify(repository).save(any())
    }

    @Test
    fun `update should return null when user not found`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        // When
        val result = userService.update(1L, user)

        // Then
        assertThat(result).isNull()
        verify(repository).findById(1L)
        verify(repository, never()).save(any())
    }

    @Test
    fun `delete should call repository deleteById`() {
        // When
        userService.delete(1L)

        // Then
        verify(repository).deleteById(1L)
    }

    @Test
    fun `change should update user role to ADMIN`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.of(user))
        whenever(repository.save(any())).thenReturn(user.apply {
            role = Role.ADMIN
        })

        // When
        userService.change(1L)

        // Then
        verify(repository).findById(1L)
        verify(repository).save(any())
    }

    @Test
    fun `change should throw exception when user not found`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<RuntimeException> {
            userService.change(1L)
        }

        verify(repository).findById(1L)
        verify(repository, never()).save(any())
    }
}