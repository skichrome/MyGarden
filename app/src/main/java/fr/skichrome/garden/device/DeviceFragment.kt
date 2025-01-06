package fr.skichrome.garden.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import fr.skichrome.garden.MyGardenTheme
import fr.skichrome.garden.R
import fr.skichrome.garden.util.AppEventObserver
import fr.skichrome.garden.util.findToolbar
import fr.skichrome.garden.util.showSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceFragment : Fragment()
{
    // ===================================
    //               Fields
    // ===================================

    private val deviceViewModel: DeviceViewModel by viewModel()

    private var deviceEditedId: Long? = null

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyGardenTheme {
                    DeviceFragmentView()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        configureUI()
        configureValidateBtn()
    }

    override fun onDestroyView()
    {
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
    }

    private fun configureUI()
    {
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
//        binding.fragmentDeviceValidateBtn.setOnClickListener {
//            if (deviceEditedId != -1L && validateInputFields())
//                saveChanges()
//        }
    }

    // --- Input validation --- //

    private fun validateInputFields(): Boolean
    {
        // Device
//        val areDeviceValid = fragmentDeviceUniqueIdLayout.setErrorIfNoText()
//            .and(fragmentDeviceNameLayout.setErrorIfNoText())
//
//        // DeviceConfiguration
//        val areDeviceConfValid = fragmentDeviceSprinkleHourLayout.setErrorIfNoTextAndNotNumber()
//            .and(fragmentDeviceSprinkleMinuteLayout.setErrorIfNoTextAndNotNumber())
//            .and(fragmentDeviceSprinkleDurationLayout.setErrorIfNoTextAndNotNumber())
//
//        return areDeviceValid.and(areDeviceConfValid)
        return false
    }

    private fun saveChanges()
    {
//        Toast.makeText(context, "[DEV] Fields valid - (${deviceEditedId?.let { "Edition" } ?: "Creation"})", Toast.LENGTH_SHORT).show()

//        val device = Device(
//            id = deviceEditedId ?: 0L,
//            deviceId = binding.fragmentDeviceUniqueIdText.text.toString(),
//            name = binding.fragmentDeviceNameText.text.toString(),
//            description = binding.fragmentDeviceDescriptionText.text?.toString()
//        )
//
//        val deviceConfiguration = DeviceConfiguration(
//            id = deviceEditedId ?: 0L,
//            startTimeHour = binding.fragmentDeviceSprinkleHourText.text.toString().toInt(),
//            startTimeMin = binding.fragmentDeviceSprinkleMinuteText.text.toString().toInt(),
//            duration = binding.fragmentDeviceSprinkleDurationText.text.toString().toInt()
//        )

//        if (deviceEditedId != null)
//            deviceViewModel.updateNewDeviceAndConfiguration(device, deviceConfiguration)
//        else
//            deviceViewModel.createNewDeviceAndConfiguration(device, deviceConfiguration)

//        deviceEditedId = -1L
    }
}