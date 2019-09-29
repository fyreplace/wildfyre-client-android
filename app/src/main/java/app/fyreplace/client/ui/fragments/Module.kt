package app.fyreplace.client.ui.fragments

import androidx.navigation.fragment.navArgs
import org.koin.dsl.bind
import org.koin.dsl.module

val fragmentArgsModule = module {
    factory { parameters ->
        val args = parameters.get<UserFragment>(0).navArgs<UserFragmentArgs>().value
        object : UserFragment.Args {
            override val author = args.author
            override val userId = args.userId
        }
    } bind UserFragment.Args::class
}
