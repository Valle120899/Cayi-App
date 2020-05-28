package com.drma.mycayiapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.quickblox.chat.QBChatService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import org.jivesoftware.smackx.ping.PingFailedListener
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


private const val PING_ALARM_ACTION = "com.quickblox.chat.ping.ACTION"

object ChatPingAlarmManager {
    private val TAG = ChatPingAlarmManager::class.java.simpleName
    private val PING_INTERVAL = TimeUnit.SECONDS.toMillis(60)
    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager
    private lateinit var context: WeakReference<Context>
    private var pingFailedListener: PingFailedListener? = null

    private val alarmBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.v(TAG, "Ping Alarm broadcast received")
            Log.d(TAG, "Calling pingServer for connection ")
            val pingManager = QBChatService.getInstance().pingManager
            pingManager?.pingServer(object : QBEntityCallback<Void> {
                override fun onSuccess(result: Void?, params: Bundle) {

                }

                override fun onError(responseException: QBResponseException) {
                    pingFailedListener?.pingFailed()
                }
            })
        }
    }

    /**
     *
     * @param context
     */
    fun onCreate(context: Context) {
        this.context = WeakReference(context)
        context.registerReceiver(alarmBroadcastReceiver, IntentFilter(PING_ALARM_ACTION))
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(PING_ALARM_ACTION), 0)
        val trigger = SystemClock.elapsedRealtime() + PING_INTERVAL
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, PING_INTERVAL, pendingIntent)
    }


    fun onDestroy() {
        try {
            context.get()?.unregisterReceiver(alarmBroadcastReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        alarmManager.cancel(pendingIntent)
        pingFailedListener = null
    }

    fun addPingListener(listener: PingFailedListener) {
        pingFailedListener = listener
    }
}