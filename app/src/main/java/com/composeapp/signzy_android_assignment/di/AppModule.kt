package com.composeapp.signzy_android_assignment.di

import android.content.Context
import androidx.room.Room
import com.composeapp.signzy_android_assignment.data.database.AppDatabase
import com.composeapp.signzy_android_assignment.data.database.UserVerificationDao
import com.composeapp.signzy_android_assignment.data.remote.HttpClientObject
import com.composeapp.signzy_android_assignment.data.repo.UserRepositoryImpl
import com.composeapp.signzy_android_assignment.domain.repo.UserRepository
import com.composeapp.signzy_android_assignment.domain.usecases.GetUsersUseCase
import com.composeapp.signzy_android_assignment.domain.usecases.GetUsersVerificationUseCase
import com.composeapp.signzy_android_assignment.domain.usecases.InsertUserVerificationUseCase
import com.composeapp.signzy_android_assignment.domain.usecases.UpdateUserVerificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClientObject.client
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "signzy_assignment.db"
        )
            .build()
    }

    // provide user verification
    @Provides
    fun provideUserVerificationDao(appDatabase: AppDatabase) = appDatabase.getUserVerificationDao()


    // users repo
    @Provides
    fun provideUserRepo(httpClient: HttpClient): UserRepository {
        return UserRepositoryImpl(httpClient)
    }



    @Provides
    fun provideGetUserRepoUseCase(userRepository: UserRepository): GetUsersUseCase {
        return GetUsersUseCase(userRepository)
    }


    @Provides
    fun provideUserVerificationUseCase(userVerificationDao: UserVerificationDao) =
        GetUsersVerificationUseCase(userVerificationDao)


    @Provides
    fun provideUpdateUserVerificationUseCase(userVerificationDao: UserVerificationDao) =
        UpdateUserVerificationUseCase(userVerificationDao)


    @Provides
    fun provideInsertUserVerificationUseCase(userVerificationDao: UserVerificationDao) =
        InsertUserVerificationUseCase(userVerificationDao)
}
