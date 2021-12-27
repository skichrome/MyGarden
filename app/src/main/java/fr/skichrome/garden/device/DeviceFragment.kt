package fr.skichrome.garden.device

import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.FragmentDeviceBinding
import fr.skichrome.garden.home.HomeSpinnerAdapter
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceConfiguration
import fr.skichrome.garden.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DeviceFragment : BaseFragment<FragmentDeviceBinding>(R.layout.fragment_device)
{
    // ===================================
    //               Fields
    // ===================================

    private val deviceViewModel: DeviceViewModel by viewModel()

    private var spinnerAdapter: HomeSpinnerAdapter? = null
    private var deviceEditedId: Long? = null

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onBindingReady()
    {
        configureViewModel()
        configureUI()
        configureValidateBtn()
    }

    override fun onDestroyView()
    {
        spinnerAdapter = null
        findToolbar()?.menu?.findItem(R.id.fragment_device_new_device)?.apply {
            isVisible = false
            setOnMenuItemClickListener(null)
        }
        super.onDestroyView()
    }

    // ===================================
    //               Methods
    // ===================================

    private fun configureViewModel()
    {
        deviceViewModel.errorMsgRef.observe(viewLifecycleOwner, AppEventObserver { showSnackBar(it) })
        deviceViewModel.devices.observe(viewLifecycleOwner) { updateSpinner(it) }
        deviceViewModel.currentDevice.observe(viewLifecycleOwner, {
            deviceEditedId = it?.id
            @StringRes val btnText =
                if (deviceEditedId == null) R.string.fragment_device_btn_creation_text else R.string.fragment_device_btn_update_text
            binding.fragmentDeviceValidateBtn.setText(btnText)
            binding.device = it
        })
        deviceViewModel.currentDeviceConfiguration.observe(viewLifecycleOwner, { binding.deviceConfiguration = it })
    }

    private fun configureUI(): Unit = with(binding) {
        fragmentDeviceUniqueIdText.doAfterTextChanged { fragmentDeviceUniqueIdLayout.error = null }
        fragmentDeviceNameText.doAfterTextChanged { fragmentDeviceNameLayout.error = null }
        fragmentDeviceSprinkleHourText.doAfterTextChanged { fragmentDeviceSprinkleHourLayout.error = null }
        fragmentDeviceSprinkleMinuteText.doAfterTextChanged { fragmentDeviceSprinkleMinuteLayout.error = null }
        fragmentDeviceSprinkleDurationText.doAfterTextChanged { fragmentDeviceSprinkleDurationLayout.error = null }

        findToolbar()?.menu?.findItem(R.id.fragment_device_new_device)?.apply {
            isVisible = true
            setOnMenuItemClickListener {
                deviceViewModel.setCurrentDevice(null)
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun configureValidateBtn()
    {
        binding.fragmentDeviceValidateBtn.setOnClickListener {
            if (deviceEditedId != -1L && validateInputFields())
                saveChanges()
        }
    }

    private fun updateSpinner(devices: List<Device>)
    {
        val itemsWithNullEntry = mutableListOf<Pair<Device?, String>>(Pair(null, getString(R.string.fragment_home_spinner_null_entry)))
        devices.map { itemsWithNullEntry.add(Pair(it, it.name)) }

        Timber.w("Items: $itemsWithNullEntry")

        spinnerAdapter = HomeSpinnerAdapter(requireContext(), itemsWithNullEntry)
        binding.fragmentDeviceSpinnerDevices.adapter = spinnerAdapter
        binding.fragmentDeviceSpinnerDevices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                val selectedDevice = itemsWithNullEntry[position].first
                deviceViewModel.setCurrentDevice(selectedDevice)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) =
                Timber.i("[fragmentHomeSpinnerDevices] - OnNothingSelected called")
        }
    }

    // --- Input validation --- //

    private fun validateInputFields(): Boolean = with(binding) {
        // Device
        val areDeviceValid = fragmentDeviceUniqueIdLayout.setErrorIfNoText()
            .and(fragmentDeviceNameLayout.setErrorIfNoText())

        // DeviceConfiguration
        val areDeviceConfValid = fragmentDeviceSprinkleHourLayout.setErrorIfNoTextAndNotNumber()
            .and(fragmentDeviceSprinkleMinuteLayout.setErrorIfNoTextAndNotNumber())
            .and(fragmentDeviceSprinkleDurationLayout.setErrorIfNoTextAndNotNumber())

        return areDeviceValid.and(areDeviceConfValid)
    }

    private fun saveChanges()
    {
        Toast.makeText(context, "[DEV] Fields valid - (${deviceEditedId?.let { "Edition" } ?: "Creation"})", Toast.LENGTH_SHORT).show()

        val device = Device(
            id = deviceEditedId ?: 0L,
            deviceId = binding.fragmentDeviceUniqueIdText.text.toString(),
            name = binding.fragmentDeviceNameText.text.toString(),
            description = binding.fragmentDeviceDescriptionText.text?.toString()
        )

        val deviceConfiguration = DeviceConfiguration(
            id = deviceEditedId ?: 0L,
            startTimeHour = binding.fragmentDeviceSprinkleHourText.text.toString().toInt(),
            startTimeMin = binding.fragmentDeviceSprinkleMinuteText.text.toString().toInt(),
            duration = binding.fragmentDeviceSprinkleDurationText.text.toString().toInt()
        )

        if (deviceEditedId != null)
            deviceViewModel.updateNewDeviceAndConfiguration(device, deviceConfiguration)
        else
            deviceViewModel.createNewDeviceAndConfiguration(device, deviceConfiguration)

        deviceEditedId = -1L
    }
}