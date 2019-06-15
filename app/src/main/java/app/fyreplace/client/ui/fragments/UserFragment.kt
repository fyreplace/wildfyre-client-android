package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import app.fyreplace.client.AppGlide
import app.fyreplace.client.Constants
import app.fyreplace.client.R
import app.fyreplace.client.ui.lazyMarkdown
import app.fyreplace.client.viewmodels.FailureHandlingViewModel
import app.fyreplace.client.viewmodels.UserFragmentViewModel
import app.fyreplace.client.viewmodels.lazyViewModel
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : SharingFragment(R.layout.fragment_user) {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<UserFragmentViewModel>()
    override var menuShareContent = ""
    override val menuShareTitle by lazy { getString(R.string.user_share_title) }
    private val fragmentArgs by navArgs<UserFragmentArgs>()
    private val markdown by lazyMarkdown()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            fragmentArgs.author != null -> viewModel.setAuthor(fragmentArgs.author!!)
            fragmentArgs.userId != -1L -> viewModel.setUserIdAsync(fragmentArgs.userId)
            else -> throw IllegalStateException("Cannot start UserFragment without a user to show")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.author.observe(viewLifecycleOwner, Observer {
            menuShareContent = Constants.Api.userShareUrl(it.user)
            user_name.text = it.name

            it.bio?.run {
                markdown.setMarkdown(user_bio, this)
                user_bio.isVisible = isNotBlank()
            }

            AppGlide.with(view)
                .load(it.avatar ?: R.drawable.ic_launcher)
                .placeholder(android.R.color.transparent)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelOffset(R.dimen.dialog_user_picture_rounding))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(user_picture)
        })
    }
}
