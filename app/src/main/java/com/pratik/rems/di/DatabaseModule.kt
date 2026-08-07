package com.pratik.rems.di

import android.content.Context
import androidx.room.Room
import com.pratik.rems.data.local.RemiDatabase
import com.pratik.rems.data.local.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // This function tells Hilt how to create the Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RemiDatabase {
        return Room.databaseBuilder(
            context,
            RemiDatabase::class.java,
            "remi_db"
        ).build()
    }

    // This function tells Hilt how to provide the DAO (needed for our logic)
    @Provides
    @Singleton
    fun provideReminderDao(db: RemiDatabase): ReminderDao {
        return db.reminderDao
    }
}