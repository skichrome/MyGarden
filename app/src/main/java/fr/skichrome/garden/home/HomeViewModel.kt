package fr.skichrome.garden.home

import androidx.annotation.StringRes
import androidx.lifecycle.*
import fr.skichrome.garden.R
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceData
import fr.skichrome.garden.util.AppEvent
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel()
{
    // =================================
    //            Fields
    // =================================

    private val _errorMsgRef = MutableLiveData<AppEvent<Int>>()
    val errorMsgRef: LiveData<AppEvent<Int>> = _errorMsgRef

    // --- Devices --- //

    private val _devices: LiveData<List<Device>> = repository.observeAllDevices().map {
        return@map if (it is AppResult.Success) it.data
        else emptyList()
    }
    val devices: LiveData<List<Device>> = _devices

    private val _currentDevice = MutableLiveData<Device?>()

    // --- Devices data --- //

    private val _devicesData: LiveData<List<DeviceData>?> = _currentDevice.switchMap { device ->
        if (device == null)
            return@switchMap MutableLiveData(null)

        return@switchMap loadDeviceData(deviceId = device.id)
    }
    val devicesData: LiveData<List<DeviceData>?> = _devicesData

    // =================================
    //              Methods
    // =================================

    private fun setError(@StringRes msgRef: Int)
    {
        _errorMsgRef.value = AppEvent(msgRef)
    }

    private fun loadDeviceData(deviceId: Long): MutableLiveData<List<DeviceData>>
    {
        val resultLiveData: MutableLiveData<List<DeviceData>> = MutableLiveData(emptyList())

        viewModelScope.launch {
            when (val result = repository.getDeviceData(deviceId))
            {
                is AppResult.Success -> resultLiveData.value = result.data
                else -> setError(R.string.view_model_home_load_device_data_error)
            }
        }
        return resultLiveData
    }

    fun setCurrentDevice(device: Device?)
    {
        _currentDevice.value = device
    }
}