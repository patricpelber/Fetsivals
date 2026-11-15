package com.festivalwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Shared behaviour: every provider simply refreshes all placed widgets. */
abstract class BaseProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, mgr: AppWidgetManager, ids: IntArray) {
        Updater.updateAll(context)
    }

    // When the last widget is removed, updateAll() cancels the daily alarm.
    override fun onDisabled(context: Context) {
        Updater.updateAll(context)
    }
}

class NeonWidget : BaseProvider()
class SunsetWidget : BaseProvider()
class CleanWidget : BaseProvider()
class PinkWidget : BaseProvider()
class GoldWidget : BaseProvider()
class SaffronWidget : BaseProvider()
class OceanWidget : BaseProvider()
class GiantWidget : BaseProvider()

/** Fires on midnight, date/time/timezone/language change, and after app update. */
class TickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Updater.updateAll(context)
    }
}
