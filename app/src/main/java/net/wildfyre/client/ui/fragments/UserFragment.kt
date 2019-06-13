package net.wildfyre.client.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.fragment_user.*
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.ui.lazyMarkdown
import net.wildfyre.client.ui.ohNo
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.UserFragmentViewModel
import net.wildfyre.client.viewmodels.lazyViewModel

class UserFragment : FailureHandlingFragment(R.layout.fragment_user) {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<UserFragmentViewModel>()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_user_actions, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> ohNo(requireContext())
        }

        return super.onOptionsItemSelected(item)
    }
}
