package fr.skichrome.garden.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes private var layout: Int) : Fragment()
{
    private var _binding: T? = null
    protected val binding: T
        get() = _binding ?: throw IllegalStateException("Data binding variable should only be accessed after onCreate call")

    abstract fun onBindingReady()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(inflater, layout, container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        _binding?.executePendingBindings()
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        onBindingReady()
    }
}