package com.aura.assistant.di

import android.content.Context
import androidx.room.Room
import com.aura.assistant.data.local.AuraDatabase
import com.aura.assistant.data.local.ConversationDao
import com.aura.assistant.data.remote.AuraBackendService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuraDatabase(@ApplicationContext context: Context): AuraDatabase {
        return Room.databaseBuilder(
            context,
            AuraDatabase::class.java,
            "aura_secure_assistant.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideConversationDao(database: AuraDatabase): ConversationDao = database.conversationDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendService(client: OkHttpClient): AuraBackendService {
        val json = Json { ignoreUnknownKeys = true; isLenient = true }
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuraBackendService::class.java)
    }
}
