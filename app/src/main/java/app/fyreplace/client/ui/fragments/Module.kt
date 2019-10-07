package app.fyreplace.client.ui.fragments

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.fyreplace.client.app.NavigationMainDirections
import app.fyreplace.client.data.models.Author
import org.koin.dsl.module

val fragmentsModule = module {
    factory<LoginFragment.Navigator> { (fragment: LoginFragment) ->
        object : LoginFragment.Navigator {
            override fun navigateToHome() {
                fragment.findNavController()
                    .navigate(LoginFragmentDirections.actionFragmentLoginToFragmentHome())
            }
        }
    }

    factory<NotificationsFragment.Navigator> { (fragment: NotificationsFragment) ->
        object : NotificationsFragment.Navigator {
            override fun navigateToPost(
                areaName: String,
                postId: Long,
                newCommentsIds: List<Long>
            ) {
                fragment.findNavController().navigate(
                    NavigationMainDirections.actionGlobalFragmentPost(
                        areaName = areaName,
                        postId = postId,
                        newCommentsIds = newCommentsIds.toLongArray()
                    )
                )
            }
        }
    }

    factory<PostFragment.Navigator> { (fragment: PostFragment) ->
        object : PostFragment.Navigator {
            override fun navigateToUser(author: Author) {
                fragment.findNavController()
                    .navigate(NavigationMainDirections.actionGlobalFragmentUser(author = author))
            }
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
