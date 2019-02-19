package com.wiseassblog.data.note.anonymous

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wiseassblog.data.datamodels.AnonymousRoomNote

private const val DATABASE_ANON = "anonymous"

@Database(entities = [AnonymousRoomNote::class],
        version = 1,
        exportSchema = false)
abstract class AnonymousNoteDatabase : RoomDatabase(){
    
    abstract fun roomNoteDao(): AnonymousNoteDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AnonymousNoteDatabase? = null

        fun getInstance(context: Context): AnonymousNoteDatabase {
            return instance ?: synchronized(this) {
                instance
                        ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AnonymousNoteDatabase {
            return Room.databaseBuilder(context, AnonymousNoteDatabase::class.java, DATABASE_ANON)
                    .build()
        }
    }
}