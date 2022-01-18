package fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import calculatorWn8.Wn8Calculator
import players.playerVehicleStats.VehicleStats

import clans.clandetails.ClanDetails
import clans.interfaces.ClanDetailsInterface
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import okhttp3.ResponseBody
import players.framents.PlayerStatsFragment
import players.framents.PlayerVehicleFragment
import players.playerInfo.PlayerInfo
import players.playerVehicleStats.ListStats


class PlayerFragment : Fragment() {

    private lateinit var searchButton: Button
    private lateinit var nicknameSearch: AutoCompleteTextView
    private lateinit var titleServerView:TextView
    private lateinit var titleNickView:TextView
    private lateinit var playerClanNameTextView: TextView
    private lateinit var playerClanMottoTextView: TextView
    private lateinit var nickLayout: LinearLayout
    private lateinit var playerFragmentLayout: LinearLayout
    private val nickLenght:Int = 2;
    private lateinit var spinner: Spinner
    private var nickname:String = ""
    private var account_id:String = ""
    private lateinit var trackerButton:Button

    private lateinit var playerDataPager: ViewPager2
    private lateinit var playerFragmentsPager: ViewPager2
    private lateinit var calculator: Wn8Calculator
    private lateinit var progressBar: ProgressBar
    private lateinit var playerClanLogoView: ImageView
    private lateinit var playerFragmentsTab: TabLayout
    private lateinit var functions: FirebaseFunctions

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
        playerClanNameTextView = view.findViewById(R.id.playerClanNameTextView)
        playerClanMottoTextView = view.findViewById(R.id.playerClanMottoTextView)
        playerFragmentsTab = view.findViewById(R.id.playerFragmentsTab)
        playerFragmentsPager = view.findViewById(R.id.playerFragmentsPager)
        playerClanMottoTextView.visibility = View.INVISIBLE
        playerClanNameTextView.visibility = View.INVISIBLE
        nicknameSearch.text.clear()
        nickLayout = view.findViewById(R.id.nickLayout)
        nickLayout.visibility = View.INVISIBLE
        playerFragmentLayout = view.findViewById(R.id.playerFragmentLayout)
        playerFragmentLayout.visibility = View.INVISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        playerClanLogoView = view.findViewById(R.id.playerClanLogoView)
        playerClanLogoView.visibility = View.INVISIBLE
        trackerButton = view.findViewById(R.id.trackerButton)
        trackerButton.visibility = View.INVISIBLE
        playerDataPager = view.findViewById(R.id.playerDataPager)
        playerDataPager.visibility = View.INVISIBLE
        calculator = arguments?.getSerializable("calculator") as Wn8Calculator
        functions = Firebase.functions

        spinner = view.findViewById(R.id.spinner)

        var adapter = activity?.let { ArrayAdapter.createFromResource(it,R.array.regions, R.layout.spinner_list) }
        if (adapter != null) {
            adapter.setDropDownViewResource(R.layout.spinner_list)
        }
        if(spinner != null) {
            spinner.adapter = adapter
        }

        trackerButton.setOnClickListener {
            if(nickname!=""&&nickname!=null){
                addPlayerToFollowingList(nickname)
            }else{
                Toast.makeText(activity,"Cannot follow this player!", Toast.LENGTH_LONG).show()
            }

        }

