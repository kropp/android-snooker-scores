package name.kropp.android.snooker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Event


class EventsViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var events: MutableLiveData<List<Event>>

    fun live(): MutableLiveData<List<Event>> {
        if (!::events.isInitialized) {
            events = MutableLiveData()

            refresh()
        }
        return events
    }

    private fun refresh() {
        launch(UI) {
            events.value = async { getApplication<SnookerApplication>().repository.events() }.await()
        }
    }
}