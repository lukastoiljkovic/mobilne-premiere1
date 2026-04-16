package com.example.premiere.di

import com.example.premiere.data.remote.MovieApi
import com.example.premiere.data.remote.MovieRepository
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import com.example.premiere.ui.movies.MoviesListViewModel
import com.example.premiere.ui.filter.FilterViewModel
import com.example.premiere.ui.details.MovieDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    single { MovieApi(get()) }
    single { MovieRepository(get()) }
    viewModel { MoviesListViewModel(get()) }
    viewModel { FilterViewModel(get()) }
    viewModel { params -> MovieDetailsViewModel(params.get<String>(), get()) }
}