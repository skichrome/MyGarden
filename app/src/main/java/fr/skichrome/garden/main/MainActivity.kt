package fr.skichrome.garden.main

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.ActivityMainBinding
import fr.skichrome.garden.service.DataSyncWorker
import fr.skichrome.garden.util.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main)
{
    // ===================================
    //               Fields
    // ===================================

    companion object
    {
        private const val WM_DATA_SYNC_ID = "work_manager_data_sync_identifier"
    }

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onBindingReady()
    {
        configureDataSync()
        val navController = getNavController()
        configureToolbar(navController)
    }

    // ===================================
    //               Methods
    // ===================================

    private fun configureDataSync()
    {
        WorkManager.getInstance(this).apply {
            val workRequest = OneTimeWorkRequestBuilder<DataSyncWorker>().build()
            enqueueUniqueWork(WM_DATA_SYNC_ID, ExistingWorkPolicy.KEEP, workRequest)
            getWorkInfoByIdLiveData(workRequest.id).observe(this@MainActivity) {
                it?.let { workInfo ->
                    val progress = workInfo.progress.getInt(DataSyncWorker.WM_PROGRESS_ID, 0)
                    binding.activityMainProgressBar.setProgressCompat(progress, true)
                }
            }
        }
    }

    private fun getNavController(): NavController =
        (supportFragmentManager.findFragmentById(R.id.activityMainNavHostFragment) as NavHostFragment).navController

    private fun configureToolbar(navController: NavController) = with(binding) {
        activityMainToolbar.setupWithNavController(navController)
    }
}