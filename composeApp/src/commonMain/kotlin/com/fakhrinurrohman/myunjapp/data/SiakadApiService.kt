package com.fakhrinurrohman.myunjapp.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SiakadApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun login(nim: String, password: String): SiakadLoginResponse {
        // Replace with actual SIAKAD login endpoint
        // return client.post("https://api.unj.ac.id/login") { ... }.body()
        
        // Mock implementation for development
        return if (nim.isNotBlank() && password.length >= 6) {
            SiakadLoginResponse(true, "mock_token_123", "Login Success", nim, "User Name")
        } else {
            SiakadLoginResponse(false, null, "Invalid Credentials")
        }
    }

    suspend fun fetchSchedule(token: String): SiakadScheduleResponse {
        // Replace with actual schedule endpoint
        // return client.get("https://api.unj.ac.id/schedule") { 
        //    header(HttpHeaders.Authorization, "Bearer $token") 
        // }.body()

        // Mock implementation
        return SiakadScheduleResponse(
            semesterName = "Semester 121 (2024/2025)",
            startDate = "2024-09-01",
            endDate = "2025-01-31",
            courses = listOf(
                SiakadCourse("c1", "IS101", "Sistem Informasi", "Dr. Muhammad", "L.201", 1, "08:00", "10:30"),
                SiakadCourse("c2", "TI202", "Pemrograman Mobile", "Prof. Siti", "L.305", 3, "13:00", "15:30")
            )
        )
    }
}
