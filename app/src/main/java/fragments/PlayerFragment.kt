package fragments

import accounts.*
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
import com.example.wotapp.MainActivity
import com.example.wotapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val fragments:ArrayList<Fragment> = arrayListOf(PlayerDataFragment(),PlayerDataFragment(),PlayerDataFragment())
        val pagerAdapter = MyViewPagerAdapter(fragments, this.activity as AppCompatActivity)
        playerDataPager = view.findViewById(R.id.playerDataPager)
        playerDataPager.adapter = pagerAdapter
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
                            var player: Player? = response.body()?.player?.get(account_id)
                            if (player != null) {
                                titleNickView.text = player.nickname
                                if(player?.statistics?.all?.battles!=0){
                                    Toast.makeText(activity,(player?.statistics?.all?.damage_dealt!! / player?.statistics?.all?.battles).toString(),Toast.LENGTH_SHORT).show()
                                }else
                                    Toast.makeText(activity,"0",Toast.LENGTH_SHORT).show()
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