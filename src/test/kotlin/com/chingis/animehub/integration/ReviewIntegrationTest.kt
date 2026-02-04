package com.chingis.animehub.integration

import com.chingis.animehub.AnimeHubApplication
import com.chingis.animehub.dto.create_dto.ReviewCreateDTO
import com.chingis.animehub.entity.Anime
import com.chingis.animehub.entity.Studio
import com.chingis.animehub.entity.User
import com.chingis.animehub.entity.Role
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.ReviewRepository
import com.chingis.animehub.repository.StudioRepository
import com.chingis.animehub.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [AnimeHubApplication::class])
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ReviewIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var animeRepository: AnimeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var studioRepository: StudioRepository

    private val objectMapper = ObjectMapper()

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:14").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    private lateinit var user: User
    private lateinit var anime: Anime
    private lateinit var studio: Studio

    @BeforeEach
    fun setup() {
        // Clean up repositories
        reviewRepository.deleteAll()
        animeRepository.deleteAll()
        userRepository.deleteAll()
        studioRepository.deleteAll()

        // Create a studio
        studio = studioRepository.save(Studio(name = "Test Studio", description = "Test Description"))

        // Create a user
        user = userRepository.save(
            User(
                name = "testuser",
                email = "test@example.com",
                password = "password",
                role = Role.USER
            )
        )

        // Create an anime
        anime = animeRepository.save(
            Anime(
                title = "Test Anime",
                description = "Test Anime Description",
                genre = "Action",
                studio = studio
            )
        )
    }

    @Test
    @WithMockUser(authorities = ["PERMISSION_REVIEW_CREATE"])
    fun `should create review`() {
        // Given
        val createDTO = ReviewCreateDTO(
            animeTitle = anime.title,
            content = "This is a test review",
            score = 8
        )

        // When/Then - Create review
        val createResult = mockMvc.perform(post("/reviews")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDTO))
            .param("userId", user.id.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").value("This is a test review"))
            .andExpect(jsonPath("$.score").value(8))
            .andReturn()

        // Extract the ID from the response
        val responseJson = createResult.response.contentAsString
        val responseMap = objectMapper.readValue(responseJson, Map::class.java)
        val reviewId = responseMap["id"] as Int

        // Verify the review was saved in the database
        assert(reviewRepository.findById(reviewId.toLong()).isPresent)
    }

    @Test
    @WithMockUser(authorities = ["PERMISSION_REVIEW_CREATE"])
    fun `should return 403 when trying to delete review without permission`() {
        // Given - Create a review first
        val createDTO = ReviewCreateDTO(
            animeTitle = anime.title,
            content = "Review to be deleted",
            score = 7
        )

        val createResult = mockMvc.perform(post("/reviews")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDTO))
            .param("userId", user.id.toString()))
            .andExpect(status().isOk)
            .andReturn()

        val responseJson = createResult.response.contentAsString
        val responseMap = objectMapper.readValue(responseJson, Map::class.java)
        val reviewId = responseMap["id"] as Int

        // When/Then - Try to delete without DELETE permission
        mockMvc.perform(delete("/reviews/$reviewId")
            .with(csrf())
            .param("userId", user.id.toString()))
            .andExpect(status().isForbidden)

        // Verify the review still exists
        assert(reviewRepository.findById(reviewId.toLong()).isPresent)
    }

    @Test
    fun `should return 401 when accessing endpoints without authentication`() {
        // Try to create a review without authentication
        val createDTO = ReviewCreateDTO(
            animeTitle = anime.title,
            content = "Unauthorized review",
            score = 6
        )

        mockMvc.perform(post("/reviews")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDTO))
            .param("userId", user.id.toString()))
            .andExpect(status().isUnauthorized)

        // Try to delete a review without authentication
        mockMvc.perform(delete("/reviews/1")
            .with(csrf())
            .param("userId", user.id.toString()))
            .andExpect(status().isUnauthorized)
    }
}