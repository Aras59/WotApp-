package com.example.wotapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wotapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.example.wotapp.models.playerstats.Player
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PlayerYesterdayStatsFragment : Fragment() {
    private lateinit var battlesView: TextView
    private lateinit var victoryBattlesView: TextView
    private lateinit var victoryPercentView: TextView
    private lateinit var survivedBattlesView: TextView
    private lateinit var survivedPercentView: TextView
    private lateinit var avgDmgView: TextView
    private lateinit var avgAsistView: TextView
    private lateinit var avgDestroyedTankView: TextView
    private lateinit var hitRatioView: TextView
    private lateinit var piercingsShotsView: TextView
    private lateinit var piercingsShotsPerBattleView: TextView
    private lateinit var expView: TextView
    private lateinit var accessInfo: TextView
    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_player_yesterday_stats, container, false)
        battlesView = view.findViewById(R.id.battlesView)
        victoryPercentView = view.findViewById(R.id.victoryView)
        victoryBattlesView = view.findViewById(R.id.victoryBattlesView)
        survivedBattlesView = view.findViewById(R.id.survivedBattlesView)
        survivedPercentView = view.findViewById(R.id.survivedPercentView)
        avgDmgView = view.findViewById(R.id.avgDmgView)
        avgAsistView = view.findViewById(R.id.avgAsistView)
        avgDestroyedTankView = view.findViewById(R.id.avgDestroyedTank)
        hitRatioView = view.findViewById(R.id.hitRatioView)
        piercingsShotsView = view.findViewById(R.id.piercingsShotsView)
        piercingsShotsPerBattleView = view.findViewById(R.id.piercingsShotsPerBattleView)
        expView = view.findViewById(R.id.expView)
        accessInfo = view.findViewById(R.id.accessInfo)

        val player: Player = arguments?.getSerializable("PlayerOverallStats") as Player
        val server:String = arguments?.getString("Server") as String

        val current = LocalDateTime.now().minusDays(1)

        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
        val formatted = current.format(formatter)

        val fireStore = FirebaseFirestore.getInstance()


        fireStore.collection("followingusers")
            .document(auth.currentUser?.displayName.toString())
            .collection(server).document(player.account_id.toString())
            .get().addOnCompleteListener {
                task ->
                if (task.isSuccessful && task.result.data != null) {
                    accessInfo.visibility = View.INVISIBLE
                }else {
                    accessInfo.visibility = View.VISIBLE
                }
            }

        fireStore.collection("playersstats")
                .document(formatted).collection(server)
                .document(player.account_id.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val statsFromDate = task.result
                    if(statsFromDate.data != null) {
                        settingStatsOnView(player, statsFromDate)
                    }
                }else {
                    Toast.makeText(activity, "Failed to get data.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun settingStatsOnView(player: Player, stats: DocumentSnapshot) {
        val battles = (player.statistics.all.battles - stats.get("battles")
            .toString().toInt()).toString()
        if( battles.toDouble() != 0.0) {
            battlesView.text = " $battles "
            val winBattles = (player.statistics.all.wins - stats.get("wins").toString().toInt()).toString()
            victoryBattlesView.text = " $winBattles "
            victoryPercentView.text = " "+ BigDecimal(((winBattles.toDouble()/battles.toDouble())*100.0)).setScale(2,
                RoundingMode.HALF_EVEN).toString()+"%"
            val survivedBattles = (player.statistics.all.survived_battles - stats.get("survived_battles").toString().toInt()).toString()
            survivedBattlesView.text = " $survivedBattles "
            survivedPercentView.text = " "+ BigDecimal(((survivedBattles.toDouble()/battles.toDouble())*100.0)).setScale(2,
                RoundingMode.HALF_EVEN).toString()+"%"
            val avgDMG = ((player.statistics.all.damage_dealt - stats.get("damage_dealt")
                .toString().toInt()).toDouble()/battles.toDouble()).toString()
            avgDmgView.text = " "+BigDecimal(avgDMG.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()+" "
            val avgAsist = ((player.statistics.all.avg_damage_assisted * player.statistics.all.battles - stats.get("avg_damage_assisted")
                .toString().toDouble() * stats.get("battles").toString().toInt())/battles.toDouble()).toString()

            avgAsistView.text = " "+BigDecimal(avgAsist.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()+" "
            val avgTanksDestroyed = ((player.statistics.all.frags - stats.get("frags")
                .toString().toInt()).toDouble()/battles.toDouble()).toString()
            avgDestroyedTankView.text = " "+BigDecimal(avgTanksDestroyed.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()+" "
            hitRatioView.text = " "+stats.get("hits_percents").toString()+"%"
            val piercingsShots = (player.statistics.all.piercings - stats.get("piercings")
                .toString().toInt())
            piercingsShotsView.text = " $piercingsShots "
            piercingsShotsPerBattleView.text = " "+BigDecimal(piercingsShots.toDouble()/battles.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()+" "
            val exp = (player.statistics.all.xp - stats.get("xp")
                .toString().toDouble())/battles.toDouble()
            expView.text = " "+BigDecimal(exp).setScale(2,RoundingMode.HALF_EVEN).toString()+" XP"
        }else{
            battlesView.text = " $battles "
            victoryBattlesView.text = " 0 "
            victoryPercentView.text = " 0 %"
            survivedBattlesView.text = " 0 "
            survivedPercentView.text = " 0 %"
            avgDmgView.text = " 0.0 "
            avgAsistView.text = " 0.0 "
            avgDestroyedTankView.text = " 0 "
            hitRatioView.text = " 0 %"
            piercingsShotsView.text = " 0 "
            piercingsShotsPerBattleView.text = " 0 "
            expView.text = " 0 XP"
        }
    }

}