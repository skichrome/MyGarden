package fr.skichrome.garden.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DataSyncWorkManager(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams)
{
    // ===================================
    //               Fields
    // ===================================

    // ===================================
    //         Superclass Methods
    // ===================================

    override suspend fun doWork(): Result
    {
//        withContext(Dispat)
        return Result.success()
    }

    // ===================================
    //               Methods
    // ===================================
}