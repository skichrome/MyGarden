package fr.skichrome.garden.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.textfield.TextInputEditText
import fr.skichrome.garden.BuildConfig
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.FragmentHomeBinding
import fr.skichrome.garden.model.DeviceFilter
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceData
import fr.skichrome.garden.util.AppEventObserver
import fr.skichrome.garden.util.BaseFragment
import fr.skichrome.garden.util.findToolbar
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
        findToolbar()?.menu?.findItem(R.id.action_global_deviceFragment)?.isVisible = true
        configureViewModel()
        configureDatePickers()
    }

    override fun onDestroyView()
    {
        spinnerAdapter = null
        findToolbar()?.menu?.findItem(R.id.action_global_deviceFragment)?.isVisible = false
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
                updateSoilMoistureChart(it)
                updateLuminosityChart(it)
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
                binding.fragmentHomeFilterStartDateText.setTextDate(calendar)
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
                binding.fragmentHomeFilterEndDateText.setTextDate(calendar)
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

        val calendarForDefault = Calendar.getInstance(Locale.getDefault())
        binding.fragmentHomeFilterEndDateText.setTextDate(calendarForDefault)
        filterEndDate = calendarForDefault.timeInMillis
        calendarForDefault.add(Calendar.DAY_OF_MONTH, -7)
        binding.fragmentHomeFilterStartDateText.setTextDate(calendarForDefault)
        filterStartDate = calendarForDefault.timeInMillis
        updateDeviceAndFilters()
    }

    private fun TextInputEditText.setTextDate(calendar: Calendar)
    {
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dateStr = "${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}/${if (month < 9) "0${month + 1}" else month + 1}/$year"
        setText(dateStr)
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
            lineWidth = 2f
            setDrawCircles(false)
        }

        // --- Chart configuration --- //
        axisRight.isEnabled = false
        with(xAxis) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            formatAxisToDate()
        }
        val lineData = LineData(temperatureLineDataSet)
        description = null
        data = lineData
        updateChartTextColor()
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
            lineWidth = 2f
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawCircles(false)
        }

        val altitudeLineDataSet = LineDataSet(altitudeEntries, "Altitude").apply {
            color = Color.GREEN
            lineWidth = 2f
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawCircles(false)
        }

        // --- Chart configuration --- //
        if (BuildConfig.DEBUG)
            isLogEnabled = true

        with(xAxis) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            formatAxisToDate()
        }
        val lineData = LineData(barometricLineDataSet, altitudeLineDataSet)
        description = null
        data = lineData
        updateChartTextColor()
        invalidate()
    }

    private fun updateSoilMoistureChart(deviceDataList: List<DeviceData>) = with(binding.fragmentHomeSoilMoistureChart) {
        // --- Chart Entries definition --- //
        val soilMoistureEntries = deviceDataList.map {
            Entry(it.timestamp.toFloat(), it.soilMoisture.toFloat())
        }

        // --- Chart Lines definition --- //
        val soilMoistureLineDataSet = LineDataSet(soilMoistureEntries, "Soil Moisture").apply {
            color = Color.BLUE
            lineWidth = 2f
            setDrawCircles(false)
        }

        // --- Chart configuration --- //
        axisRight.isEnabled = false
        with(xAxis) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            formatAxisToDate()
        }
        val lineData = LineData(soilMoistureLineDataSet)
        description = null
        data = lineData
        updateChartTextColor()
        invalidate()
    }

    private fun updateLuminosityChart(deviceDataList: List<DeviceData>) = with(binding.fragmentHomeLuminosityChart) {
        // --- Chart Entries definition --- //
        val luminosityEntries = deviceDataList.map {
            Entry(it.timestamp.toFloat(), it.luminosity.toFloat())
        }

        // --- Chart Lines definition --- //
        val luminosityLineDataSet = LineDataSet(luminosityEntries, "Luminosity").apply {
            color = Color.YELLOW
            lineWidth = 2f
            setDrawCircles(false)
        }

        // --- Chart configuration --- //
        axisRight.isEnabled = false
        with(xAxis) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            formatAxisToDate()
        }
        val lineData = LineData(luminosityLineDataSet)
        description = null
        data = lineData
        updateChartTextColor()
        invalidate()
    }

    private fun AxisBase.formatAxisToDate()
    {
        val calendar = Calendar.getInstance()
        granularity = 1f
        valueFormatter = object : ValueFormatter()
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

                return "${hours}h$minutes"
            }
        }
    }

    private fun LineChart.updateChartTextColor()
    {
        val isNightMode = resources.getBoolean(R.bool.is_night_mode)
        val colorOnBackgroundChart = if (isNightMode) getColor(R.color.white) else getColor(R.color.black)

        xAxis?.textColor = colorOnBackgroundChart
        axisLeft?.textColor = colorOnBackgroundChart
        axisRight?.textColor = colorOnBackgroundChart
        legend?.textColor = colorOnBackgroundChart
    }

    private fun getColor(@ColorRes color: Int): Int = ResourcesCompat.getColor(resources, color, context?.theme)
}