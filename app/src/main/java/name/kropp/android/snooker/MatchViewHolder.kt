package name.kropp.android.snooker

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import name.kropp.android.snooker.api.Match
import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMMd"), Locale.getDefault())

class MatchViewHolder(val activity: Activity, val view: View): RecyclerView.ViewHolder(view) {
    val flag1 = view.findViewById<AppCompatImageView>(R.id.flag1)
    val text1 = view.findViewById<TextView>(android.R.id.text1)
    val aux1 = view.findViewById<TextView>(R.id.aux1)

    val flag2 = view.findViewById<AppCompatImageView>(R.id.flag2)
    val text2 = view.findViewById<TextView>(android.R.id.text2)
    val aux2 = view.findViewById<TextView>(R.id.aux2)

    var match: Match? = null

    init {
//        view.setOnClickListener {
//            match?.let { activity.onMatchClicked(it) }
//        }
    }

    fun bind(match: Match?) {
        this.match = match

        if (match == null) return

        view.setBackgroundResource(if (match.isActive) R.color.colorPrimary else R.color.colorPrimaryDark)

        if (match.isStarted || match.isFinished) {
            aux1.text = match.score1.toString()
            aux2.text = match.score2.toString()
        } else {
            aux1.text = match.date?.let { dateFormat.format(it) } ?: ""
            aux2.text = ""
        }

        val normalOrFade1 = ContextCompat.getColor(view.context, if (match.isPlayer2Winner) R.color.colorPrimaryLight else R.color.textColor)
        text1.setTextColor(normalOrFade1)
        aux1.setTextColor(if (match.isActive) ContextCompat.getColor(view.context, R.color.colorAccent) else normalOrFade1)
        val normalOrFade2 = ContextCompat.getColor(view.context, if (match.isPlayer1Winner) R.color.colorPrimaryLight else R.color.textColor)
        text2.setTextColor(normalOrFade2)
        aux2.setTextColor(if (match.isActive) ContextCompat.getColor(view.context, R.color.colorAccent) else normalOrFade2)

        flag1.setImageResource(flagResource(match.player1.nationality))
        text1.text = match.player1.name

        flag2.setImageResource(flagResource(match.player2.nationality))
        text2.text = match.player2.name


//        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_top))
    }
}

class RoundViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val text = view.findViewById<TextView>(android.R.id.text1)

    fun bind(round: String?) {
        text.text = round
//        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.slide_in_top))
    }
}