package clans.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import clans.clandetails.Members
import com.example.wotapp.R

class ClanDescriptionFragment : Fragment() {
    private lateinit var clanDescriptionText: TextView
    private lateinit var clanMembersCount: TextView
    private lateinit var clanCreatorView: TextView
    private lateinit var clanAcceptJoinView: TextView
    private lateinit var clanCommanderView: TextView
    private lateinit var clanSh10EloView: TextView
    private lateinit var clanSh8EloView: TextView
    private lateinit var clanSh6EloView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_clan_description, container, false)


        clanDescriptionText = view.findViewById(R.id.clanDesc)
        clanDescriptionText.text = arguments?.getString("ClanDescription")
        clanCommanderView = view.findViewById(R.id.commanderView)
        clanCommanderView.text = arguments?.getString("ClanCommander")
        clanCreatorView = view.findViewById(R.id.creatorView)
        clanCreatorView.text = arguments?.getString("ClanCreator")
        clanAcceptJoinView = view.findViewById(R.id.joinRequestView)
        if(arguments?.getBoolean("JoinRequest").toString().equals("false"))
            clanAcceptJoinView.text = "No accept"
        else
            clanAcceptJoinView.text = "Accept"
        clanMembersCount = view.findViewById(R.id.clanMembersCount)
        clanMembersCount.text  = arguments?.getInt("ClanMembers").toString()
        clanSh10EloView = view.findViewById(R.id.sh10View)
        if(arguments?.getInt("Sh10pos")!=0)
            clanSh10EloView.text = arguments?.getInt("Sh10elo").toString()+" ("+arguments?.getInt("Sh10pos").toString()+")"
        else
            clanSh10EloView.text = arguments?.getInt("Sh10elo").toString()+"(-)"
        clanSh8EloView = view.findViewById(R.id.sh8View)
        if(arguments?.getInt("Sh8pos")!=0)
            clanSh8EloView.text = arguments?.getInt("Sh8elo").toString()+" ("+arguments?.getInt("Sh8pos").toString()+")"
        else
            clanSh8EloView.text = arguments?.getInt("Sh8elo").toString()+"(-)"
        clanSh6EloView = view.findViewById(R.id.sh6View)
        if(arguments?.getInt("Sh6pos")!=0)
            clanSh6EloView.text = arguments?.getInt("Sh6elo").toString()+" ("+arguments?.getInt("Sh6pos").toString()+")"
        else
            clanSh6EloView.text = arguments?.getInt("Sh6elo").toString()+"(-)"


        // Inflate the layout for this fragment
        return view
    }

}