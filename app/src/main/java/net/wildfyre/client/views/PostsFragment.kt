package net.wildfyre.client.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import net.wildfyre.client.R

class PostsFragment : Fragment(R.layout.fragment_posts) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.setTitle(R.string.main_nav_posts)
    }
}