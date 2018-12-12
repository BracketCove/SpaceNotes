package com.wiseassblog.data.note.registered

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wiseassblog.data.entities.RegisteredRoomTransaction

@Dao
interface RegisteredTransactionDao {
    @Query("SELECT * FROM transactions ORDER BY creation_date")
    fun getTransactions(): List<RegisteredRoomTransaction>

    @Query("DELETE FROM transactions")
    fun deleteAll()

    //if update successful, will return number of rows effected, which should be 1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateTransaction(transaction: RegisteredRoomTransaction): Long
}