package players.framents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wotapp.R
import fragments.ViewPagerAdapter
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
        val server:String = arguments?.getString("Server") as String
        val bundle = Bundle()
        val fragments:ArrayList<Fragment> = ArrayList()
        val pagerAdapter = ViewPagerAdapter(fragments, activity as AppCompatActivity)
        fragments.add(PlayerOverallStatsFragment())
        // TODO: 22.04.2022 Validation to check is user tracking by user whose already login in app.
        fragments.add(PlayerYesterdayStatsFragment())

        for(f in fragments){
            bundle.putSerializable("PlayerOverallStats",player)
            bundle.putDouble("WN8",wn8)
            bundle.putString("Server",server)
            f.arguments = bundle
        }

        playerStatsViewPager.adapter = pagerAdapter
        playerStatsViewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        return view
    }

}