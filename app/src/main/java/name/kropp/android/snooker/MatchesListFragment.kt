package name.kropp.android.snooker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MatchesListFragment(private val matchesListAdapter: MatchesListAdapter) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.matches_fragment, container, false)

        val matches_list = view.findViewById(R.id.matches_list) as RecyclerView

        matches_list.adapter = matchesListAdapter
        matches_list.layoutManager = LinearLayoutManager(context)

        return view
    }
}