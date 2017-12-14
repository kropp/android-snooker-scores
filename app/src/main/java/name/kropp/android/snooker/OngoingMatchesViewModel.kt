package name.kropp.android.snooker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Match


class OngoingMatchesViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var matches: MutableLiveData<Pair<List<Match>, Map<Long,String>>>

    fun live(): MutableLiveData<Pair<List<Match>, Map<Long,String>>> {
        if (!::matches.isInitialized) {
            matches = MutableLiveData()

            refresh()
        }
        return matches
    }

    private fun refresh() {
        launch(UI) {
            val repository = getApplication<SnookerApplication>().repository
            val ongoingMatches = repository.ongoingMatches(false).await()
            if (!ongoingMatches.isEmpty()) {
                val rounds = repository.rounds(ongoingMatches.first().eventId).await()
                matches.value = ongoingMatches to rounds
            }
        }
    }
}