package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged

class ProfileFragmentViewModel : ViewModel() {
    private val mDirty = MutableLiveData(false)

    val dirty: LiveData<Boolean> = mDirty.distinctUntilChanged()

    fun setIsDirty(isDirty: Boolean) = mDirty.postValue(isDirty)
}
