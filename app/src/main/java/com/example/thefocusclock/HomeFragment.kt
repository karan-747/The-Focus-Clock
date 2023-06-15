package com.example.thefocusclock

import android.app.Application
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.thefocusclock.db.FocusRecord
import com.example.thefocusclock.db.FocusRecordDao
import com.example.thefocusclock.db.FocusRecordDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private  val TAG="HomeFragment"


    private lateinit var btnplus5:FloatingActionButton
    private lateinit var btnminus5:FloatingActionButton
    private lateinit var fabFireMode:FloatingActionButton
    private lateinit var tvMinutes:TextView
    private lateinit var tvSeconds:TextView
    private lateinit var tvHowLong:TextView
    private lateinit var tvLetsFocus:TextView
    private lateinit var tvWork:TextView
    private var timeInMinutes:Long=0


    private lateinit var currDate:String
    private lateinit var calendar: Calendar

    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    private lateinit var btnStartFocus:Button

    private var isBound=false
    private lateinit var isTimerOn:MutableLiveData<Boolean>
    private lateinit var timeRemaining: MutableLiveData<Long>


    private var connection=object :ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            isBound=true
            Log.d(TAG,"Service connected")
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound=false
            Log.d(TAG,"Service disconnected")
        }

    }

    override fun onAttach(context: Context) {
        activity?.actionBar?.setBackgroundDrawable(getDrawable(requireActivity().resources,R.color.white,null))
        activity?.actionBar?.title="Home"
        super.onAttach(context)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fetchTheCurrentDate()


        homeFragmentViewModel= ViewModelProvider(this)[HomeFragmentViewModel::class.java]



        btnplus5=view.findViewById(R.id.fabPlus5)
        btnminus5=view.findViewById(R.id.fabMinus5)
        tvMinutes=view.findViewById(R.id.tvMinutes)
        tvSeconds=view.findViewById(R.id.tvSeconds)
        tvWork=view.findViewById(R.id.tvWork)
        tvHowLong=view.findViewById(R.id.tvHowLong)
        tvLetsFocus=view.findViewById(R.id.tvLetsFocus)
        btnStartFocus=view.findViewById(R.id.btnStartFocus)
        fabFireMode=view.findViewById(R.id.fabFireMode)

        val sandClockAnime = view.findViewById <LottieAnimationView>(R.id.sandClockAnime)



        isTimerOn=TimerService.isTimerOn
        timeRemaining=TimerService.remainingTime


        isTimerOn.observe(viewLifecycleOwner, Observer {
            if(it){
                tvMinutes.text=homeFragmentViewModel.convertIntoMinutes(timeRemaining.value)
                tvSeconds.text=homeFragmentViewModel.giveRemainingSeconds(timeRemaining.value)
                btnminus5.isVisible=false
                btnplus5.isVisible=false
                btnStartFocus.text="End"
                tvHowLong.text="Don't Stare At Me!"
                tvLetsFocus.text="Keep Focusing!"
                tvWork.text="Working..."
                sandClockAnime.playAnimation()


            }else{
                tvMinutes.text="00"
                tvMinutes.text="00"
                btnminus5.isVisible=true
                btnplus5.isVisible=true
                btnStartFocus.text="Start Focus"
                timeInMinutes=0
                tvHowLong.text="How long do you want to focus?"
                tvLetsFocus.text="Let's Focus!"
                tvWork.text="Work      "
                sandClockAnime.pauseAnimation()
            }
        })

        timeRemaining.observe(viewLifecycleOwner, Observer {
            tvMinutes.text=homeFragmentViewModel.convertIntoMinutes(timeRemaining.value)
            tvSeconds.text=homeFragmentViewModel.giveRemainingSeconds(timeRemaining.value)
        })

        btnminus5.setOnClickListener {
           if(timeInMinutes>=5){
               timeInMinutes-=5
               updateTimeTextView()
           }
        }

        btnplus5.setOnClickListener {
            if(timeInMinutes<=115) {
                timeInMinutes += 5
                updateTimeTextView()
            }
        }

        fabFireMode.setOnClickListener{
            if(homeFragmentViewModel.getFireModeStatus()){
                homeFragmentViewModel.updateFireModeStatus()
                toggleDND()
            }else{
                homeFragmentViewModel.updateFireModeStatus()
                toggleDND()
            }
        }

        btnStartFocus.setOnClickListener {

            if(isTimerOn.value == true){
                btnEndTast()
            }else{
                if(isValidTime()) {
                    startTimerService()
                }
            }
        }
    }

    private fun btnEndTast() {
        val intent=Intent(context,TimerService::class.java)
        activity?.unbindService(connection)
        activity?.stopService(intent)

        tvMinutes.text="00"
        tvMinutes.text="00"
        btnminus5.isVisible=true
        btnplus5.isVisible=true

        timeInMinutes=0
        tvHowLong.text="How long do you want to focus?"
        tvLetsFocus.text="Let's Focus!"
        btnStartFocus.isVisible=true
    }

    private fun startTimerService(){

        timeInMinutes=tvMinutes.text.toString().toLong()
        val timeInSeconds=timeInMinutes*60
        val timeString=timeInSeconds.toString()

        val intent=Intent(context,TimerService::class.java)

        intent.putExtra("TIME_STRING",timeString)
        fetchTheCurrentDate()
        intent.putExtra("DATE_STRING",currDate)

        activity?.startService(intent)
        activity?.bindService(intent,connection,BIND_AUTO_CREATE)
    }

    private fun updateTimeTextView() {
        var displaytext:String=timeInMinutes.toString()
        if(timeInMinutes<10){
          displaytext= "0$timeInMinutes"
        }
        tvMinutes.text=displaytext
    }

    private fun isValidTime(): Boolean {
        if(tvMinutes.text.toString()!="00"){
            return true
        }
        return false
    }

    private fun fetchTheCurrentDate() {
        calendar=Calendar.getInstance()
        currDate=DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
    }

    private fun toggleDND() {
        val notificationManager=requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if( !notificationManager.isNotificationPolicyAccessGranted){
            val intent=Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            Toast.makeText(activity?.applicationContext,"Please Enable DND Permission.",Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        if(notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL){
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            Toast.makeText(requireContext(),"FireMode Enabled",Toast.LENGTH_SHORT).show()
            fabFireMode.setImageDrawable(ResourcesCompat.getDrawable(requireActivity().resources,R.drawable.ic_fire_red,null))

        }else{
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            Toast.makeText(requireContext(),"FireMode Disabled",Toast.LENGTH_SHORT).show()
            fabFireMode.setImageDrawable(ResourcesCompat.getDrawable(requireActivity().resources,R.drawable.ic_fire_black,null))
        }
    }
}