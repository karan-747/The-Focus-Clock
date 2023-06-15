package com.example.thefocusclock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thefocusclock.db.FocusRecord
import com.example.thefocusclock.db.FocusRecordDao
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.time.Duration

class HomeFragmentViewModel:ViewModel() {

    private val TAG ="HomeFragmentViewModel"
    private var isFireModeOn:Boolean=false

    fun updateFireModeStatus(){
        isFireModeOn = !(isFireModeOn)
    }

    fun getFireModeStatus(): Boolean {
        return isFireModeOn
    }

    fun convertIntoMinutes(time: Long?):String{
        if(time==null  ){
            return "00"
        }
        if(time == -1L  ){
            return "00"
        }
        var returnString= (time/60).toString()
        if(time/60<10){
            returnString="0${(time/60)}"
        }
        return returnString
    }

    fun giveRemainingSeconds(time: Long?):String{

        if(time==null){
            return "00"
        }
        if(time == -1L  ){
            return "00"
        }
        var returnString= (time%60).toString()
        if(time%60<10){
            returnString="0${(time%60)}"
        }
        return returnString
    }

}