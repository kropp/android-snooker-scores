package name.kropp.android.snooker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.EventComplete


class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val event = MutableLiveData<EventComplete>()
//    private val matches = MutableLiveData<List<Match>>()

    private var eventId: Long? = null

    fun live(): MutableLiveData<EventComplete> {
        refresh()
        return event
    }

    fun setEvent(id: Long) {
        eventId = id
        refresh()
    }

    private val repository get() = getApplication<SnookerApplication>().repository

    private fun refresh() {
        eventId?.let {
            launch(UI) {
                event.value = repository.eventFast(it).await()
                event.value = repository.event(it).await()
            }
        }
    }
}