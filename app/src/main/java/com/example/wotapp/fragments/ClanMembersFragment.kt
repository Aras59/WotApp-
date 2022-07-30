package com.example.wotapp.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.wotapp.models.clandetails.Members
import com.example.wotapp.R
import java.lang.StringBuilder
import java.util.*


class ClanMembersFragment : Fragment() {

    private lateinit var clanMembersTable:TableLayout
    private lateinit var clanMembers:TextView
    private lateinit var paramsView:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_clan_members, container, false)
        clanMembersTable = view.findViewById(R.id.clanMembersTable)
        clanMembers = view.findViewById(R.id.clanPlayers)
        paramsView = view.findViewById(R.id.textView11)


        val memberList: Members = arguments?.getSerializable("MembersList") as Members
        val sortOrder = listOf("commander", "executive_officer","personnel_officer","combat_officer","intelligence_officer","recruitment_officer","junior_officer","private","recruit")
        var i = 1
        val sortedlist = memberList.membersList.sortedBy { sortOrder.indexOf(it.role) }

        for(member in sortedlist){
            val tableRow = TableRow(activity)
            tableRow.background = resources.getDrawable(R.drawable.border)

            val params = paramsView.layoutParams
            val index = TextView(activity)
            index.background = activity?.let { ContextCompat.getDrawable(it,R.drawable.border) }
            index.setTextColor(Color.WHITE)
            index.layoutParams = params
            index.textSize = 16.0f

            val nickname = TextView(activity)
            nickname.background = activity?.let { ContextCompat.getDrawable(it,R.drawable.border) }
            nickname.setTextColor(Color.WHITE)
            nickname.layoutParams = params
            nickname.textSize = 16.0f

            val clanRang = TextView(activity)
            clanRang.setTextColor(Color.WHITE)
            clanRang.background = activity?.let { ContextCompat.getDrawable(it,R.drawable.border) }
            clanRang.layoutParams = params
            clanRang.textSize = 16.0f


            index.text = StringBuilder(" $i. ")
            nickname.text = StringBuilder(" "+member.account_name)
            clanRang.text = StringBuilder(" "+ member.role.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
                .replace("_"," "))
            i++
            tableRow.addView(index)
            tableRow.addView(nickname)
            tableRow.addView(clanRang)
            clanMembersTable.addView(tableRow)
        }
        clanMembers.text = (i-1).toString()



        return view
    }



}