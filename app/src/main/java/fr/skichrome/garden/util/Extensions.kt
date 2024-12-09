package fr.skichrome.garden.util

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import fr.skichrome.garden.R
import fr.skichrome.garden.main.MainActivity

fun Fragment.findToolbar(): Toolbar? = when (activity)
{
    is MainActivity -> requireActivity().findViewById(R.id.activityMainToolbar)
    else -> null
}

fun Fragment.showSnackBar(@StringRes msgRef: Int) = Snackbar.make(requireView(), msgRef, Snackbar.LENGTH_LONG).show()

fun TextInputLayout.setErrorIfNoText(): Boolean
{
    val isEmpty = editText?.text.isNullOrEmpty()
    error = if (isEmpty) context.getString(R.string.extensions_tv_error_no_text) else null
    return !isEmpty
}

fun TextInputLayout.setErrorIfNoTextAndNotNumber(): Boolean
{
    val isEmpty = editText?.text.isNullOrEmpty()
    error = if (isEmpty) context.getString(R.string.extensions_tv_error_no_text) else null

    if (isEmpty)
        return false

    val isNaN = editText?.text.toString().toIntOrNull()
    error = if (isNaN == null) context.getString(R.string.extensions_tv_error_not_a_number) else null

    return isEmpty.not().and(isNaN != null)
}