package fr.skichrome.garden.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import fr.skichrome.garden.BuildConfig
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.FragmentHomeBinding
import fr.skichrome.garden.model.DeviceFilter
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceData
import fr.skichrome.garden.util.AppEventObserver
import fr.skichrome.garden.util.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home)
{
    // ===================================
    //               Fields
    // ===================================

    private val homeViewModel: HomeViewModel by viewModel()
    private var spinnerAdapter: HomeSpinnerAdapter? = null

    private var selectedDevice: Device? = null
    private var filterStartDate: Long? = null
    private var filterEndDate: Long? = null

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onBindingReady()
    {
        configureViewModel()
        configureDatePickers()
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
            {
                updateTemperatureChart(it)
                updatePressureChart(it)
            }
        }
    }

    private fun configureDatePickers()
    {
        val now = Calendar.getInstance(Locale.getDefault())
        binding.fragmentHomeFilterStartDateText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance(Locale.getDefault())
                calendar.set(year, month, dayOfMonth, 0, 0, 0)
                filterStartDate = calendar.timeInMillis

                updateDeviceAndFilters()

                val dateStr = "${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}/${if (month < 10) "0$month" else month}/$year"
                binding.fragmentHomeFilterStartDateText.setText(dateStr)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.maxDate = filterEndDate ?: now.timeInMillis
            dpd.show()
        }
        binding.fragmentHomeFilterEndDateText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance(Locale.getDefault())
                calendar.set(year, month, dayOfMonth, 23, 59, 59)
                filterEndDate = calendar.timeInMillis

                // Prevent end date under start date if inferior end date is entered in second
                if (filterStartDate ?: 0 > filterEndDate ?: 0)
                {
                    binding.fragmentHomeFilterStartDateText.setText("")
                    filterStartDate = null
                }

                updateDeviceAndFilters()

                val dateStr = "${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}/${if (month < 10) "0$month" else month}/$year"
                binding.fragmentHomeFilterEndDateText.setText(dateStr)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.maxDate = now.timeInMillis
            dpd.show()
        }

        binding.fragmentHomeBtnClearFilter.setOnClickListener {
            binding.fragmentHomeFilterStartDateText.setText("")
            binding.fragmentHomeFilterEndDateText.setText("")
            filterEndDate = null
            filterStartDate = null
            updateDeviceAndFilters()
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
                selectedDevice = itemsWithNullEntry[position].first
                updateDeviceAndFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                Timber.i("[fragmentHomeSpinnerDevices] - OnNothingSelected called")
            }
        }
    }

    private fun updateDeviceAndFilters()
    {
        val deviceFilter = DeviceFilter(
            startDate = filterStartDate?.let { it / 1000 },
            endDate = filterEndDate?.let { it / 1000 }
        )
        homeViewModel.setCurrentDevice(selectedDevice, deviceFilter)
            .also { Timber.d("Loading device [${selectedDevice?.id}]") }
    }

    // --- Charts --- //

    private fun updateTemperatureChart(deviceDataList: List<DeviceData>) = with(binding.fragmentHomeTemperatureChart) {
        // --- Chart Entries definition --- //
        val temperatureEntries = deviceDataList.map {
            Entry(it.timestamp.toFloat(), it.temperature.toFloat())
        }

        // --- Chart Lines definition --- //
        val temperatureLineDataSet = LineDataSet(temperatureEntries, "Temperature").apply {
            color = Color.CYAN
            setDrawCircles(false)
        }

        // --- Chart configuration --- //
        val lineData = LineData(temperatureLineDataSet)
        formatAxisToDate(xAxis)
        data = lineData
        invalidate()
    }

    private fun updatePressureChart(deviceDataList: List<DeviceData>) = with(binding.fragmentHomePressureChart) {
        // --- Chart Entries definition --- //
        val barometricEntries = deviceDataList.map {
            Entry(it.timestamp.toFloat(), it.barometric.toFloat())
        }

        val altitudeEntries = deviceDataList.map {
            Entry(it.timestamp.toFloat(), it.altitude.toFloat())
        }

        // --- Chart Lines definition --- //
        val barometricLineDataSet = LineDataSet(barometricEntries, "Barometric Pressure").apply {
            color = Color.RED
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawCircles(false)
        }

        val altitudeLineDataSet = LineDataSet(altitudeEntries, "Altitude").apply {
            color = Color.GREEN
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawCircles(false)
        }

        // --- Chart configuration --- //
        if (BuildConfig.DEBUG)
            isLogEnabled = true

        val lineData = LineData(barometricLineDataSet, altitudeLineDataSet)
        formatAxisToDate(xAxis)
        data = lineData
        invalidate()
    }

    private fun formatAxisToDate(axis: AxisBase)
    {
        val calendar = Calendar.getInstance()

        val valueFormatter = object : ValueFormatter()
        {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String
            {
                calendar.timeInMillis = (value * 1000).toLong()
                val hours = calendar.get(Calendar.HOUR_OF_DAY).let { h ->
                    if (h < 10) "0$h" else "$h"
                }
                val minutes = calendar.get(Calendar.MINUTE).let { m ->
                    if (m < 10) "0$m" else "$m"
                }

                val formatted = "${hours}h$minutes"
                Timber.e("Value: ${value.toLong()} / result: $formatted")
                return formatted
            }
        }
        axis.granularity = 1f
        axis.valueFormatter = valueFormatter
    }
}