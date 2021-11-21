package fr.skichrome.garden.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.SpinnerItemDevicesBinding
import fr.skichrome.garden.model.local.Device

class HomeSpinnerAdapter(context: Context, private val items: MutableList<Pair<Device?, String>>) :
    ArrayAdapter<Pair<Device?, String>>(context, R.layout.spinner_item_devices, items)
{
    // =================================
    //        Superclass Methods
    // =================================

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        return getCustomView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        return getCustomView(position, parent)
    }

    // =================================
    //              Methods
    // =================================

    private fun getCustomView(position: Int, parent: ViewGroup): View
    {
        val binding = SpinnerItemDevicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        with(binding) {
            spinnerItemDevicesIdText.text = items[position].first?.deviceId
            spinnerItemDevicesNameText.text = items[position].second
        }
        return binding.root
    }
}