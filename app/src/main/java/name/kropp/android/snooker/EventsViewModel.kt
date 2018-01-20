package name.kropp.android.snooker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Event
import kotlin.properties.Delegates


class EventsViewModel(application: Application) : AndroidViewModel(application) {
    val events = MutableLiveData<List<Event>>()

    var year by Delegates.observable(2017L) { _, _, _ -> refresh() }

    fun refresh() {
        launch(UI) {
            events.value = async { getApplication<SnookerApplication>().repository.events(year) }.await()
        }
    }
}