package com.example.lw_6

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class CounterWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_INCREMENT = "ACTION_INCREMENT"
        private const val ACTION_DECREMENT = "ACTION_DECREMENT"
        private const val PREFS_NAME = "CounterWidgetPrefs"
        private const val COUNTER_KEY = "counter_value"

        private fun getCounterValue(context: Context): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(COUNTER_KEY, 0)
        }

        private fun setCounterValue(context: Context, value: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(COUNTER_KEY, value).apply()
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val incrementIntent = Intent(context, CounterWidgetProvider::class.java).apply {
                action = ACTION_INCREMENT
            }
            val decrementIntent = Intent(context, CounterWidgetProvider::class.java).apply {
                action = ACTION_DECREMENT
            }

            val incrementPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                incrementIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.button_increment, incrementPendingIntent)

            val decrementPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                decrementIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.button_decrement, decrementPendingIntent)

            val counterValue = getCounterValue(context)
            views.setTextViewText(R.id.text_counter, counterValue.toString())

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_INCREMENT || intent.action == ACTION_DECREMENT) {
            val counterValue = getCounterValue(context)
            val newValue = when (intent.action) {
                ACTION_INCREMENT -> counterValue + 1
                ACTION_DECREMENT -> counterValue - 1
                else -> counterValue
            }
            setCounterValue(context, newValue)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CounterWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_layout)
                views.setTextViewText(R.id.text_counter, newValue.toString())
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
