package com.example.wotapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.example.wotapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.example.wotapp.models.playerstats.Player
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class Chart : Fragment() {

    private lateinit var dmgButton: Button
    private lateinit var winsButton: Button
    private lateinit var assistButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var serverSpinnerAdapter: ArrayAdapter<CharSequence>
    private lateinit var loggedUserNickname: String
    private lateinit var lineChart: LineChart
    private var statsMap = HashMap<String, DocumentSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        serverSpinnerAdapter = activity?.let { ArrayAdapter.createFromResource(it,
            R.array.regions,
            R.layout.spinner_list) } as ArrayAdapter<CharSequence>
        auth = Firebase.auth
        loggedUserNickname = auth.currentUser?.displayName.toString()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_chart, container, false)
        dmgButton = view.findViewById(R.id.dmgbutton)
        assistButton = view.findViewById(R.id.assistButton)
        winsButton = view.findViewById(R.id.winbutton)
        lineChart = view.findViewById(R.id.chart)
        val player: Player = arguments?.getSerializable("PlayerOverallStats") as Player
        val server:String = arguments?.getString("Server") as String
        dmgButton.setOnClickListener {
            setDataOnGraph("Damage")
        }
        winsButton.setOnClickListener {
            setDataOnGraph("Wins")
        }
        assistButton.setOnClickListener {
            setDataOnGraph("Assist")
        }

        val yAxis = YAxis()
        yAxis.setDrawLabels(false)
        setUpLineChart()

        gettingStats(player, server)

        return view
    }

    private fun setUpLineChart() {
        with(lineChart) {
            animateX(800, com.github.mikephil.charting.animation.Easing.EaseInSine)
            description.isEnabled = false

            xAxis.setDrawGridLines(false)
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F
            xAxis.textColor = resources.getColor(android.R.color.white)
            xAxis.textSize = 12f

            axisLeft.textColor = resources.getColor(android.R.color.white)
            axisLeft.textSize = 12f

            axisRight.isEnabled = false
            extraRightOffset = 10f

            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            legend.textSize = 14F
            legend.textColor = resources.getColor(android.R.color.white)
            legend.form = com.github.mikephil.charting.components.Legend.LegendForm.LINE
            setScaleMinima(0f,0f)
        }
    }

    private fun getStatsFromFirebase(player: Player, server:String, counter:Int){
        val current = LocalDateTime.now().minusDays(counter.toLong())
        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
        val formatted = current.format(formatter)
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("playersstats")
            .document(formatted).collection(server)
            .document(player.account_id.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val statsFromDate = task.result
                    if (statsFromDate.data != null) {
                        statsMap.put(formatted,statsFromDate)

                    }
                    setDataOnGraph("Damage")
                }

            }
    }

    private fun setDataOnGraph(option:String) {
        if(statsMap.size==7){
            val data = ArrayList<Entry>()
            when(option){
                "Damage" -> {
                    for (i in 1..7) {
                        val current = LocalDateTime.now().minusDays(i.toLong())
                        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
                        val formatted = current.format(formatter)
                        val stat = statsMap[formatted]
                        val battles = stat?.get("battles")
                        val avgDMG = (stat?.get("damage_dealt")
                            .toString().toInt()).toDouble()/battles.toString().toDouble()
                        data.add(Entry(battles.toString().toFloat(),avgDMG.toFloat()))
                    }
                }
                "Wins" -> {
                    for (i in 1..7) {
                        val current = LocalDateTime.now().minusDays(i.toLong())
                        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
                        val formatted = current.format(formatter)
                        val stat = statsMap[formatted]
                        val battles = stat?.get("battles")
                        val avgWins = BigDecimal(((stat?.get("wins").toString().toDouble()/battles.toString().toDouble())*100.0)).setScale(2,
                            RoundingMode.HALF_EVEN)
                        data.add(Entry(battles.toString().toFloat(),avgWins.toFloat()))
                    }
                }
                "Assist" -> {
                    for (i in 1..7) {
                        val current = LocalDateTime.now().minusDays(i.toLong())
                        val formatter = DateTimeFormatter.ofPattern("EE MMM dd yyyy")
                        val formatted = current.format(formatter)
                        val stat = statsMap[formatted]
                        val battles = stat?.get("battles")
                        val avgDMG = stat?.get("avg_damage_assisted")
                            .toString().toDouble()
                        data.add(Entry(battles.toString().toFloat(),avgDMG.toFloat()))
                    }
                }
            }

            Collections.sort(data, EntryXComparator())
            val lineDataSetStats = LineDataSet(data, option)
            lineDataSetStats.lineWidth = 5f
            lineDataSetStats.mode = LineDataSet.Mode.LINEAR
            lineDataSetStats.color = resources.getColor(android.R.color.white)
            lineDataSetStats.setDrawHorizontalHighlightIndicator(false)
            lineDataSetStats.setDrawVerticalHighlightIndicator(false)
            lineDataSetStats.setDrawValues(false)
            lineDataSetStats.setDrawCircles(true)
            lineDataSetStats.circleRadius = 7f
            lineDataSetStats.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSetStats.setCircleColor(android.R.color.black)
            lineChart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return Math.floor(value.toDouble()).toInt().toString()
                }
            }

            lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val decimalFormat = DecimalFormat("0.00")
                    return decimalFormat.format(value.toDouble())
                }
            }


            val dataSet = ArrayList<ILineDataSet>()
            dataSet.add(lineDataSetStats)


            val lineData = LineData(dataSet)
            lineChart.data = lineData
            lineChart.invalidate()
        }
    }

    private fun gettingStats(player: Player, server:String){
        for(i in 1..7){
            getStatsFromFirebase(player,server, i)
        }
    }


}