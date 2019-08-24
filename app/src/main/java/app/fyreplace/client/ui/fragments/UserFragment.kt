package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import app.fyreplace.client.AppGlide
import app.fyreplace.client.Constants
import app.fyreplace.client.R
import app.fyreplace.client.ui.lazyMarkdown
import app.fyreplace.client.viewmodels.UserFragmentViewModel
import app.fyreplace.client.viewmodels.getShareIntent
import app.fyreplace.client.viewmodels.lazyViewModel
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : FailureHandlingFragment(R.layout.fragment_user) {
    override val viewModels: List<ViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<UserFragmentViewModel>()
    private val fragmentArgs by navArgs<UserFragmentArgs>()
    private val markdown by lazyMarkdown()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            fragmentArgs.author != null -> viewModel.setAuthor(fragmentArgs.author!!)
            fragmentArgs.userId != -1L -> launch { viewModel.setUserId(fragmentArgs.userId) }
            else -> throw IllegalStateException("Cannot start UserFragment without a user to show")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_sharing, menu)
        viewModel.author.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_share).intent = getShareIntent(
                Constants.Api.userShareUrl(it.user),
                getString(R.string.user_action_share_title)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.author.observe(viewLifecycleOwner) {
            user_name.text = it.name

            it.bio?.run {
                markdown.setMarkdown(user_bio, this)
                user_bio.isVisible = isNotBlank()
            }

            AppGlide.with(view)
                .load(it.avatar ?: R.drawable.default_avatar)
                .placeholder(android.R.color.transparent)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelOffset(R.dimen.dialog_user_picture_rounding))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(user_picture)
        }
    }
}
