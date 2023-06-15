package com.example.thefocusclock.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface FocusRecordDao {

    @Insert
    suspend fun insertNewFocusRecord(focusRecord: FocusRecord)

    @Upsert
    suspend fun upsertFocusRecord(focusRecord: FocusRecord)

    @Update
    suspend fun updateFocusRecord(focusRecord: FocusRecord)

    @Query("SELECT * FROM focus_record_table ORDER BY id DESC")
    fun getAllFocusRecords():LiveData<List<FocusRecord> >

    @Query("SELECT * FROM focus_record_table WHERE date = :givenDate")
    fun getFocusRecordOfGivenDate(givenDate: String):FocusRecord?


}