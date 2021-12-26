package fr.skichrome.garden.device

import android.view.View
import android.widget.AdapterView
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.FragmentDeviceBinding
import fr.skichrome.garden.home.HomeSpinnerAdapter
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.util.AppEventObserver
import fr.skichrome.garden.util.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DeviceFragment : BaseFragment<FragmentDeviceBinding>(R.layout.fragment_device)
{
    // ===================================
    //               Fields
    // ===================================

    private val deviceViewModel: DeviceViewModel by viewModel()

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
        deviceViewModel.errorMsgRef.observe(viewLifecycleOwner, AppEventObserver { showSnackBar(it) })
        deviceViewModel.devices.observe(viewLifecycleOwner) { updateSpinner(it) }
        deviceViewModel.currentDevice.observe(viewLifecycleOwner, { binding.device = it })
        deviceViewModel.currentDeviceConfiguration.observe(viewLifecycleOwner, { binding.deviceConfiguration = it })
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

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                Timber.i("[fragmentHomeSpinnerDevices] - OnNothingSelected called")
            }
        }
    }
}