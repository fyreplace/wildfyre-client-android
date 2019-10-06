package app.fyreplace.client.ui.fragments

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.koin.dsl.bind
import org.koin.dsl.module

val fragmentArgsModule = module {
    factory { parameters ->
        val args = parameters.get<Fragment>(0).navArgs<PostFragmentArgs>().value
        object : PostFragment.Args {
            override val post = args.post
            override val areaName = args.areaName
            override val postId = args.postId
            override val ownPost = args.ownPost
            override val newCommentsIds = args.newCommentsIds?.toList()
                ?: args.selectedCommentId.let { if (it != -1L) listOf(it) else null }
        }
    } bind PostFragment.Args::class

    factory { parameters ->
        val args = parameters.get<Fragment>(0).navArgs<UserFragmentArgs>().value
        object : UserFragment.Args {
            override val author = args.author
            override val userId = args.userId
        }
    } bind UserFragment.Args::class
}
