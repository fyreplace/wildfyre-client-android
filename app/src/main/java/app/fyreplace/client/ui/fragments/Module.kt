package app.fyreplace.client.ui.fragments

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.fyreplace.client.app.NavigationMainDirections
import app.fyreplace.client.data.models.Author
import org.koin.dsl.module

val fragmentsModule = module {
    factory<NotificationsFragment.Navigator> { (fragment: Fragment) ->
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

    factory<PostFragment.Navigator> { (fragment: Fragment) ->
        object : PostFragment.Navigator {
            override fun navigateToUser(author: Author) {
                fragment.findNavController().navigate(
                    NavigationMainDirections.actionGlobalFragmentUser(author = author)
                )
            }
        }
    }

    factory<PostFragment.Args> { (fragment: Fragment) ->
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

    factory<UserFragment.Args> { (fragment: Fragment) ->
        val args = fragment.navArgs<UserFragmentArgs>().value
        object : UserFragment.Args {
            override val author = args.author
            override val userId = args.userId
        }
    }
}
