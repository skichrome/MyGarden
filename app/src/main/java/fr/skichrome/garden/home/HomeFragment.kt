package fr.skichrome.garden.home

import fr.skichrome.garden.R
import fr.skichrome.garden.databinding.FragmentHomeBinding
import fr.skichrome.garden.util.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home)
{
    // ===================================
    //               Fields
    // ===================================

    private val homeViewModel: HomeViewModel by viewModel()

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onBindingReady()
    {
        configureViewModel()
    }

    // ===================================
    //               Methods
    // ===================================

    private fun configureViewModel()
    {
    }
}