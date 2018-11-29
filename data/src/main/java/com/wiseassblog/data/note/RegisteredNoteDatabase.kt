package com.wiseassblog.data.note

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wiseassblog.data.entities.RoomNote

const val DATABASE_REG = "registered"

@Database(entities = [RoomNote::class],
        version = 1,
        exportSchema = false)
abstract class RegisteredNoteDatabase : RoomDatabase(){
    
    abstract fun roomNoteDao(): RoomNoteDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile private var instance: RegisteredNoteDatabase? = null

        fun getInstance(context: Context): RegisteredNoteDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RegisteredNoteDatabase {
            return Room.databaseBuilder(context, RegisteredNoteDatabase::class.java, DATABASE_ANON)
                    .build()
        }
    }
}