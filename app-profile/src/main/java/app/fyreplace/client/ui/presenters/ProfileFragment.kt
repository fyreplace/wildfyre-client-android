package app.fyreplace.client.ui.presenters

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import app.fyreplace.client.AppGlide
import app.fyreplace.client.app.profile.R
import app.fyreplace.client.app.profile.databinding.FragmentProfileBinding
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.ui.*
import app.fyreplace.client.viewmodels.CentralViewModel
import app.fyreplace.client.viewmodels.ProfileFragmentViewModel
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(R.layout.fragment_profile), Presenter, ImageSelector {
    override val viewModel by viewModel<ProfileFragmentViewModel>()
    override lateinit var bd: FragmentProfileBinding
    override val maxImageSize = 0.5f
    private val centralViewModel by sharedViewModel<CentralViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProfileBinding.inflate(inflater, container, false).run {
        lifecycleOwner = viewLifecycleOwner
        bd = this
        return@run root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        centralViewModel.selfUsername.observe(viewLifecycleOwner) { bd.userName.text = it }
        centralViewModel.selfBio.observe(viewLifecycleOwner) { bd.userBio.setText(it) }

        val transformations = MultiTransformation(
            CenterCrop(),
            RoundedCorners(resources.getDimensionPixelSize(R.dimen.user_picture_rounding))
        )
        val transition = DrawableTransitionOptions.withCrossFade()

        centralViewModel.self.observe(viewLifecycleOwner) {
            AppGlide.with(view)
                .loadAvatar(requireContext(), it)
                .transform(transformations)
                .transition(transition)
                .into(bd.userPicture)
        }
        centralViewModel.newUserAvatar.observe(viewLifecycleOwner) {
            it?.bytes?.let { bytes ->
                AppGlide.with(view)
                    .load(bytes)
                    .transform(transformations)
                    .transition(transition)
                    .into(bd.userPicture)
            }
        }

        bd.editUserPicture.setOnClickListener {
            showImageChooser(R.string.profile_editor_picture_dialog_title, false)
        }

        bd.userBio.addTextChangedListener {
            if (it.toString() != centralViewModel.selfBio.value) {
                viewModel.setIsDirty(true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_profile, menu)
        inflater.inflate(R.menu.actions_fragment_sharing, menu)

        val save = menu.findItem(R.id.action_save).actionView.findViewById<View>(R.id.button)

        save.setOnClickListener {
            launch {
                centralViewModel.sendProfile(bd.userBio.text.toString())
                viewModel.setIsDirty(false)
            }
        }

        viewModel.dirty.observe(viewLifecycleOwner) { save.isEnabled = it }

        centralViewModel.self.observe(viewLifecycleOwner) {
            if (it != null) with(menu.findItem(R.id.action_share)) {
                isVisible = !it.banned
                intent = getShareIntent(
                    userShareUrl(it.user),
                    getString(R.string.profile_action_share_title)
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<Fragment>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override suspend fun onImage(image: ImageData) {
        AppGlide.with(requiredContext)
            .load(image.bytes)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.user_picture_rounding))
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(bd.userPicture)

        viewModel.setIsDirty(true)
        centralViewModel.setPendingProfileAvatar(image)
    }

    override suspend fun onImageRemoved() = Unit
}
