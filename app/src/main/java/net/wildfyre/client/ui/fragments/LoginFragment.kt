package net.wildfyre.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import net.wildfyre.client.R
import net.wildfyre.client.databinding.FragmentLoginBinding
import net.wildfyre.client.ui.hideSoftKeyboard
import net.wildfyre.client.viewmodels.*

/**
 * [androidx.fragment.app.Fragment] showing a login screen to the user.
 */
class LoginFragment : FailureHandlingFragment(R.layout.fragment_login) {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<LoginFragmentViewModel>()
    private val mainViewModel by lazyActivityViewModel<MainActivityViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.authToken.observe(this, Observer {
            if (it.isNotEmpty()) {
                mainViewModel.login()
                findNavController().navigate(R.id.action_fragment_login_to_fragment_home)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLoginBinding.inflate(inflater, container, false).run {
            // For both the username and the password fields, require the input to not be empty
            mapOf(
                viewModel.username to username,
                viewModel.password to password
            ).forEach {
                it.key.observe(viewLifecycleOwner, Observer { content ->
                    it.value.error = if (content.isEmpty()) getString(R.string.login_error_field_required) else null
                })
            }

            model = viewModel
            lifecycleOwner = viewLifecycleOwner
            password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
                // Allow the user to use the keyboard "done" button to trigger a login attempt
                if (id == EditorInfo.IME_ACTION_DONE) {
                    login.callOnClick()
                    return@OnEditorActionListener true
                }

                return@OnEditorActionListener false
            })
            login.setOnClickListener { attemptLogin() }
            return@run root
        }
    }

    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        viewModel.setLoginAllowed(true)
    }

    private fun attemptLogin() {
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()
        var cancel = false
        var focusView: View? = null

        if (usernameStr.isEmpty()) {
            focusView = username
            cancel = true
        }

        if (passwordStr.isEmpty()) {
            focusView = password
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            hideSoftKeyboard(login)
            viewModel.setLoginAllowed(false)
            viewModel.attemptLoginAsync(usernameStr, passwordStr)
        }
    }
}
