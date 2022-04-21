package players.framents

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wotapp.R
import players.playerVehicleStats.ListStats
import android.widget.LinearLayout
import okhttp3.ResponseBody
import players.achive.Achive
import players.achive.AchiveReponse
import players.interfaces.PlayersVehiclesInterface
import players.playerVehicleStats.Stats
import players.vehicles.Tank
import players.vehicles.VehicleRespond
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode


class PlayerVehicleFragment : Fragment() {
    private lateinit var playerVehicleTable: TableLayout
    private lateinit var progressBar3: ProgressBar
    private val playersVehiclesInterface = PlayersVehiclesInterface.createEU()
    private val getAchiveList:Call<AchiveReponse> = playersVehiclesInterface.getAchiveList()
    private val getVehicleList:Call<VehicleRespond> = playersVehiclesInterface.getVehicleList()
    private lateinit var listStats: ListStats

    override fun onCreate(savedInstanceState: Bundle?) {
        listStats = arguments?.getSerializable("PlayerListStats") as ListStats
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_vehicle, container, false)
        playerVehicleTable = view.findViewById(R.id.playerVehicleTable)
        progressBar3 = view.findViewById(R.id.progressBar3)
        progressBar3.visibility = View.VISIBLE


        getAchiveList.enqueue(object : Callback<AchiveReponse>{
            override fun onResponse(call: Call<AchiveReponse>, response: Response<AchiveReponse>) {
                if(response.body()?.status!="error"){
                    val achiveReponse = response.body()?.data!!
                    getVehicleList.enqueue(object : Callback<VehicleRespond>{
                        override fun onResponse(call: Call<VehicleRespond>, response: Response<VehicleRespond>) {
                            if(response.body()?.status!="error"){
                                val vehicleList = response.body()?.data!!
                                for(member in listStats.statsList.sortedByDescending { it.random.battles }){
                                    if(member.random.battles!=0) {
                                        initalizeView(vehicleList, member, achiveReponse)
                                    }
                                }
                                progressBar3.visibility = View.INVISIBLE
                            }else{
                                progressBar3.visibility = View.INVISIBLE
                                Toast.makeText(activity,"Error Download Tank List!",Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<VehicleRespond>, t: Throwable) {
                            progressBar3.visibility = View.INVISIBLE
                            Toast.makeText(activity,"Network Connection Problem!",Toast.LENGTH_SHORT).show()
                        }

                    })
                }else{
                    Toast.makeText(activity,"Error Download Achivement List!",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AchiveReponse>, t: Throwable) {}

        })

        return view
    }


    fun initalizeView(vehicleList: Map<String, Tank> ,member: Stats, achiveReponse: Map<String,Achive>) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1.0f)
        val tablerow = TableRow(activity)
        tablerow.background = resources.getDrawable(R.drawable.border)
        tablerow.layoutParams = params


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
        tankNameView.text = vehicleList[member.tank_id.toString()]?.name

        val tankLevelView = TextView(activity)
        tankLevelView.setTextColor(Color.WHITE)
        tankLevelView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tankLevelView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tankLevelView.textSize = 20.0f
        tankLevelView.text = vehicleList.get(member.tank_id.toString())?.tier.toString() + " Tier " + vehicleList.get(member.tank_id.toString())?.type?.replaceFirstChar { it.uppercaseChar() }


        val tankStats = TextView(activity)
        tankStats.setTextColor(Color.WHITE)
        tankStats.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tankStats.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tankStats.textSize = 24.0f
        tankStats.text = "Stats"

        val tankBattleView = TextView(activity)
        tankBattleView.setTextColor(Color.WHITE)
        tankBattleView.textSize = 22.0f
        tankBattleView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tankBattleView.text = " "+member.random.battles.toString()
        val params1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params1.topMargin = 10
        tankBattleView.layoutParams = params1

        val tankVictoryView = TextView(activity)
        tankVictoryView.setTextColor(Color.WHITE)
        tankVictoryView.textSize = 22.0f
        tankVictoryView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tankVictoryView.text = " "+ BigDecimal((member.random.wins.toDouble()/member.random.battles.toDouble())*100.0).setScale(2,
            RoundingMode.HALF_EVEN).toString()+"%"
        tankBattleView.layoutParams = params1


        val tankDMGView = TextView(activity)
        tankDMGView.setTextColor(Color.WHITE)
        tankDMGView.textSize = 22.0f
        tankDMGView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tankDMGView.text = " "+ BigDecimal(((member.random.damage_dealt.toDouble()/member.random.battles.toDouble()))).setScale(0,
            RoundingMode.HALF_EVEN).toString()
        tankDMGView.layoutParams = params1

        val tankImageView = ImageView(activity)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,2.0f)
        tankImageView.layoutParams = layoutParams

        val masteryImageView = ImageView(activity)
        tankImageView.layoutParams = layoutParams


        val getVehicleLogo = PlayersVehiclesInterface.createEU()
            .getVehicleLogo(vehicleList[member.tank_id.toString()]?.images?.big_icon.toString())

        getVehicleLogo.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val buffer: ByteArray = response.body()!!.bytes()
                val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                tankImageView.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

        })

        var masteryImageUrl = achiveReponse["markOfMastery"]?.options?.get(0)?.image.toString()
        if(member.mark_of_mastery == 4){
            masteryImageUrl = achiveReponse["markOfMastery"]?.options?.get(3)?.image.toString()
        }
        else if(member.mark_of_mastery == 2){
            masteryImageUrl = achiveReponse["markOfMastery"]?.options?.get(1)?.image.toString()
        }
        else if(member.mark_of_mastery == 3){
            masteryImageUrl = achiveReponse["markOfMastery"]?.options?.get(2)?.image.toString()
        }

        val getMarkofMasteryImage = playersVehiclesInterface.getVehicleLogo(masteryImageUrl)

        getMarkofMasteryImage.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val buffer: ByteArray = response.body()!!.bytes()
                val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                masteryImageView.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

        })


        firstLayout.addView(tankNameView)
        firstLayout.addView(tankLevelView)
        secondLayout.addView(tankImageView,params)
        thirdLayout.addView(tankStats)
        thirdLayout.addView(masteryImageView)
        thirdLayout.addView(tankBattleView)
        thirdLayout.addView(tankVictoryView)
        thirdLayout.addView(tankDMGView)
//                            secondLayout.addView(thirdLayout,params)
        firstLayout.addView(secondLayout,params)
        tablerow.addView(firstLayout,500,500)
        tablerow.addView(thirdLayout,600,500)
        playerVehicleTable.addView(tablerow,params)
    }

}