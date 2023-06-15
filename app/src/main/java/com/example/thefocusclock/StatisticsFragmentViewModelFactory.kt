package com.example.thefocusclock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thefocusclock.db.FocusRecordDao

class StatisticsFragmentViewModelFactory(private val focusRecordDao: FocusRecordDao):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StatisticsFragmentViewModel::class.java)){
            return StatisticsFragmentViewModel(focusRecordDao) as T
        }
        throw IllegalArgumentException("View model classs not found")
    }
}