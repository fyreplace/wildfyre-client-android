package app.fyreplace.client.ui.fragments

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.fyreplace.client.app.NavigationMainDirections.Companion.actionGlobalFragmentDraft
import app.fyreplace.client.app.NavigationMainDirections.Companion.actionGlobalFragmentPost
import app.fyreplace.client.app.NavigationMainDirections.Companion.actionGlobalFragmentUser
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.fragments.LoginFragmentDirections.Companion.actionFragmentLoginToFragmentHome
import org.koin.dsl.module

val fragmentsModule = module {
    factory<LoginFragment.Navigator> { (fragment: LoginFragment) ->
        object : LoginFragment.Navigator {
            override fun navigateToHome() = fragment.findNavController()
                .navigate(actionFragmentLoginToFragmentHome())
        }
    }

    factory<NotificationsFragment.Navigator> { (fragment: NotificationsFragment) ->
        object : NotificationsFragment.Navigator {
            override fun navigateToPost(
                areaName: String,
                postId: Long,
                newCommentsIds: List<Long>
            ) = fragment.findNavController().navigate(
                actionGlobalFragmentPost(
                    areaName = areaName,
                    postId = postId,
                    newCommentsIds = newCommentsIds.toLongArray()
                )
            )
        }
    }

    factory<ArchiveFragment.Navigator> { (fragment: ArchiveFragment) ->
        object : ArchiveFragment.Navigator {
            override fun navigateToPost(post: Post) = fragment.findNavController()
                .navigate(actionGlobalFragmentPost(post = post))
        }
    }

    factory<OwnPostsFragment.Navigator> { (fragment: OwnPostsFragment) ->
        object : OwnPostsFragment.Navigator {
            override fun navigateToPost(post: Post) = fragment.findNavController()
                .navigate(actionGlobalFragmentPost(post = post, ownPost = true))
        }
    }

    factory<DraftsFragment.Navigator> { (fragment: DraftsFragment) ->
        object : DraftsFragment.Navigator {
            override fun navigateToDraft(draft: Post) = fragment.findNavController()
                .navigate(actionGlobalFragmentDraft(draft = draft))
        }
    }

    factory<PostFragment.Navigator> { (fragment: PostFragment) ->
        object : PostFragment.Navigator {
            override fun navigateToUser(author: Author) = fragment.findNavController()
                .navigate(actionGlobalFragmentUser(author = author))
        }
    }

    factory<DraftFragment.Args> { (fragment: DraftFragment) ->
        val args = fragment.navArgs<DraftFragmentArgs>().value
        object : DraftFragment.Args {
            override val draft = args.draft
            override val showHint = args.showHint
        }
    }

    factory<PostFragment.Args> { (fragment: PostFragment) ->
        val args = fragment.navArgs<PostFragmentArgs>().value
        object : PostFragment.Args {
            override val post = args.post
            override val areaName = args.areaName
            override val postId = args.postId
            override val ownPost = args.ownPost
            override val newCommentsIds = args.newCommentsIds?.toList()
                ?: args.selectedCommentId.let { if (it != -1L) listOf(it) else null }
        }
    }

    factory<UserFragment.Args> { (fragment: UserFragment) ->
        val args = fragment.navArgs<UserFragmentArgs>().value
        object : UserFragment.Args {
            override val author = args.author
            override val userId = args.userId
        }
    }
}
