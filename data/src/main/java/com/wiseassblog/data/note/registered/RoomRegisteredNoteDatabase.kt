package com.wiseassblog.data.note.registered

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wiseassblog.data.datamodels.RegisteredRoomNote

private const val DATABASE_REG = "registered"

/**
 * This database is used as a "cache" for registered users.
 */
@Database(entities = [RegisteredRoomNote::class],
        version = 1,
        exportSchema = false)
abstract class RegisteredNoteDatabase : RoomDatabase(){
    
    abstract fun roomNoteDao(): RegisteredNoteDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile private var instance: RegisteredNoteDatabase? = null

        fun getInstance(context: Context): RegisteredNoteDatabase {
            return instance ?: synchronized(this) {
                instance
                        ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RegisteredNoteDatabase {
            return Room.databaseBuilder(context, RegisteredNoteDatabase::class.java, DATABASE_REG)
                    .build()
        }
    }
}