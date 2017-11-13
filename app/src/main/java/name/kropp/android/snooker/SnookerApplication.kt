package name.kropp.android.snooker

import android.app.Application
import name.kropp.android.snooker.api.SnookerOrgRepository
import name.kropp.android.snooker.api.AppDatabase
import android.arch.persistence.room.Room




class SnookerApplication : Application() {
    val repository by lazy { SnookerOrgRepository(this, database) }

    val database by lazy { Room.databaseBuilder(applicationContext, AppDatabase::class.java, "snooker").fallbackToDestructiveMigration().build() }
}