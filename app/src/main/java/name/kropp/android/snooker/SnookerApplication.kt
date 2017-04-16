package name.kropp.android.snooker

import android.app.Application
import name.kropp.android.snooker.api.SnookerOrgRepository


class SnookerApplication : Application() {
    val repository = SnookerOrgRepository(this)
}