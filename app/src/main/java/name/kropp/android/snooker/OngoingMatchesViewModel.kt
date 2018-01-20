package name.kropp.android.snooker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import name.kropp.android.snooker.api.Match


class OngoingMatchesViewModel(application: Application) : AndroidViewModel(application) {
    val matches = MutableLiveData<Pair<List<Match>, Map<Long,String>>>()

    suspend fun refresh() {
        val repository = getApplication<SnookerApplication>().repository

        val ongoingMatches = repository.ongoingMatches().await()

        if (ongoingMatches.isNotEmpty()) {
            val rounds = repository.rounds(ongoingMatches.first().eventId).await()
            matches.value = ongoingMatches to rounds
        }
    }
}