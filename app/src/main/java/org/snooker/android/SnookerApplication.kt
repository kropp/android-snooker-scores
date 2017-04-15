package org.snooker.android

import android.app.Application
import org.snooker.api.SnookerOrgRepository


class SnookerApplication : Application() {
    val repository = SnookerOrgRepository(this)
}