package com.rockmanx77777.ringermodesync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("RingerModeSync", "Notification permission granted: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RingerModeSync", "MainActivity.onCreate() called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("RingerModeSync", "Requesting notification permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("RingerModeSync", "Notification permission already granted")
            }
        }

        val button = Button(this).apply {
            setText(R.string.sync_ringer_mode_to_wear)
            setOnClickListener {
                Log.d("RingerModeSync", "Sync button clicked")
                RingerModeChangedActionRunner().run(this@MainActivity, TaskerInput(Unit))
            }
        }
        setContentView(button)
    }
}