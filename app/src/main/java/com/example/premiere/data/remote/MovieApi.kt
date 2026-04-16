package com.example.premiere.data.remote

import com.example.premiere.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import android.util.Log

class MovieApi(private val client: HttpClient) {

    companion object {
        const val BASE_URL = "https://rma.finlab.rs"
    }

    suspend fun getMovies(
        pageSize: Int = 30,
        sortBy: String = "imdb_rating",
        sortOrder: String = "desc",
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    ): MoviesResponse {
        /*val response = client.get("$BASE_URL/movies") {
            parameter("page_size", pageSize)
            parameter("sort_by", sortBy)
            parameter("sort_order", sortOrder)
        }

        val raw = response.bodyAsText()
        Log.d("API_RAW", raw)

        return response.body()*/

        return client.get("$BASE_URL/movies") {
            parameter("page_size", pageSize)
            parameter("sort_by", sortBy)
            parameter("sort_order", sortOrder)
            genreId?.let { parameter("genre_id", it) }
            query?.let { parameter("query", it) }
            minYear?.let { parameter("min_year", it) }
            maxYear?.let { parameter("max_year", it) }
            minRating?.let { parameter("min_rating", it) }
        }.body()
    }

    suspend fun getMovieDetails(id: String): Movie {
        val response = client.get("$BASE_URL/movies/$id")
        val raw = response.bodyAsText()
        Log.d("API_RAW", raw)
        return response.body()
    }

    suspend fun getCast(id: String): CastResponse {
        val response = client.get("$BASE_URL/movies/$id/cast") {
            parameter("page_size", 10)
        }
        val raw = response.bodyAsText()
        return response.body()
    }

    suspend fun getImages(id: String): ImagesResponse {
        val response = client.get("$BASE_URL/movies/$id/images") {
            parameter("type", "backdrop")
        }
        val raw = response.bodyAsText()
        return response.body()
    }

    suspend fun getVideos(id: String): List<MovieVideo> {
        return client.get("$BASE_URL/movies/$id/videos").body()
    }

    suspend fun getGenres(): List<Genre> {
        return client.get("$BASE_URL/genres").body()
    }

    suspend fun getConfig(): ImageConfig {
        return client.get("$BASE_URL/config").body()
    }
}