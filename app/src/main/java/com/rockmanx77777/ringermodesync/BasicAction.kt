package com.rockmanx77777.ringermodesync

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess

class RingerModeChangedActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<RingerModeChangedActionRunner>(config) {
    override val runnerClass: Class<RingerModeChangedActionRunner> get() = RingerModeChangedActionRunner::class.java
    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("Will show a toast with the current ringer mode")
    }
}

class ActivityConfigRingerModeChangedAction : Activity(), TaskerPluginConfigNoInput {
    override val context: Context get() = applicationContext
    private val taskerHelper by lazy { RingerModeChangedActionHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.finishForTasker()
    }
}

class RingerModeChangedActionRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        Log.d("RingerModeSync", "RingerModeChangedActionRunner.run() called")
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ringerMode = audioManager.ringerMode
        val ringerModeText = when (ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "Silent"
            AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
            AudioManager.RINGER_MODE_NORMAL -> "Normal"
            else -> "Unknown"
        }
        Log.d("RingerModeSync", "Detected ringer mode: $ringerModeText")
        Handler(Looper.getMainLooper()).post { Toast.makeText(context, "Ringer Mode: $ringerModeText", Toast.LENGTH_LONG).show() }

        Thread {
            try {
                val nodeList = Tasks.await(Wearable.getNodeClient(context).connectedNodes)
                Log.d("RingerModeSync", "Found ${nodeList.size} connected nodes")
                val payload = java.nio.ByteBuffer.allocate(4).putInt(ringerMode).array()
                for (node in nodeList) {
                    Wearable.getMessageClient(context).sendMessage(node.id, "/ringer_mode", payload)
                    Log.d("RingerModeSync", "Sent message to node: ${node.displayName} (${node.id})")
                }
            } catch (e: Exception) {
                Log.e("RingerModeSync", "Failed to send message to wear", e)
            }
        }.start()

        return TaskerPluginResultSucess()
    }
}