package fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.red
import players.interfaces.PlayersInterface
import players.interfaces.PlayersPersonalData
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wotapp.R
import players.interfaces.PlayersVehiclesInterface
import players.playerstats.Datas
import players.playerstats.Player
import players.playerstats.PlayersList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import calculatorWn8.Wn8Calculator
import players.playerVehicleStats.Data
import players.playerVehicleStats.VehicleStats
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener





class PlayerFragment : Fragment() {

    private lateinit var searchButton: Button
    private lateinit var nicknameSearch: AutoCompleteTextView
    private lateinit var titleServerView:TextView
    private lateinit var titleNickView:TextView
    private lateinit var nickLayout: LinearLayout
    private val nickLenght:Int = 2;
    private lateinit var spinner: Spinner
    private var nickname:String = ""
    private var account_id:String = ""
    private lateinit var playerDataPager: ViewPager2
    private lateinit var calculator: Wn8Calculator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_player, container, false)

        //INICJALIZACJA WYGLADU
        titleNickView = view.findViewById(R.id.titleNickView)
        titleServerView = view.findViewById(R.id.titleServerView)
        searchButton = view.findViewById(R.id.searchButton)
        nicknameSearch = view.findViewById(R.id.playerAutoCompleteTextView)
        nicknameSearch.text.clear()
        nickLayout = view.findViewById(R.id.nickLayout)
        nickLayout.visibility=View.INVISIBLE
        calculator = arguments?.getSerializable("calculator") as Wn8Calculator

        spinner = view.findViewById(R.id.spinner)

        var adapter = activity?.let { ArrayAdapter.createFromResource(it,R.array.regions, R.layout.spinner_list) }
        if (adapter != null) {
            adapter.setDropDownViewResource(R.layout.spinner_list)
        }
        if(spinner != null) {
            spinner.adapter = adapter
        }

        //DANE
        nicknameSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.length>nickLenght){
                    var playersInterface: Call<PlayersList>
                    if(spinner.selectedItem.toString().equals("EU")){
                        playersInterface = PlayersInterface.createEU().getPlayers(nicknameSearch.text.toString())
                    }
                    else if(spinner.selectedItem.toString().equals("RU")){
                        playersInterface = PlayersInterface.createRU().getPlayers(nicknameSearch.text.toString())
                    }
                    else if(spinner.selectedItem.toString().equals("ASIA")){
                        playersInterface = PlayersInterface.createASIA().getPlayers(nicknameSearch.text.toString())
                    }
                    else{
                        playersInterface = PlayersInterface.createNA().getPlayers(nicknameSearch.text.toString())
                    }

                    playersInterface.enqueue(object : Callback<PlayersList> {
                        override fun onResponse(call: Call<PlayersList>, response: Response<PlayersList>) {
                            if(response!=null){
                                if(response.body()?.data?.isNotEmpty() == true) {
                                    var nicknames: List<String> = emptyList()
                                    nicknames = response.body()?.data?.map{it.nickname}!!
                                    var nicknamesAdapter = activity?.let { ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, nicknames) }
                                    nicknameSearch.setAdapter(nicknamesAdapter)
                                    account_id = response.body()?.data?.first()?.account_id.toString()!!
                                    nickname= response.body()?.data?.first()?.nickname!!
                                }
                            }else{
                                account_id = ""
                                nickname = ""
                                Toast.makeText(activity,"Player doesn't exist!",Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<PlayersList>, t: Throwable) {
                            account_id = ""
                            nickname = ""
                        }
                    })
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        searchButton.setOnClickListener {
            if(nicknameSearch.text.length>nickLenght){

                val fragments:ArrayList<Fragment> = arrayListOf(PlayerOverallStatsFragment(),PlayerOverallStatsFragment())

                var playersPersonalDataInterface: Call<Datas>
                if(spinner.selectedItem.toString().equals("EU")){
                    playersPersonalDataInterface = PlayersPersonalData.createEU().getPlayerPersonalData(account_id)
                }
                else if(spinner.selectedItem.toString().equals("RU")){
                    playersPersonalDataInterface = PlayersPersonalData.createRU().getPlayerPersonalData(account_id)
                }
                else if(spinner.selectedItem.toString().equals("ASIA")){
                    playersPersonalDataInterface = PlayersPersonalData.createASIA().getPlayerPersonalData(account_id)
                }
                else{
                    playersPersonalDataInterface = PlayersPersonalData.createNA().getPlayerPersonalData(account_id)
                }

                playersPersonalDataInterface.enqueue(object : Callback<Datas>{
                    override fun onResponse(call: Call<Datas>, response: Response<Datas>) {
                        var wn8:Double = 0.0
                        if(response!=null){
                            var player: Player? = response.body()?.player?.get(account_id)
                            val bundle = Bundle()
                            val playersVehiclesInterface: Call<VehicleStats>
                            if(spinner.selectedItem.toString().equals("EU")){
                                playersVehiclesInterface = PlayersVehiclesInterface.createEU().getPlayersVehiclesStats(account_id)
                                titleServerView.text = "EU"
                            }
                            else if(spinner.selectedItem.toString().equals("RU")){
                                playersVehiclesInterface = PlayersVehiclesInterface.createRU().getPlayersVehiclesStats(account_id)
                                titleServerView.text = "RU"
                            }
                            else if(spinner.selectedItem.toString().equals("ASIA")){
                                playersVehiclesInterface = PlayersVehiclesInterface.createASIA().getPlayersVehiclesStats(account_id)
                                titleServerView.text = "ASIA"
                            }
                            else{
                                playersVehiclesInterface = PlayersVehiclesInterface.createNA().getPlayersVehiclesStats(account_id)
                                titleServerView.text = "NA"
                            }

                            playersVehiclesInterface.enqueue(object : Callback<VehicleStats>{
                                @SuppressLint("ResourceAsColor")
                                override fun onResponse(call: Call<VehicleStats>, response: Response<VehicleStats>) {
                                    response.code()
                                    val a = response.body()?.stats?.get(account_id.toString())
                                    var x = 0
                                    if (a != null) {
                                        for(temp in a){
                                            if(temp.random.battles>0){
                                                x+=temp.random.battles
                                                wn8 += calculator.calulateWN8byTank(temp.tank_id,temp.random.battles,
                                                    temp.random.wins.toDouble(),temp.random.dropped_capture_points.toDouble(),
                                                    temp.random.frags.toDouble(),temp.random.damage_dealt.toDouble(),temp.random.spotted.toDouble())*temp.random.battles
                                            }
                                        }
                                        wn8 = wn8/x
                                    }


                                    if (player != null) {
                                        titleNickView.text = player.nickname
                                        for(f in fragments){

                                            bundle.putSerializable("PlayerOverallStats",player)
                                            bundle.putDouble("WN8",wn8)
                                            f.arguments = bundle
                                        }
                                        val pagerAdapter = MyViewPagerAdapter(fragments, activity as AppCompatActivity)
                                        playerDataPager = view.findViewById(R.id.playerDataPager)
                                        if(wn8<400.0){
                                              playerDataPager.setBackgroundResource(R.color.red)
                                              searchButton.setBackgroundResource(R.color.red)
                                              nickLayout.setBackgroundResource(R.color.red)
                                          }
                                        if(wn8<859.0 && wn8>399.0){
                                             playerDataPager.setBackgroundResource(R.color.oragne)
                                             searchButton.setBackgroundResource(R.color.oragne)
                                             nickLayout.setBackgroundResource(R.color.oragne)
                                         }
                                        if(wn8<1419.0 && wn8>858.0){
                                            playerDataPager.setBackgroundResource(R.color.yellow)
                                            searchButton.setBackgroundResource(R.color.yellow)
                                            nickLayout.setBackgroundResource(R.color.yellow)
                                        }
                                        if(wn8<2104.0 && wn8>1418.0){
                                            playerDataPager.setBackgroundResource(R.color.green)
                                            searchButton.setBackgroundResource(R.color.green)
                                            nickLayout.setBackgroundResource(R.color.green)
                                        }
                                        if(wn8<2769.0 && wn8>2103.0){
                                            playerDataPager.setBackgroundResource(R.color.blue)
                                            searchButton.setBackgroundResource(R.color.blue)
                                            nickLayout.setBackgroundResource(R.color.blue)
                                        }
                                        if(wn8>2768.0){
                                            playerDataPager.setBackgroundResource(R.color.purple_500)
                                            searchButton.setBackgroundResource(R.color.purple_500)
                                            nickLayout.setBackgroundResource(R.color.purple_500)

                                        }
                                        playerDataPager.adapter = pagerAdapter
                                        nickLayout.visibility=View.VISIBLE
                                        nicknameSearch.text.clear()
                                    }else{
                                        Toast.makeText(activity,"Player doesn't exist!",Toast.LENGTH_LONG).show()
                                    }

                                }

                                override fun onFailure(call: Call<VehicleStats>, t: Throwable) {
                                    Toast.makeText(activity,"Player doesn't exist!",Toast.LENGTH_LONG).show()
                                }

                            })


                        }
                    }
                    override fun onFailure(call: Call<Datas>, t: Throwable) {
                        Toast.makeText(activity,"Network Error!",Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
//
//        spinner.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View,
//                position: Int,
//                id: Long
//            ) {
//                nicknameSearch.text.clear()
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                nicknameSearch.text.clear()
//            }
//        }

        return view;
    }




}