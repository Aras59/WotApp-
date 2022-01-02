package clans.fragments

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
import clans.clandetails.Members
import com.example.wotapp.R
import calculatorWn8.Wn8Calculator
import calculatorWn8.Wn8ExpValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ClanMembersFragment : Fragment() {

    private lateinit var clanMembersTable:TableLayout
    private lateinit var clanMembers:TextView
    private lateinit var paramsView:TextView
    private lateinit var calculator: Wn8Calculator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_clan_members, container, false)
        clanMembersTable = view.findViewById(R.id.clanMembersTable)
        clanMembers = view.findViewById(R.id.clanPlayers)
        paramsView = view.findViewById(R.id.textView11)
        val values: List<Wn8ExpValue> = readWn8File()
        val calculator:Wn8Calculator = Wn8Calculator(values)

        val memberList: Members = arguments?.getSerializable("MembersList") as Members
        val sortOrder = listOf("commander", "executive_officer","personnel_officer","combat_officer","intelligence_officer","recruitment_officer","junior_officer","private","recruit")
        if(memberList.membersList!=null){
            var i:Int = 1
            val sortedlist = memberList.membersList.sortedBy { sortOrder.indexOf(it.role) }

            for(member in sortedlist){
                val tablerow: TableRow = TableRow(activity)
                tablerow.background = resources.getDrawable(R.drawable.border)

                val params = paramsView.layoutParams
                val index: TextView = TextView(activity)
                index.setBackground(activity?.let { ContextCompat.getDrawable(it,R.drawable.border) })
                index.setTextColor(Color.WHITE)
                index.layoutParams = params
                index.textSize = 16.0f

                val nickname: TextView = TextView(activity)
                nickname.setBackground(activity?.let { ContextCompat.getDrawable(it,R.drawable.border) })
                nickname.setTextColor(Color.WHITE)
                nickname.layoutParams = params
                nickname.textSize = 16.0f

                val clanRang: TextView = TextView(activity)
                clanRang.setTextColor(Color.WHITE)
                clanRang.setBackground(activity?.let { ContextCompat.getDrawable(it,R.drawable.border) })
                clanRang.layoutParams = params
                clanRang.textSize = 16.0f




                index.text = " "+i.toString()+". "
                nickname.text = " "+member.account_name
                clanRang.text = " "+member.role.capitalize().replace("_"," ")
                i++
                tablerow.addView(index)
                tablerow.addView(nickname)
                tablerow.addView(clanRang)
                clanMembersTable.addView(tablerow)
            }
            clanMembers.text = (i-1).toString()
        }



        return view
    }

    private fun readWn8File():List<Wn8ExpValue>{
        val input = activity?.applicationContext?.assets?.open("wn8exp.json")
        val size = input?.available()
        val buffer = size?.let { ByteArray(it) }
        if (input != null) {
            input.read(buffer)
        }
        if (input != null) {
            input.close()
        }
        val wn8text = buffer?.let { String(it) }
        var gson = Gson()
        val typeWN8: Type? = object : TypeToken<List<Wn8ExpValue?>?>() {}.type
        return gson.fromJson(wn8text, typeWN8)
    }


}