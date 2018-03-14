package name.kropp.android.snooker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Player

typealias PlayerRanking = Pair<Player,Long>

class RankingViewModel(application: Application) : AndroidViewModel(application) {
    private val rankings = MutableLiveData<List<PlayerRanking>>()

    fun live(): LiveData<List<PlayerRanking>> {
        refresh()
        return rankings
    }

    private val repository get() = getApplication<SnookerApplication>().repository

    fun refresh() {
        launch(UI) {
            rankings.value = repository.rankings().await()
        }
    }
}