package players.framents

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.wotapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import players.playerstats.Player
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class PlayerDateStatsFragment : Fragment() {
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
    private lateinit var dateSpinner: Spinner
    private val auth = Firebase.auth
    private val fireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_player_date_stats, container
            , false)

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
        dateSpinner = view.findViewById(R.id.dateSpinner)

        val player: Player = arguments?.getSerializable("PlayerOverallStats") as Player
        val server:String = arguments?.getString("Server") as String
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
        val formatted = current.format(formatter)

        fireStore.collection("followingusers")
            .document(auth.currentUser?.displayName.toString()).collection(server)
            .document(player.account_id.toString()).get()
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful && task.result.data != null) {
                    val followingDate = task.result.data!!.get("followingdate").toString()
                    val datesBetweenTodayAndFollow = getDates(followingDate,formatted)
                    val datesAdapter = activity?.let { ArrayAdapter<String>(it,R.layout.spinner_list
                        ,datesBetweenTodayAndFollow) }
                    datesAdapter?.setDropDownViewResource(R.layout.spinner_list)
                    dateSpinner.adapter = datesAdapter
                }
            }

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val date = dateSpinner.selectedItem.toString()
                fireStore.collection("playersstats")
                    .document(date).collection(server)
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
            }

        }

        return view
    }

    private fun getDates(dateString1: String, dateString2: String): ArrayList<String> {
        val dates: ArrayList<String> = ArrayList<String>()
        val df1: DateFormat = SimpleDateFormat("EE MMM dd yyyy")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df1.parse(dateString1)
            date2 = df1.parse(dateString2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1: Calendar = Calendar.getInstance()
        cal1.setTime(date1)
        val cal2: Calendar = Calendar.getInstance()
        cal2.setTime(date2)
        while (!cal1.after(cal2)) {
            dates.add(df1.format(cal1.time))
            cal1.add(Calendar.DATE, 1)
        }
        return dates
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
            avgDmgView.text = " "+ BigDecimal(avgDMG.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()+" "
            val avgAsist = ((player.statistics.all.avg_damage_assisted * player.statistics.all.battles - stats.get("avg_damage_assisted")
                .toString().toDouble() * stats.get("battles").toString().toInt())/battles.toDouble()).toString()

            avgAsistView.text = " "+ BigDecimal(avgAsist.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()+" "
            val avgTanksDestroyed = ((player.statistics.all.frags - stats.get("frags")
                .toString().toInt()).toDouble()/battles.toDouble()).toString()
            avgDestroyedTankView.text = " "+ BigDecimal(avgTanksDestroyed.toDouble()).setScale(2,
                RoundingMode.HALF_EVEN).toString()+" "
            hitRatioView.text = " "+stats.get("hits_percents").toString()+"%"
            val piercingsShots = (player.statistics.all.piercings - stats.get("piercings")
                .toString().toInt())
            piercingsShotsView.text = " $piercingsShots "
            piercingsShotsPerBattleView.text = " "+ BigDecimal(piercingsShots.toDouble()/battles.toDouble()).setScale(2,
                RoundingMode.HALF_EVEN).toString()+" "
            val exp = (player.statistics.all.xp - stats.get("xp")
                .toString().toDouble())/battles.toDouble()
            expView.text = " "+ BigDecimal(exp).setScale(2, RoundingMode.HALF_EVEN).toString()+" XP"
        }
    }

}