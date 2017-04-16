package name.kropp.android.snooker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SnookerOrgDbHelper(context: Context) : SQLiteOpenHelper(context, FILENAME, null, VERSION) {
    companion object {
        private const val FILENAME = "snooker.org.db"
        private const val VERSION = 2

        const val TABLE_PLAYERS = "players"
        val COLUMN_PLAYERS = arrayOf("id", "json")
    }

    override fun onCreate(db: SQLiteDatabase) = db.execSQL("CREATE TABLE ${TABLE_PLAYERS} (id INTEGER PRIMARY KEY, json TEXT)")

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${TABLE_PLAYERS}")
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = onUpgrade(db, oldVersion, newVersion)
}