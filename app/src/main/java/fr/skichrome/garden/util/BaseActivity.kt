package fr.skichrome.garden.util

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes private var layout: Int) : AppCompatActivity()
{
    private var _binding: T? = null
    protected val binding: T
        get() = _binding ?: throw IllegalStateException("Data binding variable should only be accessed after onCreate call")

    abstract fun onBindingReady()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layout)
        _binding?.lifecycleOwner = this
        _binding?.executePendingBindings()
        onBindingReady()
    }
}