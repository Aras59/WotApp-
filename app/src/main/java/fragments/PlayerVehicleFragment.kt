package fragments

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import clans.interfaces.ClanDetailsInterface
import com.example.wotapp.R
import okhttp3.ResponseBody
import players.playerVehicleStats.ListStats
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode


class PlayerVehicleFragment : Fragment() {
    private lateinit var playerVehicleTable: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_vehicle, container, false)
        //playerVehicleTable = view.findViewById(R.id.playerVehicleTable)

        val listStats: ListStats = arguments?.getSerializable("PlayerListStats") as ListStats

        for(member in listStats.statsList.sortedBy { it.random.battles }){
            if(member.random.battles!=0){

            }
        }

        return view
    }


}