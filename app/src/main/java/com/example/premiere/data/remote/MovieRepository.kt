package com.example.premiere.data.remote

import com.example.premiere.data.model.*

class MovieRepository(private val api: MovieApi) {

    suspend fun getMovies(
        pageSize: Int = 30,
        sortBy: String = "imdb_rating",
        sortOrder: String = "desc",
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    ): Result<MoviesResponse> = runCatching {
        api.getMovies(pageSize, sortBy, sortOrder, genreId, query, minYear, maxYear, minRating)
    }

    suspend fun getMovieDetails(id: String): Result<Movie> = runCatching {
        api.getMovieDetails(id)
    }

    suspend fun getCast(id: String): Result<CastResponse> = runCatching {
        api.getCast(id)
    }

    suspend fun getImages(id: String): Result<ImagesResponse> = runCatching {
        api.getImages(id)
    }

    suspend fun getVideos(id: String): Result<VideosResponse> = runCatching {
        api.getVideos(id)
    }

    suspend fun getGenres(): Result<GenresResponse> = runCatching {
        api.getGenres()
    }

    suspend fun getConfig(): Result<ImageConfig> = runCatching {
        api.getConfig()
    }
}