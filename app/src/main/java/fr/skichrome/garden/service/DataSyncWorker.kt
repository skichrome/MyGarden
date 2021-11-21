package fr.skichrome.garden.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class DataSyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams), KoinComponent
{
    // ===================================
    //               Fields
    // ===================================

    private val dataSyncRepository: DataSyncRepository by inject()

    companion object
    {
        const val WM_PROGRESS_ID = "progress_id"

        private const val PROGRESS_START = 0
        private const val PROGRESS_DEVICES_OK = 33
        private const val PROGRESS_CONF_OK = 66
        private const val PROGRESS_END = 100
    }

    // ===================================
    //         Superclass Methods
    // ===================================

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        updateProgress(PROGRESS_START)
        return@withContext synchronizeDevices()
    }

    // ===================================
    //               Methods
    // ===================================

    private suspend fun updateProgress(newProgress: Int)
    {
        val progress = workDataOf(WM_PROGRESS_ID to newProgress)
        setProgress(progress)
    }

    private suspend fun synchronizeDevices(): Result = withContext(Dispatchers.IO) {
        return@withContext when (val result = dataSyncRepository.synchronizeDevices())
        {
            is AppResult.Success ->
            {
                updateProgress(PROGRESS_DEVICES_OK)
                val syncResult = listOf(
                    synchronizeDevicesConfiguration(result.data),
                    synchronizeDevicesData(result.data)
                )

                if (syncResult.any { it is Result.Failure }) Result.failure()
                else Result.success()
            }
            else -> Result.failure().also { Timber.e("An error occurred when sync devices") }
        }
    }

    private suspend fun synchronizeDevicesConfiguration(devicesId: List<Long>): Result = withContext(Dispatchers.IO) {
        val asyncCalls = devicesId.map { id ->
            async {
                when (dataSyncRepository.synchronizeDeviceConfiguration(id))
                {
                    is AppResult.Success -> Result.success()
                    else -> Result.failure().also { Timber.e("An error occurred when sync device conf") }
                }
            }
        }.awaitAll().also { updateProgress(PROGRESS_CONF_OK) }

        return@withContext if (asyncCalls.any { it is Result.Failure }) Result.failure()
        else Result.success()
    }

    private suspend fun synchronizeDevicesData(devicesId: List<Long>): Result = withContext(Dispatchers.IO) {
        val asyncCalls = devicesId.map { id ->
            async {
                when (dataSyncRepository.synchronizeDeviceData(id))
                {
                    is AppResult.Success -> Result.success()
                    else -> Result.failure().also { Timber.e("An error occurred when sync device data") }
                }
            }
        }.awaitAll().also { updateProgress(PROGRESS_END) }

        return@withContext if (asyncCalls.any { it is Result.Failure }) Result.failure()
        else Result.success()
    }
}