package name.kropp.android.snooker

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import name.kropp.android.snooker.api.Event
import java.text.SimpleDateFormat


val YMDDateFormat = SimpleDateFormat("yyyy-MM-dd")


fun formatEventDates(event: Event, context: Context): String =
        DateUtils.formatDateRange(context, event.startDate.time, event.endDate.time, DateUtils.FORMAT_SHOW_DATE)


fun flagResource(country: String) = when(country) {
    "Australia" -> R.drawable.au
    "Belgium" -> R.drawable.be
    "Brazil" -> R.drawable.br
    "China" -> R.drawable.cn
    "Cyprus" -> R.drawable.cy
    "Egypt" -> R.drawable.eg
    "England" -> R.drawable.gb_eng
    "Finland" -> R.drawable.fi
    "Germany" -> R.drawable.de
    "Gibraltar" -> R.drawable.gi
    "Hong Kong" -> R.drawable.hk
    "India" -> R.drawable.`in`
    "Iran" -> R.drawable.ir
    "Ireland" -> R.drawable.ie
    "Iceland" -> R.drawable.`is`
    "Israel" -> R.drawable.il
    "Malaysia" -> R.drawable.my
    "Malta" -> R.drawable.mt
    "Northern Ireland" -> R.drawable.gb_nir
    "Norway" -> R.drawable.no
    "Poland" -> R.drawable.pl
    "Pakistan" -> R.drawable.pk
    "Scotland" -> R.drawable.gb_sct
    "Switzerland" -> R.drawable.ch
    "Thailand" -> R.drawable.th
    "Wales" -> R.drawable.gb_wls
    else -> {
        if (country.isNotEmpty()) {
            Log.i("flag", "No flag for $country")
        }
        R.drawable.unknown
    }
}
