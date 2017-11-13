package name.kropp.android.snooker.api

import android.arch.persistence.room.*

@Database(entities = arrayOf(PlayerEntity::class, Round::class), version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun eventsDao(): EventsDao
}

@Dao
interface PlayerDao {
    @Query("SELECT * from players where id = :id")
    fun findById(id: Long) : PlayerEntity

    @Insert
    fun add(playerEntity: PlayerEntity)
}

@Dao
interface EventsDao {
    @Query("SELECT * from rounds where eventId = :eventId")
    fun rounds(eventId: Long): List<Round>
}