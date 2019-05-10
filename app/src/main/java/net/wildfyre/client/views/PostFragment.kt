package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_post.*
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostFragmentViewModel
import net.wildfyre.client.views.markdown.PostPlugin
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin
import ru.noties.markwon.recycler.MarkwonAdapter
import ru.noties.markwon.recycler.table.TableEntryPlugin

class PostFragment : FailureHandlingFragment(R.layout.fragment_post) {
    private lateinit var viewModel: PostFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel)
    private val args by navArgs<PostFragmentArgs>()
    private lateinit var markdown: Markwon

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(PostFragmentViewModel::class.java)
        markdown = Markwon.builder(context)
            .usePlugin(CorePlugin.create())
            .usePlugin(PostPlugin.create(context))
            .usePlugin(ImagesPlugin.create(context))
            .usePlugin(OkHttpImagesPlugin.create())
            .usePlugin(TableEntryPlugin.create(context))
            .build()

        viewModel.setPostId(args.postId)
        viewModel.markdownContent.observe(this, Observer {
            val adapter = content.adapter as MarkwonAdapter
            adapter.setMarkdown(markdown, it)
            adapter.notifyItemRangeInserted(0, adapter.itemCount)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
            findViewById<RecyclerView>(R.id.content).adapter = MarkwonAdapter.createTextViewIsRoot(R.layout.post_entry)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_post_actions, menu)
    }
}