package com.example.wotapp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wotapp.R
import com.example.wotapp.models.playerVehicleStats.ListStats
import android.widget.LinearLayout
import okhttp3.ResponseBody
import com.example.wotapp.models.achive.AchiveReponse
import com.example.wotapp.interfaces.PlayersInterface
import com.example.wotapp.models.playerVehicleStats.VehicleStatsForView
import com.example.wotapp.models.vehicles.VehicleRespond
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.RoundingMode


class PlayerVehicleFragment : Fragment() {
    private lateinit var playerVehicleTable: TableLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var playersInterface: PlayersInterface
    private lateinit var getAchieveList: Call<AchiveReponse>
    private lateinit var getVehicleList: Call<VehicleRespond>
    private lateinit var getMarkOfMasteryImage: Call<ResponseBody>
    private lateinit var listStats: ListStats
    private lateinit var markOfMasteryBitmap: HashMap<String,Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        listStats = arguments?.getSerializable("PlayerListStats") as ListStats
        playersInterface = when(arguments?.getString("Server").toString()){
            "EU" -> PlayersInterface.createEU()
            "RU" -> PlayersInterface.createRU()
            "ASIA" -> PlayersInterface.createASIA()
            else -> PlayersInterface.createNA()
        }
        markOfMasteryBitmap = HashMap()
        getAchieveList = playersInterface.getAchiveList()
        getVehicleList = playersInterface.getVehicleList()

        getMasteryImage(PlayersInterface.markOfMastery3,"3mark")
        getMasteryImage(PlayersInterface.markOfMastery2,"2mark")
        getMasteryImage(PlayersInterface.markOfMastery1,"1mark")
        getMasteryImage(PlayersInterface.aceTanker,"Acemark")

