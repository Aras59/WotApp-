package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wotapp.R
import com.google.android.material.tabs.TabLayoutMediator
import players.playerstats.Player

class PlayerStatsFragment : Fragment() {

    private lateinit var playerStatsViewPager:ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_stats, container, false)
        playerStatsViewPager = view.findViewById(R.id.playerStatsViewPager)

        val player: Player = arguments?.getSerializable("PlayerOverallStats") as Player
        val wn8:Double = arguments?.getDouble("WN8") as Double
        val bundle = Bundle()
        val fragments:ArrayList<Fragment> = arrayListOf(PlayerOverallStatsFragment(),PlayerOverallStatsFragment())
        for(f in fragments){

            bundle.putSerializable("PlayerOverallStats",player)
            bundle.putDouble("WN8",wn8)
            f.arguments = bundle
        }
        val pagerAdapter = MyViewPagerAdapter(fragments, activity as AppCompatActivity)

        playerStatsViewPager.adapter = pagerAdapter





        return view
    }

}