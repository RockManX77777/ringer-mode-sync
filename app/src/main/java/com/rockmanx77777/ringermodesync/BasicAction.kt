package com.rockmanx77777.ringermodesync

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
        blurbBuilder.append("Will show a toast saying 'RingerModeChanged'")
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
        Handler(Looper.getMainLooper()).post { Toast.makeText(context, "RingerModeChanged", Toast.LENGTH_LONG).show() }
        return TaskerPluginResultSucess()
    }
}