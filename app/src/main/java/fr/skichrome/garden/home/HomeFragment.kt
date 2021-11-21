package fr.skichrome.garden.home

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.FragmentHomeBinding
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceData
import fr.skichrome.garden.util.AppEventObserver
import fr.skichrome.garden.util.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home)
{
    // ===================================
    //               Fields
    // ===================================

    private val homeViewModel: HomeViewModel by viewModel()
    private var spinnerAdapter: HomeSpinnerAdapter? = null

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onBindingReady()
    {
        configureViewModel()
    }

    override fun onDestroyView()
    {
        spinnerAdapter = null
        super.onDestroyView()
    }

    // ===================================
    //               Methods
    // ===================================

    private fun configureViewModel()
    {
        homeViewModel.errorMsgRef.observe(viewLifecycleOwner, AppEventObserver { showSnackBar(it) })
        homeViewModel.devices.observe(viewLifecycleOwner) { updateSpinner(it) }
        homeViewModel.devicesData.observe(viewLifecycleOwner) {
            if (it == null)
            {
                Timber.d("No device selected placeholder")
            } else
                updateTemperatureChart(it)
        }
    }

    private fun updateSpinner(devices: List<Device>)
    {
        val itemsWithNullEntry = mutableListOf<Pair<Device?, String>>(Pair(null, getString(R.string.fragment_home_spinner_null_entry)))
        devices.map { itemsWithNullEntry.add(Pair(it, it.name)) }

        Timber.w("Items: $itemsWithNullEntry")

        spinnerAdapter = HomeSpinnerAdapter(requireContext(), itemsWithNullEntry)
        binding.fragmentHomeSpinnerDevices.adapter = spinnerAdapter
        binding.fragmentHomeSpinnerDevices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                homeViewModel.setCurrentDevice(itemsWithNullEntry[position].first)
                    .also { Timber.d("Loading device [${itemsWithNullEntry[position].first?.id}] [${itemsWithNullEntry[position].second}]") }
            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                Timber.i("[fragmentHomeSpinnerDevices] - OnNothingSelected called")
            }
        }
    }

    // --- Charts --- //

    private fun updateTemperatureChart(deviceDataList: List<DeviceData>) = with(binding.fragmentHomeTemperatureChart) {
        val entries = deviceDataList.map { Entry(it.timestamp.toFloat(), it.temperature.toFloat()) }
        val lineDataSet = LineDataSet(entries, "Temperature")
        lineDataSet.color = Color.CYAN
        lineDataSet.valueTextColor = Color.RED
        val lineData = LineData(lineDataSet)
        data = lineData
        invalidate()
    }
}