        //DANE
        nicknameSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.length>nickLenght){
                    var playersInterface: Call<PlayerInfo>
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

                    playersInterface.enqueue(object : Callback<PlayerInfo> {
                        override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if(response.body()?.status!="error"){
                                if(response.body()?.meta?.count!=0){
                                    var nicknames: List<String> = emptyList()
                                    nicknames = response.body()?.data?.map {it.nickname}!!
                                    var nicknamesAdapter = activity?.let { ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, nicknames) }
                                    nicknameSearch.setAdapter(nicknamesAdapter)
                                }
                            }
                        }
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {}
                    })
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        searchButton.setOnClickListener {
            if(nicknameSearch.text.length>nickLenght){
                progressBar.visibility = View.VISIBLE
                var playersInterface: Call<PlayerInfo>
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

                    playersInterface.enqueue(object : Callback<PlayerInfo> {
                        override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if((response.body()?.status!="error")){
                                if(response.body()?.meta?.count!=0) {

                                    account_id = response.body()?.data?.first()?.account_id.toString()!!
                                    nickname= response.body()?.data?.first()?.nickname!!
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
                                            if(response.body()?.meta?.count!=0){
                                                var player: Player? = response.body()?.player?.get(account_id)

                                                val playerClanID = response.body()?.player?.get(account_id)?.clan_id
                                                if(playerClanID!=0){
                                                    var clanDetailsInterface: Call<ClanDetails>
                                                    if (spinner.selectedItem.toString().equals("EU")) {
                                                        clanDetailsInterface =
                                                            ClanDetailsInterface.createEU().getClanDetails(playerClanID.toString())
                                                    } else if (spinner.selectedItem.toString().equals("RU")) {
                                                        clanDetailsInterface =
                                                            ClanDetailsInterface.createRU().getClanDetails(playerClanID.toString())
                                                    } else if (spinner.selectedItem.toString().equals("ASIA")) {
                                                        clanDetailsInterface =
                                                            ClanDetailsInterface.createASIA().getClanDetails(playerClanID.toString())
                                                    } else {
                                                        clanDetailsInterface = ClanDetailsInterface.createNA().getClanDetails(playerClanID.toString())
                                                    }


                                                    clanDetailsInterface.enqueue(object : Callback<ClanDetails> {
                                                        override fun onResponse(call: Call<ClanDetails>, response: Response<ClanDetails>) {

                                                            val logoUrl: String? = response.body()!!.clan.get(playerClanID.toString())?.emblems?.x195?.portal
                                                            if (logoUrl != null) {
                                                                playerClanNameTextView.text = " ["+response.body()?.clan?.get(playerClanID.toString())?.tag+"] "+response.body()?.clan?.get(playerClanID.toString())?.name
                                                                playerClanNameTextView.visibility = View.VISIBLE
                                                                playerClanMottoTextView.text = " "+response.body()?.clan?.get(playerClanID.toString())?.motto
                                                                playerClanMottoTextView.visibility = View.VISIBLE
                                                                var logoCall: Call<ResponseBody>
                                                                if (spinner.selectedItem.toString().equals("EU"))
                                                                    logoCall = ClanDetailsInterface.createEU().getClanLogo(logoUrl)
                                                                else if (spinner.selectedItem.toString().equals("RU"))
                                                                    logoCall = ClanDetailsInterface.createRU().getClanLogo(logoUrl)
                                                                else if (spinner.selectedItem.toString().equals("ASIA"))
                                                                    logoCall = ClanDetailsInterface.createASIA().getClanLogo(logoUrl)
                                                                else
                                                                    logoCall = ClanDetailsInterface.createNA().getClanLogo(logoUrl)

                                                                logoCall.enqueue(object : Callback<ResponseBody> {
                                                                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                                        val buffer: ByteArray = response.body()!!.bytes()
                                                                        val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                                                                        playerClanLogoView.setImageBitmap(bitmap)
                                                                        playerClanLogoView.visibility = View.VISIBLE
                                                                    }

                                                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                                        playerClanLogoView.visibility = View.INVISIBLE
                                                                        playerClanNameTextView.visibility = View.INVISIBLE
                                                                        playerClanMottoTextView.visibility = View.INVISIBLE
                                                                    }

                                                                })
                                                            }

                                                        }

                                                        override fun onFailure(call: Call<ClanDetails>, t: Throwable) {
                                                            playerClanLogoView.visibility = View.INVISIBLE
                                                            playerClanNameTextView.visibility = View.INVISIBLE
                                                            playerClanMottoTextView.visibility = View.INVISIBLE
                                                        }

                                                    })
                                                }else{
                                                    playerClanLogoView.visibility = View.INVISIBLE
                                                    playerClanNameTextView.visibility = View.INVISIBLE
                                                    playerClanMottoTextView.visibility = View.INVISIBLE
                                                }



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

                                                            if(wn8<650.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.red)
                                                                searchButton.setBackgroundResource(R.color.red)
                                                                nickLayout.setBackgroundResource(R.color.red)
                                                            }
                                                            if(wn8<1050.0 && wn8>649.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.oragne)
                                                                searchButton.setBackgroundResource(R.color.oragne)
                                                                nickLayout.setBackgroundResource(R.color.oragne)
                                                            }
                                                            if(wn8<1250.0 && wn8>1049.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.yellow)
                                                                searchButton.setBackgroundResource(R.color.yellow)
                                                                nickLayout.setBackgroundResource(R.color.yellow)
                                                            }
                                                            if(wn8<1400.0 && wn8>1249.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.green)
                                                                searchButton.setBackgroundResource(R.color.green)
                                                                nickLayout.setBackgroundResource(R.color.green)
                                                            }
                                                            if(wn8<1600.0 && wn8>1399.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.dark_green)
                                                                searchButton.setBackgroundResource(R.color.dark_green)
                                                                nickLayout.setBackgroundResource(R.color.dark_green)
                                                            }
                                                            if(wn8<2000.0 && wn8>1599.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.blue)
                                                                searchButton.setBackgroundResource(R.color.blue)
                                                                nickLayout.setBackgroundResource(R.color.blue)
                                                            }
                                                            if(wn8<2450.0 && wn8>1999.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.dark_blue)
                                                                searchButton.setBackgroundResource(R.color.dark_blue)
                                                                nickLayout.setBackgroundResource(R.color.dark_blue)
                                                            }
                                                            if(wn8<2850.0 && wn8>2449.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.purple)
                                                                searchButton.setBackgroundResource(R.color.purple)
                                                                nickLayout.setBackgroundResource(R.color.purple)
                                                            }
                                                            if(wn8>2849.0){
                                                                playerFragmentLayout.setBackgroundResource(R.color.dark_purple)
                                                                searchButton.setBackgroundResource(R.color.dark_purple)
                                                                nickLayout.setBackgroundResource(R.color.dark_purple)
                                                            }



                                                            //ViewPager2 Init
                                                            val bundle = Bundle()
                                                            val fragments:ArrayList<Fragment> = ArrayList<Fragment>()
                                                            val pagerAdapter = ViewPagerAdapter(fragments, activity as AppCompatActivity)
                                                            playerFragmentsPager.adapter = pagerAdapter
                                                            fragments.add(PlayerStatsFragment())
                                                            fragments.add(PlayerVehicleFragment())
                                                            for(f in fragments){
                                                                val listStats:ListStats = ListStats(a!!)
                                                                bundle.putSerializable("PlayerListStats",listStats)
                                                                bundle.putSerializable("PlayerOverallStats",player)
                                                                bundle.putDouble("WN8",wn8)
                                                                f.arguments = bundle
                                                            }
                                                            TabLayoutMediator(playerFragmentsTab, playerFragmentsPager)
                                                            { tab, position ->
                                                                if (position == 0) tab.text = "Player Stats"
                                                                if (position == 1) tab.text = "Player Vehicles"
                                                            }.attach()

                                                            playerFragmentLayout.visibility = View.VISIBLE
                                                            progressBar.visibility = View.INVISIBLE
                                                            nickLayout.visibility=View.VISIBLE
                                                            playerFragmentsPager.visibility = View.VISIBLE
                                                            trackerButton.visibility = View.VISIBLE
                                                            nicknameSearch.text.clear()
                                                            titleNickView.text = " "+player.nickname
                                                        }


                                                    }

                                                    override fun onFailure(call: Call<VehicleStats>, t: Throwable) {
                                                        progressBar.visibility = View.INVISIBLE
                                                        Toast.makeText(activity,"Player doesn't exist!",Toast.LENGTH_SHORT).show()
                                                    }

                                                })
                                            }
                                            else{
                                                progressBar.visibility = View.INVISIBLE
                                                Toast.makeText(activity,"Player doesn't exist!",
                                                    Toast.LENGTH_LONG).show()
                                            }
                                        }
                                        override fun onFailure(call: Call<Datas>, t: Throwable) {
                                            progressBar.visibility = View.INVISIBLE
                                            Toast.makeText(activity, "Network Connection Problem!", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                }else{
                                    progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(activity,"Player doesn't exist!", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                progressBar.visibility = View.INVISIBLE
                                Toast.makeText(activity,"Wrong Nickname!",Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(activity, "Network Connection Problem!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }


        return view;
    }

    private fun addPlayerToFollowingList(nickname:String): Task<String> {
        val data = hashMapOf(
            "nickname" to nickname
        )

        return functions
            .getHttpsCallable("addToTrackList")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
                result

            }
    }


}