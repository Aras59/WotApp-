package fragments

import android.graphics.BitmapFactory
import players.interfaces.PlayersInterface
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import okhttp3.ResponseBody
import players.framents.Chart
import players.framents.GraphsFragment
import players.framents.PlayerStatsFragment
import players.framents.PlayerVehicleFragment
import players.playerInfo.PlayerInfo
import players.playerVehicleStats.ListStats
import players.playerVehicleStats.Stats
import java.lang.StringBuilder


class PlayerFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var serverSpinnerAdapter: ArrayAdapter<CharSequence>
    private lateinit var functions: FirebaseFunctions
    private lateinit var nicknameSearchAutoCompleteTextView: AutoCompleteTextView
    private lateinit var playersInterface: PlayersInterface
    private lateinit var clanDetailsInterface: ClanDetailsInterface

    private lateinit var searchButton: Button

    private lateinit var serverLogo: ImageView
    private lateinit var titleNickView: TextView
    private lateinit var playerClanNameTextView: TextView
    private lateinit var playerClanMottoTextView: TextView
    private lateinit var nickLayout: LinearLayout
    private lateinit var playerFragmentLayout: LinearLayout
    private val nickLength:Int = 2
    private lateinit var spinner: Spinner
    private var nickname:String = ""
    private var accountId:String = ""
    private lateinit var logedUserNickname: String
    private lateinit var trackerButton:Button

    private lateinit var playerDataPager: ViewPager2
    private lateinit var playerFragmentsPager: ViewPager2
    private lateinit var calculator: Wn8Calculator
    private lateinit var progressBar: ProgressBar
    private lateinit var playerClanLogoView: ImageView
    private lateinit var playerFragmentsTab: TabLayout
    private val firestore = FirebaseFirestore.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        serverSpinnerAdapter = activity?.let { ArrayAdapter.createFromResource(it,R.array.regions,
            R.layout.spinner_list) } as ArrayAdapter<CharSequence>
        calculator = arguments?.getSerializable("calculator") as Wn8Calculator
        auth = Firebase.auth
        logedUserNickname = auth.currentUser?.displayName.toString()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_player, container, false)

        titleNickView = view.findViewById(R.id.titleNickView)
        serverLogo = view.findViewById(R.id.serverLogo)
        searchButton = view.findViewById(R.id.searchButton)
        nicknameSearchAutoCompleteTextView = view.findViewById(R.id.playerAutoCompleteTextView)
        playerClanNameTextView = view.findViewById(R.id.playerClanNameTextView)
        playerClanMottoTextView = view.findViewById(R.id.playerClanMottoTextView)
        playerFragmentsTab = view.findViewById(R.id.playerFragmentsTab)
        playerFragmentsPager = view.findViewById(R.id.playerFragmentsPager)
        playerClanMottoTextView.visibility = View.INVISIBLE
        playerClanNameTextView.visibility = View.INVISIBLE
        nicknameSearchAutoCompleteTextView.text.clear()
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

        functions = Firebase.functions

        spinner = view.findViewById(R.id.spinner)

        serverSpinnerAdapter.setDropDownViewResource(R.layout.spinner_list)

        spinner.adapter = serverSpinnerAdapter


        trackerButton.setOnClickListener {
            if (nickname != "") {
                addPlayerToFollowingList(nickname)
                trackerButton.visibility = View.INVISIBLE
            } else {
                Toast.makeText(activity,"Cannot follow this player!", Toast.LENGTH_LONG).show()
            }
        }

        nicknameSearchAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.length>nickLength){
                    when(spinner.selectedItem.toString()){
                        "EU" -> {
                            playersInterface = PlayersInterface.createEU()
                            clanDetailsInterface = ClanDetailsInterface.createEU()
                        }
                        "RU" -> {
                            playersInterface = PlayersInterface.createRU()
                            clanDetailsInterface = ClanDetailsInterface.createRU()
                        }
                        "ASIA" -> {
                            playersInterface = PlayersInterface.createASIA()
                            clanDetailsInterface = ClanDetailsInterface.createASIA()
                        }
                        else -> {
                            playersInterface = PlayersInterface.createNA()
                            clanDetailsInterface = ClanDetailsInterface.createNA()
                        }
                    }

                    val getPlayers = playersInterface.getPlayers(nicknameSearchAutoCompleteTextView.text.toString())
                    getPlayers.enqueue(object : Callback<PlayerInfo> {
                        override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if (response.body()?.status != "error" && response.body()?.meta?.count != 0) {
                                val nicknames: List<String> = response.body()?.data?.map {it.nickname}!!
                                val nicknamesAdapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, nicknames) }
                                nicknameSearchAutoCompleteTextView.setAdapter(nicknamesAdapter)
                            }
                        }
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                            //Nothing must be do here but must override this function to use enqueue
                        }
                    })
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Don't want any operation here
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Don't want any operation here
            }
        })

        searchButton.setOnClickListener {
            if(nicknameSearchAutoCompleteTextView.text.length>nickLength){
                when(spinner.selectedItem.toString()){
                    "EU" -> {
                        playersInterface = PlayersInterface.createEU()
                        clanDetailsInterface = ClanDetailsInterface.createEU()
                        serverLogo.setImageResource(R.drawable.euro32)
                    }
                    "RU" -> {
                        playersInterface = PlayersInterface.createRU()
                        clanDetailsInterface = ClanDetailsInterface.createRU()
                        serverLogo.setImageResource(R.drawable.russia32)
                    }
                    "ASIA" -> {
                        playersInterface = PlayersInterface.createASIA()
                        clanDetailsInterface = ClanDetailsInterface.createASIA()
                        serverLogo.setImageResource(R.drawable.china32)
                    }
                    else -> {
                        playersInterface = PlayersInterface.createNA()
                        clanDetailsInterface = ClanDetailsInterface.createNA()
                        serverLogo.setImageResource(R.drawable.us32)
                    }
                }


                progressBar.visibility = View.VISIBLE
                val getPlayers = playersInterface.getPlayers(nicknameSearchAutoCompleteTextView.text.toString())
                getPlayers.enqueue(object : Callback<PlayerInfo> {
                    override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                        if(response.body()?.status != "error" && response.body()?.meta?.count != 0){
                            accountId = response.body()?.data?.first()?.account_id.toString()
                            nickname = response.body()?.data?.first()?.nickname!!
                            val getPlayersPersonalData = playersInterface.getPlayerPersonalData(accountId)
                            getPlayersPersonalData.enqueue(object : Callback<Datas>{
                                override fun onResponse(call: Call<Datas>, response: Response<Datas>) {
                                    if(response.body()?.status != "error" && response.body()?.meta?.count != 0){
                                        val player: Player? = response.body()?.player?.get(accountId)
                                        titleNickView.text = StringBuilder(" "+player?.nickname)
                                        val playerClanID = response.body()?.player?.get(accountId)?.clan_id
                                        if(playerClanID != 0){

                                            val getClanDetails = clanDetailsInterface.getClanDetails(playerClanID.toString())

                                            getClanDetails.enqueue(object : Callback<ClanDetails> {
                                                override fun onResponse(call: Call<ClanDetails>, response: Response<ClanDetails>) {

                                                    val logoUrl = response.body()!!.clan[playerClanID.toString()]?.emblems?.x195?.portal.toString()
                                                    playerClanNameTextView.text = StringBuilder(" ["+response.body()?.clan
                                                        ?.get(playerClanID.toString())?.tag+"] "+response.body()
                                                        ?.clan?.get(playerClanID.toString())?.name)
                                                    playerClanNameTextView.visibility = View.VISIBLE
                                                    playerClanMottoTextView.text = StringBuilder(" "+response.body()?.clan?.get(playerClanID.toString())?.motto)
                                                    playerClanMottoTextView.visibility = View.VISIBLE

                                                    val getClanLogo = clanDetailsInterface.getClanLogo(logoUrl)

                                                    getClanLogo.enqueue(object : Callback<ResponseBody> {
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

                                        val getPlayersVehiclesStats = playersInterface.getPlayersVehiclesStats(accountId)
                                        getPlayersVehiclesStats.enqueue(object : Callback<VehicleStats>{
                                            override fun onResponse(call: Call<VehicleStats>, response: Response<VehicleStats>) {
                                                if(response.body()?.status != "error" && response.body()?.meta?.count != 0) {
                                                    val listOfStatsPerTanks = response.body()?.stats?.get(accountId)

                                                    if (listOfStatsPerTanks != null){
                                                        val wn8 = wn8Calculate(listOfStatsPerTanks)

                                                        if (player != null) {

                                                            colorBackgroundWithWn8(wn8)

                                                            viewPager2ForStatsFragmentsInit(
                                                                listOfStatsPerTanks,
                                                                wn8,
                                                                player
                                                            )

                                                            playerFragmentLayout.visibility =
                                                                View.VISIBLE
                                                            nickLayout.visibility = View.VISIBLE
                                                            playerFragmentsPager.visibility =
                                                                View.VISIBLE
                                                            progressBar.visibility = View.INVISIBLE
                                                            setingVisibilityForTrackerButton(spinner.selectedItem.toString(),accountId)
                                                            nicknameSearchAutoCompleteTextView.text.clear()

                                                        }
                                                    } else {
                                                        progressBar.visibility = View.INVISIBLE
                                                        Toast.makeText(activity,"This player don't have any battles",Toast.LENGTH_SHORT).show()
                                                    }


                                                }
                                            }
                                            override fun onFailure(call: Call<VehicleStats>, t: Throwable) {
                                                progressBar.visibility = View.INVISIBLE
                                                Toast.makeText(activity,"Problem with calculate player WN8",Toast.LENGTH_SHORT).show()
                                            }

                                        })
                                    } else {
                                        progressBar.visibility = View.INVISIBLE
                                        Toast.makeText(activity,"Problem with get player stats!",
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

        return view
    }

    private fun addPlayerToFollowingList(nickname:String): Task<String> {
        val server = when(spinner.selectedItem.toString()){
            "EU" -> "EU"
            "RU" -> "RU"
            "ASIA" -> "ASIA"
            else -> "NA"
        }

        val auth = Firebase.auth
        val data = hashMapOf(
            "followednickname" to nickname,
            "server" to server,
            "usernickname" to auth.currentUser!!.displayName,
        )

        return functions
            .getHttpsCallable("addToFollowAndFollowingDataBase")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
                result

            }
    }

    private fun setingVisibilityForTrackerButton(server: String, accountID: String){
        if(nicknameSearchAutoCompleteTextView.text.toString().equals(logedUserNickname,true)) {
            trackerButton.visibility = View.INVISIBLE
        } else {
            trackerButton.visibility = View.VISIBLE
        }

        firestore.collection("followingusers")
            .document(logedUserNickname)
            .collection(server).document(accountID)
            .get().addOnCompleteListener {
                    task ->
                if (task.isSuccessful && task.result.data != null) {
                    trackerButton.visibility = View.INVISIBLE
                }else {
                    trackerButton.visibility = View.VISIBLE
                }
            }
    }

    private fun colorBackgroundWithWn8(wn8: Double) {
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
    }

    private fun wn8Calculate(listOfStatsPerTanks: List<Stats>?): Double{
        var wn8 = 0.0
        var allRandomBattlesPlayed = 0
        if (listOfStatsPerTanks != null) {
            for(temp in listOfStatsPerTanks){
                if(temp.random.battles>0){
                    allRandomBattlesPlayed+=temp.random.battles
                    wn8 += calculator.calculateWN8byTank(temp.tank_id,temp.random.battles,
                        temp.random.wins.toDouble(),temp.random.dropped_capture_points.toDouble(),
                        temp.random.frags.toDouble(),temp.random.damage_dealt.toDouble(),temp.random.spotted.toDouble())*temp.random.battles
                }
            }
        }
        return wn8/allRandomBattlesPlayed

    }

    private fun viewPager2ForStatsFragmentsInit(listOfStatsPerTanks: List<Stats>?, wn8: Double
                                                , player: Player?){
            val bundle = Bundle()
            val fragments:ArrayList<Fragment> = ArrayList()
            val pagerAdapter = ViewPagerAdapter(fragments, activity as AppCompatActivity)
            val server = when(spinner.selectedItem.toString()){
                "EU" -> "EU"
                "RU" -> "RU"
                "ASIA" -> "ASIA"
                else -> "NA"
            }

            playerFragmentsPager.adapter = pagerAdapter
            fragments.add(PlayerStatsFragment())
            fragments.add(Chart())
            fragments.add(PlayerVehicleFragment())
            for(f in fragments){
                val listStats = ListStats(listOfStatsPerTanks!!)
                bundle.putSerializable("PlayerListStats", listStats)
                bundle.putSerializable("PlayerOverallStats", player)
                bundle.putDouble("WN8", wn8)
                bundle.putString("Server", server)
                f.arguments = bundle
            }
            TabLayoutMediator(playerFragmentsTab, playerFragmentsPager)
            { tab, position ->
                if (position == 0) tab.text = "Player Stats"
                if (position == 1) tab.text = "Graphs"
                if (position == 2) tab.text = "Player Vehicles"
            }.attach()
    }


}