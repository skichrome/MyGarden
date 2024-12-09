package fr.skichrome.garden.device

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.skichrome.garden.MyGardenTheme
import fr.skichrome.garden.R
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceConfiguration

@Composable
fun DeviceFragmentView(deviceViewModel: DeviceViewModel = viewModel())
{
    val devices by deviceViewModel.devices.observeAsState()
    val device by deviceViewModel.currentDevice.observeAsState()
    val deviceConf by deviceViewModel.currentDeviceConfiguration.observeAsState()
    DeviceFragmentRoot(devices, device, deviceConf) { deviceViewModel.setCurrentDevice(it) }
}

@Composable
private fun DeviceFragmentRoot(devices: List<Device>?, device: Device?, deviceConf: DeviceConfiguration?, onDeviceSelected: (device: Device?) -> Unit)
{
    Column {
        DeviceSpinner(devices = devices, onDeviceSelected = onDeviceSelected)
        Title(text = R.string.fragment_device_definition_title)
        DeviceTextField(labelRes = R.string.fragment_device_unique_id_hint, content = device?.deviceId)
        DeviceTextField(labelRes = R.string.fragment_device_name_hint, content = device?.name)
        DeviceTextField(labelRes = R.string.fragment_device_description_hint, content = device?.description)

        Title(text = R.string.fragment_device_configuration_title)
        SwitchDeviceSprinkleEnabled(deviceConf?.duration)
        DeviceSprinkleHM(deviceConf?.startTimeHour, deviceConf?.startTimeMin)
        DeviceTextField(labelRes = R.string.fragment_device_duration_hint, content = deviceConf?.duration?.toString())
        CreateOrUpdateBtn(device) { }
    }
}

@Composable
private fun DeviceSprinkleHM(durationHr: Int?, durationMin: Int?)
{
    val modifier = Modifier.padding(top = 16.dp)

    Row(
        modifier = getModifier(),
    ) {
        DeviceTextField(
            labelRes = R.string.fragment_device_hour_hint,
            content = durationHr?.toString(),
            modifier = modifier
                .weight(0.5f)
                .padding(end = 8.dp)
        )
        DeviceTextField(
            labelRes = R.string.fragment_device_minute_hint,
            content = durationMin?.toString(),
            modifier = modifier
                .weight(0.5f)
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun DeviceSpinner(devices: List<Device>?, onDeviceSelected: (device: Device?) -> Unit)
{
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val itemPosition = remember { mutableIntStateOf(0) }

    val itemsWithNullEntry = mutableListOf<Pair<Device?, String>>(Pair(null, stringResource(R.string.fragment_home_spinner_null_entry)))
    devices?.map { itemsWithNullEntry.add(Pair(it, it.name)) }

    Box(
        modifier = getModifier().clickable { isDropDownExpanded.value = true },
    ) {
        // Contain view that will be always visible
        Row {
            Text(
                text = itemsWithNullEntry[itemPosition.intValue].second,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                contentDescription = "Arrow drop down"
            )
        }

        // Contain dropdown menu that appear only when user click
        DropdownMenu(modifier = Modifier.fillMaxWidth(0.9f), expanded = isDropDownExpanded.value, onDismissRequest = {
            isDropDownExpanded.value = false
        }) {
            itemsWithNullEntry.forEachIndexed { i, device ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Image(
                                painter = painterResource(R.drawable.ic_baseline_memory_24),
                                contentDescription = "Integrated circuit image",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = device.second, modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }
                    },
                    onClick = {
                        isDropDownExpanded.value = false
                        itemPosition.intValue = i
                        onDeviceSelected(device.first)
                    })
            }
        }
    }
}

@Composable
private fun Title(@StringRes text: Int)
{
    Text(
        stringResource(text),
        style = MaterialTheme.typography.titleMedium,
        modifier = getModifier()
    )
}

@Composable
private fun DeviceTextField(@StringRes labelRes: Int, content: String?, suffix: String? = null, modifier: Modifier = getModifier())
{
    OutlinedTextField(
        value = content ?: "",
        enabled = true,
        label = { Text(stringResource(labelRes)) },
        onValueChange = {
            // Todo : Check error here
        },
        isError = true, // Todo : error here
        suffix = { suffix?.let { Text(text = it) } },
        modifier = modifier
    )
}

@Composable
private fun SwitchDeviceSprinkleEnabled(duration: Int?)
{
    Row(
        modifier = getModifier()
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.fragment_device_sprinkle_enabled_sw_hint),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = duration != 0,
            onCheckedChange = {
                // Todo : Change sprinkle duration to 0 if disabled ?
            }
        )
    }
}

@Composable
private fun CreateOrUpdateBtn(device: Device?, btnCallback: () -> Unit)
{
    val btnText = device?.id?.let {
        stringResource(R.string.fragment_device_btn_update_text)
    } ?: stringResource(R.string.fragment_device_btn_creation_text)

    Button(onClick = btnCallback, modifier = getModifier()) {
        Text(btnText)
    }
}

@Preview(widthDp = 411, heightDp = 846, showBackground = true)
@Composable
private fun DeviceFragmentPreview()
{
    val demoDevice = Device(id = -1, deviceId = "UniqueStringId", name = "Sigfox Device", description = "lorem ipsum dolor sit amet")
    val demoConfigDevice = DeviceConfiguration(id = -1, startTimeHour = 4, startTimeMin = 18, duration = 10)

    MyGardenTheme {
        DeviceFragmentRoot(listOf(demoDevice), demoDevice, demoConfigDevice) {}
    }
}

private fun getModifier(): Modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp)
    .padding(top = 16.dp)