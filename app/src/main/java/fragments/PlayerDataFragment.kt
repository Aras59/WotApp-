package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wotapp.R


class PlayerDataFragment : Fragment() {

    private lateinit var nickTextView:TextView
    private lateinit var wn8TextView:TextView
    private lateinit var avgDmgTextView:TextView
    private lateinit var avgDmgTextValue:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player_data, container, false)
    }

}