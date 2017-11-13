package name.kropp.android.snooker.api

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "players")
data class PlayerEntity(
        @PrimaryKey var id: Long,
        override var name: String,
        override var nationality: String
) : Player

@Entity(tableName = "rounds")
data class Round(
        @PrimaryKey var id: Long,
        var eventId: Long,
        var description: String
)