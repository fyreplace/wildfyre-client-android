package net.wildfyre.client.viewmodels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

inline fun <reified VM : ViewModel> FragmentActivity.lazyViewModel(): Lazy<VM> =
    lazy { ViewModelProviders.of(this).get(VM::class.java) }

inline fun <reified VM : ViewModel> Fragment.lazyViewModel(): Lazy<VM> =
    lazy { ViewModelProviders.of(this).get(VM::class.java) }

inline fun <reified VM : ViewModel> Fragment.lazyActivityViewModel(): Lazy<VM> =
    lazy { ViewModelProviders.of(requireActivity()).get(VM::class.java) }
