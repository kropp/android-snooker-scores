package org.snooker.android

import java.text.SimpleDateFormat


val YMDDateFormat = SimpleDateFormat("yyyy-MM-dd")

fun flagResource(country: String) = when(country) {
    "England" -> R.drawable.gb_eng
    "Northern Ireland" -> R.drawable.gb_nir
    "Scotland" -> R.drawable.gb_sct
    "Wales" -> R.drawable.gb_wls
    "Australia" -> R.drawable.au
    "Belgium" -> R.drawable.be
    "China" -> R.drawable.cn
    "Ireland" -> R.drawable.ie
    "Thailand" -> R.drawable.th
    "Hong Kong" -> R.drawable.hk
    else -> R.drawable.unknown
}
