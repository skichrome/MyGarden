package fr.skichrome.garden.main

import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.ActivityMainBinding
import fr.skichrome.garden.util.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main)
{
    private val mainViewModel: MainViewModel by viewModel()

    override fun onBindingReady()
    {
    }
}