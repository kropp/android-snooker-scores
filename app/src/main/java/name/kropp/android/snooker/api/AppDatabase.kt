package name.kropp.android.snooker.api

import android.arch.persistence.room.*

@Database(version = 5, exportSchema = false, entities = arrayOf(
        PlayerEntity::class,
        Round::class,
        EventData::class,
        MatchData::class
))
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun eventsDao(): EventsDao
    abstract fun matchesDao(): MatchesDao
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

    @Query("SELECT * from rounds where ID = :id")
    fun round(id: Long): Round

    @Query("SELECT * from events where Season = :year ORDER BY StartDate")
    fun events(year: Long): List<EventData>

    @Query("SELECT * from events where ID = :eventId")
    fun event(eventId: Long): EventData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rounds: Round)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: EventData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: List<EventData>)
}

@Dao
interface MatchesDao {
    @Query("SELECT * from matches where ID = :matchId")
    fun match(matchId: Long): MatchData

    @Query("SELECT * from matches where EventID = :eventId")
    fun matchesOfEvent(eventId: Long): List<MatchData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(match: MatchData)
}