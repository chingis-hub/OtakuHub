package com.chingis.animehub.integration

import com.chingis.animehub.AnimeHubApplication
import com.chingis.animehub.dto.create_dto.AnimeCreateDTO
import com.chingis.animehub.entity.Studio
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.StudioRepository
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

// поднимает весь контекст Spring Boot
@SpringBootTest(classes = [AnimeHubApplication::class])
// вкл MockMvc, который позвлляет кидать HTTP-запросы к контроллерам без запуска реал сервера
@AutoConfigureMockMvc
// вкл поддержку Testcontainers, чтобы исп контейнеры Docker для тестовой бд
@Testcontainers
@ActiveProfiles("test")

class AnimeIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var animeRepository: AnimeRepository

    @Autowired
    private lateinit var studioRepository: StudioRepository

    private val objectMapper = ObjectMapper()

    // Полная изоляция от настроек проекта
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

    private lateinit var studio: Studio

    @BeforeEach
    fun setup() {
        animeRepository.deleteAll()
        studioRepository.deleteAll()

        studio = studioRepository.save(Studio(name = "Test Studio", description = "Test Description"))
    }

    @Test
    @WithMockUser(authorities = ["PERMISSION_ANIME_CREATE", "PERMISSION_ANIME_READ"])
    fun `should create and retrieve anime`() {
        // Given
        val createDTO = AnimeCreateDTO(
            title = "Integration Test Anime",
            description = "Integration Test Description",
            genre = "Action",
            studioId = studio.id
        )

        // When/Then - Create anime
        val createResult = mockMvc.perform(post("/animes")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDTO)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Integration Test Anime"))
            .andExpect(jsonPath("$.description").value("Integration Test Description"))
            .andExpect(jsonPath("$.genre").value("Action"))
            .andExpect(jsonPath("$.studioName").value("Test Studio"))
            .andReturn()

        // Extract the ID from the response
        val responseJson = createResult.response.contentAsString
        val responseMap = objectMapper.readValue(responseJson, Map::class.java)
        val animeId = responseMap["id"] as Int

        // When/Then - Get anime by ID
        mockMvc.perform(get("/animes/$animeId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(animeId))
            .andExpect(jsonPath("$.title").value("Integration Test Anime"))
            .andExpect(jsonPath("$.description").value("Integration Test Description"))

        // When/Then - Get anime by title
        mockMvc.perform(get("/animes/title/Integration Test Anime"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(animeId))
            .andExpect(jsonPath("$.title").value("Integration Test Anime"))
    }

    @Test
    @WithMockUser(authorities = ["PERMISSION_ANIME_CREATE", "PERMISSION_ANIME_DELETE"])
    fun `should delete anime`() {
        // Given - Create an anime
        val createDTO = AnimeCreateDTO(
            title = "Anime To Delete",
            description = "Will be deleted",
            genre = "Horror",
            studioId = studio.id
        )

        val createResult = mockMvc.perform(post("/animes")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDTO)))
            //.andExpect(status().isOk)
            .andReturn()

        val responseJson = createResult.response.contentAsString
        val responseMap = objectMapper.readValue(responseJson, Map::class.java)
        val animeId = responseMap["id"] as Int

        // When - Delete the anime
        mockMvc.perform(delete("/animes/$animeId")
            .with(csrf()))
            .andExpect(status().isOk)

        // Then - Verify it's deleted
        mockMvc.perform(get("/animes/$animeId"))
            .andExpect(status().isNotFound)
    }
}
