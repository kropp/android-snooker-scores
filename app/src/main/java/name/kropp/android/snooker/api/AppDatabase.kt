package name.kropp.android.snooker.api

import android.arch.persistence.room.*

@Database(entities = arrayOf(PlayerEntity::class, Round::class, EventData::class), version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun eventsDao(): EventsDao
}

@Dao
interface PlayerDao {
    @Query("SELECT * from players where id = :id")
    fun findById(id: Long) : PlayerEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(playerEntity: PlayerEntity)
}

@Dao
interface EventsDao {
    @Query("SELECT * from rounds where eventId = :eventId")
    fun rounds(eventId: Long): List<Round>

    @Query("SELECT * from events where Season = :year ORDER BY StartDate")
    fun events(year: Long): List<EventData>

    @Query("SELECT * from events where ID = :eventId")
    fun event(eventId: Long): EventData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: EventData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: List<EventData>)
}