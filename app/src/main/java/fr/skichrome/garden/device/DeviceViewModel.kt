package fr.skichrome.garden.device

import androidx.annotation.StringRes
import androidx.lifecycle.*
import fr.skichrome.garden.R
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceConfiguration
import fr.skichrome.garden.util.AppEvent
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.launch
import timber.log.Timber

class DeviceViewModel(private val repository: DeviceRepository) : ViewModel()
{
    // ===================================
    //               Fields
    // ===================================

    private val _errorMsgRef = MutableLiveData<AppEvent<Int>>()
    val errorMsgRef: LiveData<AppEvent<Int>> = _errorMsgRef

    // --- Devices --- //

    private val _devices: LiveData<List<Device>> = repository.observeAllDevices().map {
        return@map if (it is AppResult.Success) it.data
        else emptyList()
    }
    val devices: LiveData<List<Device>> = _devices

    private val _currentDeviceId = MutableLiveData<Long?>()

    private val _currentDeviceAndConfiguration: LiveData<Pair<Device, DeviceConfiguration>?> = _currentDeviceId.switchMap { deviceId ->
        getDeviceAndConfiguration(deviceId)
    }
    val currentDevice: LiveData<Device?> = _currentDeviceAndConfiguration.map { it?.first }
    val currentDeviceConfiguration: LiveData<DeviceConfiguration?> = _currentDeviceAndConfiguration.map { it?.second }

    // ===================================
    //               Methods
    // ===================================

    private fun setError(@StringRes msgRef: Int)
    {
        _errorMsgRef.value = AppEvent(msgRef)
    }

    private fun getDeviceAndConfiguration(deviceId: Long?): MutableLiveData<Pair<Device, DeviceConfiguration>?>
    {
        val result = MutableLiveData<Pair<Device, DeviceConfiguration>?>()
        if (deviceId == null)
        {
            result.value = null
            return result
        }

        viewModelScope.launch {
            val device = repository.loadDevice(deviceId)
            val deviceConfiguration = repository.loadDeviceConfiguration(deviceId)

            if (device is AppResult.Error)
                setError(R.string.view_model_device_load_device_error).also { return@launch }
            if (deviceConfiguration is AppResult.Error)
                setError(R.string.view_model_device_load_device_conf_error).also { return@launch }

            if (device is AppResult.Success && deviceConfiguration is AppResult.Success)
                result.value = Pair(device.data, deviceConfiguration.data).also { Timber.e("Device: $device / Config: $deviceConfiguration") }
        }

        return result
    }

    fun setCurrentDevice(device: Device?)
    {
        _currentDeviceId.value = device?.id
    }
}