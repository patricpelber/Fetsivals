package com.festivalwidget

import android.content.Context
import java.util.Calendar

/**
 * Reads res/raw/festivals.txt and finds the next festivals.
 * Pure integer date maths (no java.time) so it works on every Android version
 * and allocates almost nothing.
 */
object Festivals {

    class Item(@JvmField val days: Int, @JvmField val name: String)

    /** Days since 1970-01-01 for a civil date (Howard Hinnant's algorithm). */
    private fun dayNumber(y: Int, m: Int, d: Int): Int {
        val yy = if (m <= 2) y - 1 else y
        val era = (if (yy >= 0) yy else yy - 399) / 400
        val yoe = yy - era * 400
        val mp = if (m > 2) m - 3 else m + 9
        val doy = (153 * mp + 2) / 5 + d - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097 + doe - 719468
    }

    /** "MM-DD" repeats every year, "YYYY-MM-DD" is a one-off date. Returns -1 if invalid or past. */
    private fun resolve(key: String, thisYear: Int, today: Int): Int {
        val p = key.split('-')
        val n: Int = try {
            when (p.size) {
                2 -> {
                    val m = p[0].toInt()
                    val d = p[1].toInt()
                    val a = dayNumber(thisYear, m, d)
                    if (a >= today) a else dayNumber(thisYear + 1, m, d)
                }
                3 -> dayNumber(p[0].toInt(), p[1].toInt(), p[2].toInt())
                else -> -1
            }
        } catch (e: NumberFormatException) {
            -1
        }
        return if (n < today) -1 else n
    }

    /** Next [limit] festivals, soonest first. Ties keep file order (so list major ones first). */
    fun upcoming(ctx: Context, limit: Int): List<Item> {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val today = dayNumber(year, c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
        val out = ArrayList<Item>()
        ctx.resources.openRawResource(R.raw.festivals).bufferedReader().useLines { lines ->
            for (line in lines) {
                val s = line.trim()
                if (s.isEmpty() || s[0] == '#') continue
                val comma = s.indexOf(',')
                if (comma < 5) continue
                val n = resolve(s.substring(0, comma).trim(), year, today)
                if (n < 0) continue
                out.add(Item(n - today, s.substring(comma + 1).trim()))
            }
        }
        out.sortBy { it.days }
        return if (out.size > limit) out.subList(0, limit) else out
    }
}
