package app.fyreplace.client.viewmodels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> FragmentActivity.lazyViewModel() =
    lazy { ViewModelProvider(this).get(VM::class.java) }

inline fun <reified VM : ViewModel> Fragment.lazyViewModel() =
    lazy { ViewModelProvider(this).get(VM::class.java) }

inline fun <reified VM : ViewModel> Fragment.lazyActivityViewModel() =
    lazy { ViewModelProvider(requireActivity()).get(VM::class.java) }
