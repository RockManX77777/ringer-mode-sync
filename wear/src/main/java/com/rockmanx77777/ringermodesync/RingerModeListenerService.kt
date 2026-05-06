package com.rockmanx77777.ringermodesync

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import java.nio.ByteBuffer

class RingerModeListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/ringer_mode") {
            val ringerMode = ByteBuffer.wrap(messageEvent.data).int
            Log.d("RingerModeSync", "Received ringer mode: $ringerMode")
            Handler(Looper.getMainLooper()).post {
                setRingerMode(applicationContext, ringerMode)
            }
        }
    }
}

fun setRingerMode(context: Context, mode: Int) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (!notificationManager.isNotificationPolicyAccessGranted) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

        try {
            context.startActivity(intent)
            Toast.makeText(context, "Please grant DND access", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            val adbCommand = "adb shell cmd notification allow_dnd ${context.packageName}"
            Log.e("RingerModeSync", "Failed to open DND settings. Run: $adbCommand", e)
            Toast.makeText(context, "Cannot open DND settings. Enable via ADB:\n$adbCommand", Toast.LENGTH_LONG).show()
        }
    } else {
        try {
            audioManager.ringerMode = mode
            val modeName = when (mode) {
                AudioManager.RINGER_MODE_SILENT -> "Silent"
                AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
                AudioManager.RINGER_MODE_NORMAL -> "Normal"
                else -> "Unknown"
            }
            Toast.makeText(context, "Set to $modeName", Toast.LENGTH_SHORT).show()

            val vibrator = context.getSystemService(Vibrator::class.java)
            if (vibrator != null && vibrator.hasVibrator()) {
                Log.d("RingerModeSync", "Vibrating...")
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (e: Exception) {
            Log.e("RingerModeSync", "Error setting ringer mode", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}