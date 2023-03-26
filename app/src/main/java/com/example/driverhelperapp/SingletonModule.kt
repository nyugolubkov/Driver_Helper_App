package com.example.driverhelperapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.driverhelperapp.services.*
import com.example.driverhelperapp.utils.AuthAuthenticator
import com.example.driverhelperapp.utils.AuthInterceptor
import com.example.driverhelperapp.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager = TokenManager(context)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authAuthenticator)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(
        tokenManager: TokenManager
    ): AuthInterceptor = AuthInterceptor(tokenManager)

    @Singleton
    @Provides
    fun provideAuthAuthenticator(
        tokenManager: TokenManager
    ): AuthAuthenticator = AuthAuthenticator(tokenManager)

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl("http://51.250.64.142:8080")
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideAuthAPIService(retrofit: Retrofit.Builder): AuthApiService =
        retrofit
            .build()
            .create(AuthApiService::class.java)

    @Singleton
    @Provides
    fun provideContactAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): ContactApiService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(ContactApiService::class.java)

    @Singleton
    @Provides
    fun provideDriverAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): DriverApiService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(DriverApiService::class.java)

    @Singleton
    @Provides
    fun provideRideAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): RideApiService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(RideApiService::class.java)

    @Singleton
    @Provides
    fun provideWeatherAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): WeatherApiService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(WeatherApiService::class.java)
}