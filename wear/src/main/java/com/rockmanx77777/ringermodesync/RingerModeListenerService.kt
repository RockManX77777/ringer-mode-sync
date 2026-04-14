package com.rockmanx77777.ringermodesync

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class RingerModeListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/ringer_mode") {
            val ringerMode = String(messageEvent.data)
            Log.d("RingerModeSync", "Received ringer mode: $ringerMode")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "Ringer Mode: $ringerMode", Toast.LENGTH_SHORT).show()
            }
        }
    }
}