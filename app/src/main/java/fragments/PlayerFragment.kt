package fragments

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


class PlayerFragment : Fragment() {

    private lateinit var searchButton: Button
    private lateinit var nicknameSearch: AutoCompleteTextView
    private lateinit var titleServerView:TextView
    private lateinit var titleNickView:TextView
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
                                Toast.makeText(activity,"Nie ma ",Toast.LENGTH_SHORT).show()
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
                    titleServerView.text = "EU"
                }
                else if(spinner.selectedItem.toString().equals("RU")){
                    playersPersonalDataInterface = PlayersPersonalData.createRU().getPlayerPersonalData(account_id)
                    titleServerView.text = "RU"
                }
                else if(spinner.selectedItem.toString().equals("ASIA")){
                    playersPersonalDataInterface = PlayersPersonalData.createASIA().getPlayerPersonalData(account_id)
                    titleServerView.text = "ASIA"
                }
                else{
                    playersPersonalDataInterface = PlayersPersonalData.createNA().getPlayerPersonalData(account_id)
                    titleServerView.text = "NA"
                }

                playersPersonalDataInterface.enqueue(object : Callback<Datas>{
                    override fun onResponse(call: Call<Datas>, response: Response<Datas>) {
                        if(response!=null){

                            val playersVehiclesInterface: Call<VehicleStats>
                            playersVehiclesInterface = PlayersVehiclesInterface.createEU().getPlayersVehiclesStats(account_id)
                            playersVehiclesInterface.enqueue(object : Callback<VehicleStats>{
                                override fun onResponse(call: Call<VehicleStats>, response: Response<VehicleStats>) {
                                    println(response.body()?.stats?.get("514691088")?.first()?.account_id.toString())
                                    val a = response.body()?.stats?.get("514691088")?.first()
                                    println()


                                }

                                override fun onFailure(call: Call<VehicleStats>, t: Throwable) {
                                    println("XDDDDD")
                                }

                            })

                            var player: Player? = response.body()?.player?.get(account_id)
                            if (player != null) {
                                titleNickView.text = player.nickname
                                for(f in fragments){
                                    val bundle = Bundle()
                                    bundle.putSerializable("PlayerOverallStats",player)
                                    f.arguments = bundle
                                }
                                val pagerAdapter = MyViewPagerAdapter(fragments, activity as AppCompatActivity)
                                playerDataPager = view.findViewById(R.id.playerDataPager)
                                playerDataPager.adapter = pagerAdapter
                            }else{
                                Toast.makeText(activity,"Nie ma takiego gracza",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<Datas>, t: Throwable) {
                        Toast.makeText(activity,"Błąd danych",Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }





        return view;
    }





}