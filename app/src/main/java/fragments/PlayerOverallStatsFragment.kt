package fragments

import android.annotation.SuppressLint
import players.playerstats.Player
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wotapp.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class PlayerOverallStatsFragment : Fragment() {

    lateinit var wtrRatingView: TextView
    lateinit var victoryView: TextView
    lateinit var victoryBattlesView: TextView
    lateinit var avgDmgView: TextView
    lateinit var avgAsistView: TextView
    lateinit var expView: TextView
    lateinit var battlesView: TextView
    lateinit var survivedBattlesView: TextView
    lateinit var survivedPercentView: TextView
    lateinit var avgDestroyedTankView: TextView
    lateinit var hitRatioView: TextView
    lateinit var piercingsShotsView: TextView
    lateinit var piercingsShotsPerBattleView: TextView
    lateinit var wn8View: TextView
    lateinit var createdView: TextView
    lateinit var lastGameView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_overall_stats, container, false)
        wtrRatingView = view.findViewById(R.id.wtrRating)
        victoryView = view.findViewById(R.id.victoryView)
        victoryBattlesView = view.findViewById(R.id.victoryBattlesView)
        avgDmgView = view.findViewById(R.id.avgDmgView)
        avgAsistView = view.findViewById(R.id.avgAsistView)
        expView = view.findViewById(R.id.expView)
        battlesView = view.findViewById(R.id.battlesView)
        survivedBattlesView = view.findViewById(R.id.survivedBattlesView)
        survivedPercentView = view.findViewById(R.id.survivedPercentView)
        avgDestroyedTankView = view.findViewById(R.id.avgDestroyedTank)
        hitRatioView = view.findViewById(R.id.hitRatioView)
        piercingsShotsView = view.findViewById(R.id.piercingsShotsView)
        piercingsShotsPerBattleView = view.findViewById(R.id.piercingsShotsPerBattleView)
        wn8View = view.findViewById(R.id.wn8View)
        createdView = view.findViewById(R.id.createdView)
        lastGameView = view.findViewById(R.id.lastGameView)

        val player: Player = arguments?.getSerializable("PlayerOverallStats") as Player

        wtrRatingView.text = " "+player.global_rating.toString()
        battlesView.text = " "+player.statistics.all.battles.toString()
        survivedBattlesView.text = " "+player.statistics.all.survived_battles.toString()+" "
        hitRatioView.text = " "+player.statistics.all.hits_percents.toString() + "%"
        piercingsShotsView.text = " "+player.statistics.all.piercings.toString()
        victoryBattlesView.text = " "+player.statistics.all.wins.toString()+" "
        var wn8 = arguments?.getDouble("WN8")
//        if (wn8 != null) {
////            if(wn8<400.0){
////                wn8View.setTextColor(R.color.red)
////            }
////            if(wn8<859.0 && wn8>399.0){
////                wn8View.setTextColor(R.color.oragne)
////            }
////            if(wn8<1419.0 && wn8>858.0){
////                wn8View.setTextColor(R.color.yellow)
////            }
////            if(wn8<2104.0 && wn8>1418.0){
////                wn8View.setTextColor(R.color.green)
////            }
////            if(wn8<2769.0 && wn8>2103.0){
////                wn8View.setTextColor(R.color.blue)
////            }
////            if(wn8>2768.0){
////                wn8View.setTextColor(R.color.purple_500)
////            }
//        }

        wn8View.text = wn8?.let { " "+BigDecimal(it).setScale(2,RoundingMode.HALF_EVEN).toString() }
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
        createdView.text = " Created at: "+simpleDateFormat.format(player.created_at.toLong()*1000L).toString()
        lastGameView.text = " Last Battle at: "+simpleDateFormat.format(player.last_battle_time.toLong()*1000L).toString()

        if(player.statistics.all.battles!=0){
            victoryView.text = " "+BigDecimal(((player.statistics.all.wins.toDouble()/player.statistics.all.battles.toDouble())*100.0)).setScale(2,RoundingMode.HALF_EVEN).toString()+"%"
            survivedPercentView.text = " "+BigDecimal(((player.statistics.all.survived_battles.toDouble()/player.statistics.all.battles.toDouble())*100.0)).setScale(2,RoundingMode.HALF_EVEN).toString()+"%"
            avgDmgView.text = " "+(player.statistics.all.damage_dealt/player.statistics.all.battles).toString()
            avgAsistView.text = " "+(player.statistics.all.avg_damage_assisted).toString()
            expView.text = " "+(player.statistics.all.xp/player.statistics.all.battles).toString()+" XP"
            avgDestroyedTankView.text = " "+BigDecimal((player.statistics.all.frags/player.statistics.all.battles).toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
            piercingsShotsPerBattleView.text = " "+(player.statistics.all.piercings/player.statistics.all.battles).toString()+"shots"
        }else{
            avgDmgView.text = " "+"0"
            avgAsistView.text =" "+ player.statistics.all.avg_damage_assisted.toString()
            expView.text = " "+"0 XP"
            victoryView.text = " "+"0%"
            survivedPercentView.text = " "+"0%"
            avgDestroyedTankView.text = " "+"0"
            piercingsShotsPerBattleView.text = " "+"0"
        }


        return view;
    }


    companion object {
        fun newInstance(param1: String, param2: String) =
            PlayerOverallStatsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}