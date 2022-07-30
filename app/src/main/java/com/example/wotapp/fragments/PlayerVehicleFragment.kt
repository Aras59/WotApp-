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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wotapp.adapter.VehicleRecycleViewAdapter
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
import java.util.stream.Collectors


class PlayerVehicleFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var playersInterface: PlayersInterface
    private lateinit var getAchieveList: Call<AchiveReponse>
    private lateinit var getVehicleList: Call<VehicleRespond>
    private lateinit var getMarkOfMasteryImage: Call<ResponseBody>
    private lateinit var vehicleRecycleView: RecyclerView
    private lateinit var listStats: ListStats
    private lateinit var markOfMasteryBitmap: HashMap<String,Bitmap>
    private lateinit var nationSpinner: Spinner
    private lateinit var tierSpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var filterButton: ImageButton

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
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_vehicle, container, false)
        progressBar = view.findViewById(R.id.progressBar3)
        vehicleRecycleView = view.findViewById(R.id.vehiclesRecycleView)
        filterButton = view.findViewById(R.id.fillterButton)
        vehicleRecycleView.layoutManager = LinearLayoutManager(activity)
        nationSpinner = view.findViewById(R.id.nationSpinner)
        val nationAdapter = activity?.let { ArrayAdapter.createFromResource(it,R.array.nationArray,
            R.layout.spinner_list) } as ArrayAdapter<CharSequence>
        nationAdapter.setDropDownViewResource(R.layout.spinner_list)
        nationSpinner.adapter = nationAdapter
        typeSpinner = view.findViewById(R.id.typeSpinner)
        val typeAdapter = activity?.let { ArrayAdapter.createFromResource(it,R.array.typeArray,
            R.layout.spinner_list) } as ArrayAdapter<CharSequence>
        nationAdapter.setDropDownViewResource(R.layout.spinner_list)
        typeSpinner.adapter = typeAdapter
        tierSpinner = view.findViewById(R.id.tierSpinner)
        val tierAdapter =  activity?.let { ArrayAdapter.createFromResource(it,R.array.tierArray,
            R.layout.spinner_list) } as ArrayAdapter<CharSequence>
        tierAdapter.setDropDownViewResource(R.layout.spinner_list)
        tierSpinner.adapter = tierAdapter
        progressBar.visibility = View.VISIBLE

        val vehicleStatsForViewList: MutableList<VehicleStatsForView> = arrayListOf()
        getVehicleList.enqueue(object : Callback<VehicleRespond>{
            override fun onResponse(call: Call<VehicleRespond>, response: Response<VehicleRespond>) {
                if(response.body()?.status!="error"){

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

        filterButton.setOnClickListener {
            var vehicleList = ArrayList<VehicleStatsForView>()
            vehicleList = vehicleList
                .stream()
                .filter { i -> i.tank.nation.equals(nationSpinner.selectedItem.toString())}
                .collect(Collectors.toList()) as ArrayList<VehicleStatsForView>
            filterVehicle(vehicleStatsForViewList)
        }

        return view
    }

    private fun filterVehicle(vehicleStatsForViewList: MutableList<VehicleStatsForView>){
        val adapter = vehicleRecycleView.adapter as VehicleRecycleViewAdapter
        var vehicleList = ArrayList<VehicleStatsForView>()
        vehicleList.addAll(vehicleStatsForViewList)
        if("-".equals(nationSpinner.selectedItem.toString())
            && "-".equals(typeSpinner.selectedItem.toString())
            && "-".equals(tierSpinner.selectedItem.toString())) {
            adapter.updateData(vehicleList)
            return
        }
        if(!"-".equals(nationSpinner.selectedItem.toString())){
            vehicleList = vehicleList
                .stream()
                .filter { i -> i.tank.nation.equals(nationSpinner.selectedItem.toString(),true)}
                .collect(Collectors.toList()) as ArrayList<VehicleStatsForView>
        }
        if(!"-".equals(typeSpinner.selectedItem.toString())){
            vehicleList = vehicleList
                .stream()
                .filter { i -> i.tank.type.equals(typeSpinner.selectedItem.toString(),true)}
                .collect(Collectors.toList()) as ArrayList<VehicleStatsForView>
        }
        if(!"-".equals(tierSpinner.selectedItem.toString())){
            vehicleList = vehicleList
                .stream()
                .filter { i -> i.tank.tier == tierSpinner.selectedItem.toString().toInt()}
                .collect(Collectors.toList()) as ArrayList<VehicleStatsForView>
        }
        adapter.updateData(vehicleList)
    }


    private fun initializeView(vehicleStatsForViewList: MutableList<VehicleStatsForView>) {
        vehicleStatsForViewList.sortByDescending { it.tank.nation }
        vehicleStatsForViewList.sortByDescending { it.tank.tier }
        val vehicleList = ArrayList<VehicleStatsForView>()
        vehicleList.addAll(vehicleStatsForViewList)
        val vehicleAdapter = VehicleRecycleViewAdapter(vehicleList,markOfMasteryBitmap)
        vehicleRecycleView.adapter = vehicleAdapter
    }

}