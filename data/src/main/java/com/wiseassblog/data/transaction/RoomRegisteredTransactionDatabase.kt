package com.wiseassblog.data.transaction

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wiseassblog.data.entities.RegisteredRoomTransaction
import com.wiseassblog.data.note.registered.RegisteredTransactionDao

private const val DATABASE_TRANSACTION = "transactions"

/**
 * This database is used as a "cache" for registered users.
 */
@Database(entities = [RegisteredRoomTransaction::class],
        version = 1,
        exportSchema = false)
abstract class RoomRegisteredTransactionDatabase : RoomDatabase() {

    abstract fun roomTransactionDao(): RegisteredTransactionDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: RoomRegisteredTransactionDatabase? = null

        fun getInstance(context: Context): RoomRegisteredTransactionDatabase {
            return instance
                    ?: synchronized(this) {
                instance
                        ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RoomRegisteredTransactionDatabase {
            return Room.databaseBuilder(context, RoomRegisteredTransactionDatabase::class.java, DATABASE_TRANSACTION)
                    .build()
        }
    }
}