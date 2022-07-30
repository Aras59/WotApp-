package com.example.wotapp.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wotapp.R
import com.example.wotapp.interfaces.PlayersInterface
import com.example.wotapp.models.playerVehicleStats.VehicleStatsForView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.text.StringBuilder

class VehicleRecycleViewAdapter(private val vehicleList: ArrayList<VehicleStatsForView>,private val markOfMasteryBitmap: HashMap<String, Bitmap>)
    : RecyclerView.Adapter<VehicleRecycleViewAdapter.ViewHolder>(){

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tankNameText: TextView = itemView.findViewById(R.id.tankNameText)
        val tierText: TextView = itemView.findViewById(R.id.tierText)
        val typeText: TextView = itemView.findViewById(R.id.typeText)
        val battlesText: TextView = itemView.findViewById(R.id.battlesText)
        val winsText: TextView = itemView.findViewById(R.id.winsText)
        val damageText: TextView = itemView.findViewById(R.id.dmgText)
        val vehicleImage: ImageView = itemView.findViewById(R.id.vehicleImage)
        val masteryImage: ImageView = itemView.findViewById(R.id.masteryImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_view_design,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicleList[position]

        holder.tankNameText.text = vehicle.tank.name
        holder.tierText.text = StringBuilder(vehicle.tank.tier.toString()+ " Tier ")
        holder.typeText.text = StringBuilder(vehicle.tank.type.replaceFirstChar { it.uppercaseChar() })
        holder.battlesText.text = StringBuilder(" "+vehicle.statistic.random.battles.toString())
        holder.winsText.text = StringBuilder(" " + BigDecimal((vehicle.statistic.random
            .wins.toDouble() / vehicle.statistic.random.battles.toDouble())*100.0)
            .setScale(2, RoundingMode.HALF_EVEN).toString()+"%")
        holder.damageText.text = StringBuilder(" " + BigDecimal((vehicle.statistic.random
            .damage_dealt.toDouble()/vehicle.statistic.random.battles.toDouble()))
            .setScale(0, RoundingMode.HALF_EVEN).toString())
        holder.masteryImage.setImageBitmap(markOfMasteryBitmap["3mark"])
        when(vehicle.statistic.mark_of_mastery){
            4 -> holder.masteryImage.setImageBitmap(markOfMasteryBitmap["Acemark"])
            2 -> holder.masteryImage.setImageBitmap(markOfMasteryBitmap["2mark"])
            3 -> holder.masteryImage.setImageBitmap(markOfMasteryBitmap["1mark"])
        }

        val playersInterface = PlayersInterface.createEU()

        val getVehicleLogo = playersInterface.getVehicleLogo(vehicle.tank.images.big_icon)

        getVehicleLogo.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val buffer: ByteArray = response.body()!!.bytes()
                val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                holder.vehicleImage.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

        })
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    fun updateData( newPostsList : ArrayList<VehicleStatsForView>){
        vehicleList.clear()
        vehicleList.addAll(newPostsList)
        notifyDataSetChanged()
    }

}