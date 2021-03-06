package me.cyber.nukleos.utils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.cyber.nukleos.ui.control.SensorModel
import me.cyber.nukleus.R

class DeviceAdapter(private val deviceSelectedListener: DeviceSelectedListener) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    var deviceList = mutableListOf<SensorModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DeviceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sensor_list_item, parent, false), deviceSelectedListener)
    override fun getItemCount() = deviceList.size
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) = holder.bind(deviceList[position])

    class DeviceViewHolder(val item: View, private val mDeviceSelectedListener: DeviceSelectedListener) : RecyclerView.ViewHolder(item) {
        fun bind(device: SensorModel) = with(item) {
            setOnClickListener { mDeviceSelectedListener.onDeviceSelected(it, adapterPosition) }
            findViewById<TextView>(R.id.name_tv).text = device.name
            findViewById<TextView>(R.id.address_tv).text = device.address
        }
    }
}

interface DeviceSelectedListener {
    fun onDeviceSelected(v: View, position: Int)
}
