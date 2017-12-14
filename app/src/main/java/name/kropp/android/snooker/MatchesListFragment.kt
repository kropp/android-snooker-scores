package name.kropp.android.snooker

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MatchesListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.matches_fragment, container, false)

        val matches_list = view.findViewById<RecyclerView>(R.id.matches_list)

        val matchesListAdapter = MatchesListAdapter(activity as MainActivity)

        val eventViewModel = ViewModelProviders.of(activity).get(EventViewModel::class.java)
        eventViewModel.live().observe({lifecycle}) { event ->
            matchesListAdapter.setEvent(matchesByRound(event!!.matches, false), event.rounds)
            matchesListAdapter.notifyDataSetChanged()
        }

        matches_list.adapter = matchesListAdapter
        matches_list.layoutManager = LinearLayoutManager(context)

        return view
    }
}