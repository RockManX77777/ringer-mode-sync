package com.rockmanx77777.ringermodesync.presentation

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.rockmanx77777.ringermodesync.presentation.theme.RingerModeSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

private fun setRingerMode(context: Context, mode: Int) {
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
            Toast.makeText(context, "Enable DND access via ADB:\n$adbCommand", Toast.LENGTH_LONG).show()
        }
    } else {
        try {
            audioManager.ringerMode = mode
            val modeName = when (mode) {
                AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
                AudioManager.RINGER_MODE_NORMAL -> "Normal"
                else -> "Unknown"
            }
            Toast.makeText(context, "Set to $modeName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("RingerModeSync", "Error setting ringer mode", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun WearApp() {
    val context = LocalContext.current
    RingerModeSyncTheme {
        AppScaffold {
            ScreenScaffold { contentPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { setRingerMode(context, AudioManager.RINGER_MODE_VIBRATE) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Vibrate")
                        }
                        Button(
                            onClick = { setRingerMode(context, AudioManager.RINGER_MODE_NORMAL) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Normal")
                        }
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
