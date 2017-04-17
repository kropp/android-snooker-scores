package name.kropp.android.snooker.api

import java.util.*

class Match(private val data: MatchData, val player1: Player, val player2: Player, private val repository: SnookerOrgRepository) {
    val id get() = data.ID
    val number get() = data.Number
    val score1 get() = data.Score1
    val score2 get() = data.Score2
    val round get() = data.Round
    val date: Date
        get() = data.ScheduledDate
    val isStarted: Boolean
        get() = data.Unfinished
    val isFinished: Boolean
        get() = !data.Unfinished && data.WinnerID != 0L
    val isActive: Boolean
        get() = data.Unfinished && !data.OnBreak
    val isPlayer1Winner: Boolean
        get() = data.WinnerID == data.Player1ID
    val isPlayer2Winner: Boolean
        get() = data.WinnerID == data.Player2ID
    val eventId: Long get() = data.EventID
    val worldSnookerId: Long get() = data.WorldSnookerID
}