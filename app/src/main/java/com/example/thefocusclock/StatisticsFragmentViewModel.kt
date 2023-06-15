package com.example.thefocusclock

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.thefocusclock.db.FocusRecord
import com.example.thefocusclock.db.FocusRecordDao

class StatisticsFragmentViewModel(private val dao: FocusRecordDao):ViewModel() {

    private var focusRecords=dao.getAllFocusRecords()
    fun getAllFocusRecords():LiveData<List<FocusRecord>>{
        return focusRecords
    }

}