package com.example.thefocusclock.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FocusRecord::class], version = 1)
abstract  class FocusRecordDatabase :RoomDatabase(){

    abstract val focusRecordDao:FocusRecordDao



    companion object{

        @Volatile
        private var INSTANCE:FocusRecordDatabase? = null
        fun getInstanceOfFocusRecordDatabase(context: Context):FocusRecordDatabase{
            synchronized(this){
                var instance= INSTANCE
                if(instance==null){
                    instance=Room.databaseBuilder(context,FocusRecordDatabase::class.java,"focus_record_database").build()
                    INSTANCE=instance
                }
                return instance
            }

        }
    }


}