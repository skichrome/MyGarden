package fr.skichrome.garden.main

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.ActivityMainBinding
import fr.skichrome.garden.util.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main)
{
    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onBindingReady()
    {
        val navController = getNavController()
        configureToolbar(navController)
    }

    // ===================================
    //               Methods
    // ===================================

    private fun getNavController(): NavController =
        (supportFragmentManager.findFragmentById(R.id.activityMainNavHostFragment) as NavHostFragment).navController

    private fun configureToolbar(navController: NavController) = with(binding) {
        activityMainToolbar.setupWithNavController(navController)
    }
}