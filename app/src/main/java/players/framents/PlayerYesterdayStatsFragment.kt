package players.framents

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wotapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlayerYesterdayStatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_player_yesterday_stats, container, false)
        val current = LocalDateTime.now().minusDays(1)

        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
        val formatted = current.format(formatter)

        println("Current Date and Time is: $formatted")
        val db = Firebase.firestore

        val docRef = db.collection("playersstats").document(formatted)
            .collection("EU").document("522715376")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
        }

        return view
    }

}