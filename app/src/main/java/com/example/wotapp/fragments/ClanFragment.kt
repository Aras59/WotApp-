package com.example.wotapp.fragments

import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.example.wotapp.models.clandatas.Clan
import com.example.wotapp.models.clandetails.ClanDetails
import com.example.wotapp.models.clandetails.Members
import com.example.wotapp.models.clanratings.ClanRatings
import com.example.wotapp.interfaces.ClanDetailsInterface
import com.example.wotapp.interfaces.ClanInterface
import com.example.wotapp.interfaces.ClanRatingsInterface
import com.example.wotapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClanFragment : Fragment() {
    private lateinit var clanAutoCompleteTextView: AutoCompleteTextView
    private lateinit var spinner: Spinner
    private lateinit var searchButton: Button
    private lateinit var clanLogoView: ImageView
    private lateinit var clanNameView: TextView
    private lateinit var clanMottoView: TextView
    private lateinit var clanInfoTabs: TabLayout
    private lateinit var clanDataLayout: LinearLayout
    private lateinit var clanPagerLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var clanPager: ViewPager2
    private var clanTag: String = ""
    private var clanID: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_clan, container, false)

        clanAutoCompleteTextView = view.findViewById(R.id.clansAutoCompleteTextView)
        spinner = view.findViewById(R.id.spinner)
        searchButton = view.findViewById(R.id.searchButton)
        clanLogoView = view.findViewById(R.id.clanLogoView)
        clanNameView = view.findViewById(R.id.clanNameView)
        clanMottoView = view.findViewById(R.id.clanMotto)
        clanInfoTabs = view.findViewById(R.id.clanInfoTabs)
        clanPager = view.findViewById(R.id.clanPager)
        clanDataLayout = view.findViewById(R.id.clanDataLayout)
        clanDataLayout.visibility = View.INVISIBLE
        clanPagerLayout = view.findViewById(R.id.clanPagerLayout)
        clanPagerLayout.visibility = View.INVISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        val adapter = activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.regions,
                R.layout.spinner_list
            )
        }
        adapter?.setDropDownViewResource(R.layout.spinner_list)
        spinner.adapter = adapter


        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (clanAutoCompleteTextView.text.length > 2) {
                    val clansInterface: Call<Clan> = if (spinner.selectedItem.toString().equals("EU")) {
                        ClanInterface.createEU()
                            .getClan(clanAutoCompleteTextView.text.toString())
                    } else if (spinner.selectedItem.toString().equals("RU")) {
                        ClanInterface.createRU()
                            .getClan(clanAutoCompleteTextView.text.toString())
                    } else if (spinner.selectedItem.toString().equals("ASIA")) {
                        ClanInterface.createASIA()
                            .getClan(clanAutoCompleteTextView.text.toString())
                    } else {
                        ClanInterface.createNA()
                            .getClan(clanAutoCompleteTextView.text.toString())
                    }

                    clansInterface.enqueue(object : Callback<Clan> {
                        override fun onResponse(call: Call<Clan>, response: Response<Clan>
                        ) {
                            if (response.body()?.status!="error") {
                                if (response.body()?.data?.isNotEmpty() == true) {

                                    val clanTags: List<String> = response.body()?.data?.map { it.tag }!!
                                    val clanTagsAdapter = activity?.let {
                                        ArrayAdapter(
                                            it,
                                            android.R.layout.simple_list_item_1,
                                            clanTags
                                        )
                                    }
                                    clanAutoCompleteTextView.setAdapter(clanTagsAdapter)
                                    clanID = response.body()?.data?.first()?.clan_id.toString()
                                    clanTag = response.body()?.data?.first()?.tag!!
                                }
                            } else {
                                clanID = ""
                                clanTag = ""
                                Toast.makeText(activity, "Clan doesn't exist!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Clan>, t: Throwable) {
                            clanID = ""
                            clanTag = ""
                        }
                    })
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        })



        clanAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.length > 2) {
                        val clansInterface: Call<Clan> = if (spinner.selectedItem.toString() == "EU") {
                            ClanInterface.createEU().getClan(clanAutoCompleteTextView.text.toString())
                        } else if (spinner.selectedItem.toString() == "RU") {
                            ClanInterface.createRU().getClan(clanAutoCompleteTextView.text.toString())
                        } else if (spinner.selectedItem.toString() == "ASIA") {
                            ClanInterface.createASIA().getClan(clanAutoCompleteTextView.text.toString())
                        } else {
                            ClanInterface.createNA().getClan(clanAutoCompleteTextView.text.toString())
                        }

                        clansInterface.enqueue(object : Callback<Clan> {
                            override fun onResponse(call: Call<Clan>, response: Response<Clan>) {
                                if (response.body()?.status!="error") {
                                    if (response.body()?.data?.isNotEmpty() == true) {
                                        val clanTags: List<String> = response.body()?.data?.map { it.tag }!!
                                        val clanTagsAdapter = activity?.let {
                                            ArrayAdapter(it, android.R.layout.simple_list_item_1,clanTags)
                                        }
                                        clanAutoCompleteTextView.setAdapter(clanTagsAdapter)
                                        clanID =
                                            response.body()?.data?.first()?.clan_id.toString()
                                        clanTag = response.body()?.data?.first()?.tag!!
                                    }
                                }
                            }
                            override fun onFailure(call: Call<Clan>, t: Throwable) {
                                Toast.makeText(activity, "Network Connection Problem!", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        })

        searchButton.setOnClickListener {
            if (clanAutoCompleteTextView.text.length > 2) {
                progressBar.visibility = View.VISIBLE

                val clansInterface: Call<Clan> = if (spinner.selectedItem.toString().equals("EU")) {
                    ClanInterface.createEU().getClan(clanAutoCompleteTextView.text.toString())
                } else if (spinner.selectedItem.toString().equals("RU")) {
                    ClanInterface.createRU().getClan(clanAutoCompleteTextView.text.toString())
                } else if (spinner.selectedItem.toString().equals("ASIA")) {
                    ClanInterface.createASIA().getClan(clanAutoCompleteTextView.text.toString())
                } else {
                    ClanInterface.createNA().getClan(clanAutoCompleteTextView.text.toString())
                }

                clansInterface.enqueue(object : Callback<Clan> {
                    override fun onResponse(call: Call<Clan>, response: Response<Clan>) {
                        if (response.body()?.status!="error") {
                            if (response.body()?.data?.isNotEmpty() == true) {
                                val clanTags: List<String> = response.body()?.data?.map { it.tag }!!
                                val clanTagsAdapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, clanTags) }

                                clanAutoCompleteTextView.setAdapter(clanTagsAdapter)
                                clanID = response.body()?.data?.first()?.clan_id.toString()
                                clanTag = response.body()?.data?.first()?.tag!!

                                val clanDetailsInterface: Call<ClanDetails> =
                                    if (spinner.selectedItem.toString().equals("EU")) {
                                    ClanDetailsInterface.createEU().getClanDetails(clanID)
                                } else if (spinner.selectedItem.toString().equals("RU")) {
                                    ClanDetailsInterface.createRU().getClanDetails(clanID)
                                } else if (spinner.selectedItem.toString().equals("ASIA")) {
                                    ClanDetailsInterface.createASIA().getClanDetails(clanID)
                                } else {
                                    ClanDetailsInterface.createNA().getClanDetails(clanID)
                                }

                                clanDetailsInterface.enqueue(object : Callback<ClanDetails> {
                                    override fun onResponse(call: Call<ClanDetails>, response: Response<ClanDetails>){
                                        if (response.body() != null) {
                                            //CLAN LOGO DOWNLOAD
                                            val logoUrl: String? =
                                                response.body()!!.clan[clanID]?.emblems?.x195?.portal
                                            if (logoUrl != null) {
                                                val logoCall: Call<ResponseBody>
                                                if (spinner.selectedItem.toString().equals("EU")) {
                                                    logoCall = ClanDetailsInterface.createEU()
                                                        .getClanLogo(logoUrl)
                                                } else if (spinner.selectedItem.toString()
                                                        .equals("RU")
                                                ) {
                                                    logoCall = ClanDetailsInterface.createRU()
                                                        .getClanLogo(logoUrl)
                                                } else if (spinner.selectedItem.toString()
                                                        .equals("ASIA")
                                                ) {
                                                    logoCall = ClanDetailsInterface.createASIA()
                                                        .getClanLogo(logoUrl)
                                                } else {
                                                    logoCall = ClanDetailsInterface.createNA()
                                                        .getClanLogo(logoUrl)
                                                }
                                                logoCall.enqueue(object : Callback<ResponseBody> {
                                                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                        val buffer: ByteArray =
                                                            response.body()!!.bytes()
                                                        val bitmap = BitmapFactory.decodeByteArray(
                                                            buffer,
                                                            0,
                                                            buffer.size
                                                        )
                                                        clanLogoView.setImageBitmap(bitmap)
                                                    }

                                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

                                                })

                                            }

                                            if (response.body()?.clan?.get(clanID)?.color != null) {
                                                clanNameView.text = "[" + response.body()?.clan?.get(clanID)?.tag + "] " + response.body()?.clan?.get(clanID)?.name
                                                clanNameView.setTextColor(Color.parseColor(response.body()?.clan?.get(clanID)?.color))
                                                searchButton.setBackgroundColor(Color.parseColor(response.body()?.clan?.get(clanID)?.color))
                                            }

                                            if (response.body()?.clan?.get(clanID)?.motto != null)
                                                clanMottoView.text = response.body()?.clan?.get(clanID)?.motto

                                            //Clan Ratings

                                            val clanRatings: Call<ClanRatings> =
                                                if (spinner.selectedItem.toString().equals("EU"))
                                                ClanRatingsInterface.createEU().getClanRatings(clanID)
                                            else if (spinner.selectedItem.toString().equals("RU"))
                                                ClanRatingsInterface.createRU().getClanRatings(clanID)
                                            else if (spinner.selectedItem.toString().equals("ASIA"))
                                                ClanRatingsInterface.createASIA().getClanRatings(clanID)
                                            else
                                                ClanRatingsInterface.createNA().getClanRatings(clanID)

                                            var sh10elo = 1000
                                            var sh8elo = 1000
                                            var sh6elo = 1000
                                            var sh6pos = 0
                                            var sh8pos = 0
                                            var sh10pos = 0

                                            val parentRespond = response
                                            clanRatings.enqueue(object : Callback<ClanRatings> {
                                                override fun onResponse(call: Call<ClanRatings>, response: Response<ClanRatings>) {
                                                    if (response.body() != null) {
                                                        if (response.body()?.ratings?.get(clanID)?.fb_elo_rating_10?.value != null)
                                                            sh10elo = response.body()?.ratings?.get(clanID)?.fb_elo_rating_10?.value!!

                                                        sh10pos = response.body()?.ratings?.get(clanID)?.fb_elo_rating_10?.rank!!

                                                        if (response.body()?.ratings?.get(clanID)?.fb_elo_rating_8?.value != null)
                                                            sh8elo = response.body()?.ratings?.get(clanID)?.fb_elo_rating_8?.value!!

                                                        sh8pos = response.body()?.ratings?.get(clanID)?.fb_elo_rating_8?.rank!!

                                                        if (response.body()?.ratings?.get(clanID)?.fb_elo_rating_6?.value != null)

                                                            sh6elo = response.body()?.ratings?.get(clanID)?.fb_elo_rating_6?.value!!

                                                        sh6pos = response.body()?.ratings?.get(clanID)?.fb_elo_rating_6?.rank!!

                                                    }

                                                    val fragments: ArrayList<Fragment> = arrayListOf(
                                                        ClanDescriptionFragment(), ClanMembersFragment()
                                                    )

                                                    for (f in fragments) {
                                                        val bundle = Bundle()
                                                        if (parentRespond.body()?.clan?.get(clanID)?.members != null) {
                                                            val membersList = Members(parentRespond.body()?.clan?.get(clanID)?.members!!)
                                                            bundle.putSerializable("MembersList", membersList)
                                                        }

                                                        bundle.putString(
                                                            "ClanDescription",
                                                            parentRespond.body()?.clan?.get(clanID)?.description
                                                        )
                                                        bundle.putString(
                                                            "ClanCommander",
                                                            parentRespond.body()?.clan?.get(clanID)?.leader_name
                                                        )
                                                        bundle.putString(
                                                            "ClanCreator",
                                                            parentRespond.body()?.clan?.get(clanID)?.creator_name
                                                        )
                                                        bundle.putInt("Sh10pos", sh10pos)
                                                        bundle.putInt("Sh8pos", sh8pos)
                                                        bundle.putInt("Sh6pos", sh6pos)
                                                        bundle.putInt("Sh10elo", sh10elo)
                                                        bundle.putInt("Sh8elo", sh8elo)
                                                        bundle.putInt("Sh6elo", sh6elo)

                                                        if (parentRespond.body()?.clan?.get(clanID)?.accepts_join_requests != null)
                                                            bundle.putBoolean("JoinRequest", parentRespond.body()?.clan?.get(clanID)?.accepts_join_requests!!)
                                                        else
                                                            bundle.putBoolean("JoinRequest", false)

                                                        bundle.putString("ClanCreator", parentRespond.body()?.clan?.get(clanID)?.creator_name)
                                                        bundle.putString("ClanCreator", parentRespond.body()?.clan?.get(clanID)?.creator_name)
                                                        if (parentRespond.body()?.clan?.get(clanID)?.members?.size != null)
                                                            bundle.putInt("ClanMembers", parentRespond.body()?.clan?.get(clanID)?.members?.size!!)

                                                        if (spinner.selectedItem.toString().equals("EU"))
                                                            bundle.putString("Server", "EU")
                                                        else if (spinner.selectedItem.toString().equals("RU"))
                                                            bundle.putString("Server", "RU")
                                                        else if (spinner.selectedItem.toString().equals("ASIA"))
                                                            bundle.putString("Server", "ASIA")
                                                        else
                                                            bundle.putString("Server", "NA")

                                                        f.arguments = bundle
                                                    }
                                                    val clanAdapter = ViewPagerAdapter(fragments, activity as AppCompatActivity)
                                                    clanPager.adapter = clanAdapter

                                                    TabLayoutMediator(
                                                        clanInfoTabs,
                                                        clanPager
                                                    ) { tab, position ->
                                                        if (position == 0) tab.text = "Description"
                                                        if (position == 1) tab.text = "Clan Members"
                                                    }.attach()

                                                    clanAutoCompleteTextView.text.clear()
                                                    progressBar.visibility = View.INVISIBLE
                                                    clanDataLayout.visibility = View.VISIBLE
                                                    clanPagerLayout.visibility = View.VISIBLE
                                                    clanID = ""
                                                    clanTag = ""
                                                }

                                                override fun onFailure(call: Call<ClanRatings>, t: Throwable) {}
                                            })
                                        }
                                    }
                                    override fun onFailure(call: Call<ClanDetails>, t: Throwable) {}
                                })
                            }
                        } else {
                            clanAutoCompleteTextView.text.clear()
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(activity, "Wrong Clan Tag!", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Clan>, t: Throwable) {
                        Toast.makeText(activity, "Network Connection Problem!", Toast.LENGTH_LONG).show()
                    }
                })

            }

        }

        return view
    }
}
