package com.sologo.app.di

import com.google.gson.GsonBuilder
import com.sologo.app.network.*
import com.sologo.app.network.api.*
import com.sologo.app.utils.DateAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit

// Добавьте ErrorInterceptor.kt в папку network
class ErrorInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code) {
            401 -> {
                // Токен истек или неверные учетные данные
                response.close()
                throw retrofit2.HttpException(
                    retrofit2.Response.error<Any>(
                        response.body,
                        response.newBuilder().build()
                    )
                )
            }
        }
        return response
    }
}

val networkModule = module {

    // TokenManager
    single { TokenManager(androidContext()) }

    // AuthInterceptor
    single { AuthInterceptor(get()) }

    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(ErrorInterceptor())  // Добавлен перехватчик ошибок
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Gson с DateAdapter
    single {
        GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateAdapter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    // API интерфейсы
    single { get<Retrofit>().create(UserApi::class.java) }
    single { get<Retrofit>().create(BookingApi::class.java) }
    single { get<Retrofit>().create(HotelApi::class.java) }
    single { get<Retrofit>().create(CityApi::class.java) }
    single { get<Retrofit>().create(RouteApi::class.java) }
    single { get<Retrofit>().create(LostApi::class.java) }
    single { get<Retrofit>().create(SafeZoneApi::class.java) }
    single { get<Retrofit>().create(WeatherApi::class.java) }
}