package name.kropp.android.snooker.api

import android.arch.persistence.room.*

@Database(entities = arrayOf(PlayerEntity::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
}

@Dao
interface PlayerDao {
    @Query("SELECT * from players where id = :id")
    fun findById(id: Long) : PlayerEntity

    @Insert
    fun add(playerEntity: PlayerEntity)
}
