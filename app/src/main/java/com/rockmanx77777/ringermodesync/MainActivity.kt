package com.rockmanx77777.ringermodesync

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val statusText = TextView(this).apply {
            setText(R.string.tasker_plugin_status)
            textSize = 20f
        }
        layout.addView(statusText)

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


        val batteryButton = Button(this).apply {
            setText(R.string.disable_battery_optimizations)
            setOnClickListener {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }
        layout.addView(batteryButton)

        val syncButton = Button(this).apply {
            setText(R.string.test_sync_to_wear)
            setOnClickListener {
                Log.d("RingerModeSync", "Sync button clicked")
                RingerModeChangedActionRunner().run(this@MainActivity, TaskerInput(Unit))
            }
        }
        layout.addView(syncButton)

        setContentView(layout)

        checkBatteryOptimization()
    }

    private fun checkBatteryOptimization() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            Log.d("RingerModeSync", "Battery optimizations already ignored")
        } else {
            Toast.makeText(this, R.string.battery_optimization_toast, Toast.LENGTH_LONG).show()
        }
    }
}