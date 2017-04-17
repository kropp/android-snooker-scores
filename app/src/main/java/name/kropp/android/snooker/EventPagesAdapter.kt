package name.kropp.android.snooker

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class EventPagesAdapter(private val context: Context, fm: FragmentManager, private val liveMatchesListAdapter: MatchesListAdapter, private val allMatchesListAdapter: MatchesListAdapter) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) = when (position) {
        0 -> MatchesListFragment(liveMatchesListAdapter)
        else -> MatchesListFragment(allMatchesListAdapter)
    }

    override fun getPageTitle(position: Int) = context.getString(if (position == 0) R.string.tab_live else R.string.tab_all)
    override fun getCount() = 2
}