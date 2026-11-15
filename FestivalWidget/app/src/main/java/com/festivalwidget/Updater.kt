package com.festivalwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.Calendar

/**
 * Builds and pushes the widget views.
 *
 * Time, weekday, month and day are drawn by TextClock inside the widget itself, so the
 * system keeps them correct (including timezone and 12/24h changes) with ZERO work from
 * this app. We only push the festival name and the countdown, once a day.
 */
object Updater {

    const val ACTION_TICK = "com.festivalwidget.TICK"

    private class Style(val cls: Class<*>, val layout: Int, val big: Boolean)

    private val styles = arrayOf(
        Style(NeonWidget::class.java, R.layout.w_neon, false),
        Style(SunsetWidget::class.java, R.layout.w_sunset, false),
        Style(CleanWidget::class.java, R.layout.w_clean, false),
        Style(PinkWidget::class.java, R.layout.w_pink, false),
        Style(GoldWidget::class.java, R.layout.w_gold, false),
        Style(SaffronWidget::class.java, R.layout.w_saffron, false),
        Style(OceanWidget::class.java, R.layout.w_ocean, false),
        Style(GiantWidget::class.java, R.layout.w_giant, true)
    )

    fun updateAll(ctx: Context) {
        val mgr = AppWidgetManager.getInstance(ctx)
        var anyWidget = false
        var computed = false
        var name = ""
        var days = -1
        var open: PendingIntent? = null

        for (s in styles) {
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, s.cls))
            if (ids.isEmpty()) continue
            anyWidget = true

            if (!computed) {
                computed = true
                val next = Festivals.upcoming(ctx, 1).firstOrNull()
                if (next != null) {
                    name = next.name
                    days = next.days
                }
                open = PendingIntent.getActivity(
                    ctx, 0, Intent(ctx, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            val rv = RemoteViews(ctx.packageName, s.layout)
            rv.setTextViewText(R.id.tvFest, if (days < 0) "-" else name)
            if (s.big) {
                rv.setTextViewText(R.id.tvNum, if (days < 0) "-" else days.toString())
                rv.setTextViewText(
                    R.id.tvLeft,
                    ctx.getString(
                        when {
                            days == 0 -> R.string.big_today
                            days == 1 -> R.string.big_day
                            else -> R.string.big_days
                        }
                    )
                )
            } else {
                rv.setTextViewText(
                    R.id.tvLeft,
                    when {
                        days < 0 -> ""
                        days == 0 -> ctx.getString(R.string.today)
                        days == 1 -> ctx.getString(R.string.tomorrow)
                        else -> ctx.getString(R.string.in_days, days)
                    }
                )
            }
            rv.setOnClickPendingIntent(R.id.root, open)
            mgr.updateAppWidget(ids, rv)
        }

        if (anyWidget) scheduleMidnight(ctx) else cancelMidnight(ctx)
    }

    private fun tickIntent(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 1, Intent(ctx, TickReceiver::class.java).setAction(ACTION_TICK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    /** One inexact, non-waking alarm per day. No permission needed. */
    private fun scheduleMidnight(ctx: Context) {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 5)
        c.set(Calendar.MILLISECOND, 0)
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC, c.timeInMillis, tickIntent(ctx))
    }

    private fun cancelMidnight(ctx: Context) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(tickIntent(ctx))
    }
}