        super.onCreate(savedInstanceState)
    }

    private fun getMasteryImage(url: String, key: String){
        getMarkOfMasteryImage = playersInterface.getVehicleLogo(url)
        getMarkOfMasteryImage.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val buffer: ByteArray = response.body()!!.bytes()
                val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                markOfMasteryBitmap.put(key,bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity,"Problem with achievements icon download",Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_vehicle, container, false)
        playerVehicleTable = view.findViewById(R.id.playerVehicleTable)
        progressBar = view.findViewById(R.id.progressBar3)
        progressBar.visibility = View.VISIBLE


        getVehicleList.enqueue(object : Callback<VehicleRespond>{
            override fun onResponse(call: Call<VehicleRespond>, response: Response<VehicleRespond>) {
                if(response.body()?.status!="error"){
                    val vehicleStatsForViewList: MutableList<VehicleStatsForView> = arrayListOf()
                    val vehicleMap = response.body()?.data
                    for(playerStatisticOnTank in listStats.statsList){
                        val vehicle = vehicleMap?.get(playerStatisticOnTank.tank_id.toString())
                        if (playerStatisticOnTank.random.battles!=0 && vehicle != null) {
                            vehicleStatsForViewList.add(VehicleStatsForView(playerStatisticOnTank,vehicle))
                        }
                    }

                    initializeView(vehicleStatsForViewList)
                    progressBar.visibility = View.INVISIBLE
                }else{
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity,"Error Download Tank List!",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<VehicleRespond>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity,"Network Connection Problem!",Toast.LENGTH_SHORT).show()
            }

        })

        return view
    }


    fun initializeView(vehicleStatsForViewList: MutableList<VehicleStatsForView>) {
        vehicleStatsForViewList.sortByDescending { it.tank.nation }
        vehicleStatsForViewList.sortByDescending { it.tank.tier }
        for(vehicleStats in vehicleStatsForViewList) {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1.0f)
            val tableRow = TableRow(activity)
            tableRow.background = resources.getDrawable(R.drawable.border)
            tableRow.layoutParams = params


            val firstLayout = LinearLayout(context)
            firstLayout.orientation = LinearLayout.VERTICAL

            val secondLayout = LinearLayout(context)
            secondLayout.orientation = LinearLayout.HORIZONTAL

            val thirdLayout = LinearLayout(context)
            thirdLayout.orientation = LinearLayout.VERTICAL

            val tankNameView = TextView(activity)
            tankNameView.setTextColor(Color.WHITE)
            tankNameView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tankNameView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tankNameView.textSize = 24.0f
            tankNameView.text = vehicleStats.tank.name

            val tankLevelView = TextView(activity)
            tankLevelView.setTextColor(Color.WHITE)
            tankLevelView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tankLevelView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tankLevelView.textSize = 20.0f
            tankLevelView.text = StringBuilder(vehicleStats.tank.tier.toString()+ " Tier " + vehicleStats.tank.type.replaceFirstChar { it.uppercaseChar() })



            val tankStats = TextView(activity)
            tankStats.setTextColor(Color.WHITE)
            tankStats.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tankStats.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tankStats.textSize = 24.0f
            tankStats.text = StringBuilder("Stats")

            val tankBattleView = TextView(activity)
            tankBattleView.setTextColor(Color.WHITE)
            tankBattleView.textSize = 22.0f
            tankBattleView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tankBattleView.text = StringBuilder(" "+vehicleStats.statistic.random.battles.toString())
            val params1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params1.topMargin = 10
            tankBattleView.layoutParams = params1

            val tankVictoryView = TextView(activity)
            tankVictoryView.setTextColor(Color.WHITE)
            tankVictoryView.textSize = 22.0f
            tankVictoryView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tankVictoryView.text = StringBuilder(" " + BigDecimal((vehicleStats.statistic.random
                .wins.toDouble() / vehicleStats.statistic.random.battles.toDouble())*100.0)
                .setScale(2, RoundingMode.HALF_EVEN).toString()+"%")
            tankBattleView.layoutParams = params1


            val tankDMGView = TextView(activity)
            tankDMGView.setTextColor(Color.WHITE)
            tankDMGView.textSize = 22.0f
            tankDMGView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tankDMGView.text = StringBuilder(" " + BigDecimal((vehicleStats.statistic.random
                .damage_dealt.toDouble()/vehicleStats.statistic.random.battles.toDouble()))
                .setScale(0, RoundingMode.HALF_EVEN).toString())
            tankDMGView.layoutParams = params1

            val tankImageView = ImageView(activity)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,2.0f)
            tankImageView.layoutParams = layoutParams

            val masteryImageView = ImageView(activity)
            tankImageView.layoutParams = layoutParams


            val getVehicleLogo = playersInterface.getVehicleLogo(vehicleStats.tank.images.big_icon)


            getVehicleLogo.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val buffer: ByteArray = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                    tankImageView.setImageBitmap(bitmap)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

            })

            masteryImageView.setImageBitmap(markOfMasteryBitmap.get("3mark"))
            when(vehicleStats.statistic.mark_of_mastery){
                4 -> masteryImageView.setImageBitmap(markOfMasteryBitmap.get("Acemark"))
                2 -> masteryImageView.setImageBitmap(markOfMasteryBitmap.get("2mark"))
                3 -> masteryImageView.setImageBitmap(markOfMasteryBitmap.get("1mark"))
            }

            firstLayout.addView(tankNameView)
            firstLayout.addView(tankLevelView)
            secondLayout.addView(tankImageView,params)
            thirdLayout.addView(tankStats)
            thirdLayout.addView(masteryImageView)
            thirdLayout.addView(tankBattleView)
            thirdLayout.addView(tankVictoryView)
            thirdLayout.addView(tankDMGView)
            firstLayout.addView(secondLayout,params)
            tableRow.addView(firstLayout,500,500)
            tableRow.addView(thirdLayout,600,500)
            playerVehicleTable.addView(tableRow,params)
        }

    }

}