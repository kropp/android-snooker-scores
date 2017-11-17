package name.kropp.android.snooker.api

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "matches")
data class MatchData(
        @PrimaryKey
        val ID: Long,
        @ForeignKey(entity = EventData::class, parentColumns = arrayOf("ID"), childColumns =  arrayOf("EventID"), onDelete = ForeignKey.CASCADE)
        val EventID: Long,
        val Round: Long,
        val Number: Long,
        val Player1ID: Long,
        val Score1: Long,
        val Walkover1: Boolean,
        val Player2ID: Long,
        val Score2: Long,
        val Walkover2: Boolean,
        val WinnerID: Long,
        val Unfinished: Boolean,
        val OnBreak: Boolean,
        val WorldSnookerID: Long,
        val LiveUrl: String,
        val DetailsUrl: String,
        val PointsDropped: Boolean,
        val ShowCommonNote: Boolean,
        val Estimated: Boolean,
        val Type: Int,
        val TableNo: Int,
        val VideoURL: String,
        val InitDate: Date,
        val ModDate: Date,
//    val StartDate: Date,
//    val EndDate: Date,
        var ScheduledDate: Date,
        val FrameScores: String,
        val Sessions: String,
        val Note: String,
        val ExtendedNote: String
)