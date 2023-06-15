package com.example.thefocusclock
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thefocusclock.db.FocusRecord
import com.example.thefocusclock.db.FocusRecordDao
import com.example.thefocusclock.db.FocusRecordDatabase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TimerService: Service() {
    private val TAG="TimerService"
    private val CHANNEL_ID="timer_completion_channel"
    private val CHANNEL_NAME="The Focus Clock Notifications"
    private val CHANNEL_DESC="The Focus Clock App sends notification about completion of timer"
    private  var binder=TimerServiceBinder()
    private lateinit var timer:CountDownTimer

    private var focusRecordDao:FocusRecordDao?=null
    companion object{
        var remainingTime= MutableLiveData<Long>()
        var isTimerOn=MutableLiveData<Boolean>()
        var isTimerCompleted= MutableLiveData<Boolean>(false)


    }
    init{
        remainingTime.value=-1
        isTimerOn.value=false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeString=intent?.getStringExtra("TIME_STRING")

        val time=timeString.toString().toLong()*1000
        val date=intent?.getStringExtra("DATE_STRING")
        isTimerOn.value=true
        remainingTime.value=time/1000
        timer= object : CountDownTimer(time, 1000) {
            override fun onTick(p0: Long) {
               remainingTime.value=p0/1000

            }

            override fun onFinish() {
                remainingTime.value=-1
                isTimerOn.value=false
                isTimerCompleted.value=true
                newFunction(date,(time/60/1000).toInt() )
                stopSelf()
            }
        }
        timer.start()
        return START_NOT_STICKY
    }

    private val channel= Channel<Job>(capacity= Channel.UNLIMITED).apply {
        GlobalScope.launch {
            consumeEach { it.join() }
        }
    }

    private var oldFocusRecord:FocusRecord?=null
    private fun getOldFocusRecord(refFocusRecord:FocusRecord){
        channel.trySend(
            GlobalScope.launch( start = CoroutineStart.LAZY) {
                oldFocusRecord=focusRecordDao?.getFocusRecordOfGivenDate(refFocusRecord.date)
            }
        )
    }

    private fun upsertMyFocusRecord(refFocusRecord: FocusRecord){
        channel.trySend(
            GlobalScope.launch(start = CoroutineStart.LAZY) {
                if(oldFocusRecord==null){
                    focusRecordDao?.insertNewFocusRecord(refFocusRecord)
                }else{
                    var refTime:Int?=refFocusRecord.timeMinutes.toInt()
                    var oldTime=oldFocusRecord?.timeMinutes?.toInt()

                    refTime = oldTime?.let { refTime?.plus(it) }

                    val updatedFocusRecord=oldFocusRecord?.id?.let { oldFocusRecord?.date?.let { it1 ->
                        FocusRecord(it,
                            it1,  "Work",refTime.toString())

                    } }
                    val m= oldFocusRecord?.id?.let { oldFocusRecord?.date?.let { it1 ->
                        FocusRecord(it,
                            it1,
                            "Work",
                            refTime.toString()
                        )
                    } }
                    updatedFocusRecord?.let { insetRecord(it) }

                }
            }
        )
    }
    private fun insetRecord(focusRecord: FocusRecord){
        GlobalScope.launch {
            focusRecordDao?.upsertFocusRecord(focusRecord)
        }
    }


    private fun newFunction(date:String?,time:Int){
        val db=FocusRecordDatabase.getInstanceOfFocusRecordDatabase(applicationContext)
        focusRecordDao=db.focusRecordDao
        val refRecord= date?.let { FocusRecord(0, it,"Work",time.toString()) }!!
        getOldFocusRecord(refRecord)
        upsertMyFocusRecord(refRecord)

        if(NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()){
            showNotification()
        }


    }





    private fun showNotification() {

        channel.trySend(
            GlobalScope.launch( start = CoroutineStart.LAZY) {
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val mChannel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    mChannel.description = CHANNEL_DESC

                    notificationManager.createNotificationChannel(mChannel)
                }
                val drawable=AppCompatResources.getDrawable(applicationContext,
                    R.drawable.applogo
                )
                var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
                    setPriority(NotificationCompat.PRIORITY_HIGH)
                    setSmallIcon(R.drawable.ic_checkmark)
                    setAutoCancel(true)
                    setContentTitle("Hey, Focus Duration Completed ")
                    setContentText("Let's focus more!")
                    setDefaults(Notification.DEFAULT_SOUND)
                    setChannelId(CHANNEL_ID)
                    if (drawable != null) {
                        setLargeIcon(drawable.toBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,
                            Bitmap.Config.ARGB_8888))
                    }
                }
                notificationManager.notify(0, builder.build())
            }
        )



    }

    override fun onDestroy() {
        timer.cancel()
        isTimerOn.value=false
        remainingTime.value=-1
        stopSelf()
    }

    override fun onCreate() {
        isTimerOn.value=false
        remainingTime.value=-1
        super.onCreate()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        isTimerOn.value=false
        remainingTime.value=-1
        timer.cancel()
        stopSelf()

        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class TimerServiceBinder: Binder() {
        fun getServiceInstance(): TimerService {
            return this@TimerService
        }

    }
}