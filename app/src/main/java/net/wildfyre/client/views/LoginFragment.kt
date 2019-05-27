package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import net.wildfyre.client.R
import net.wildfyre.client.data.Failure
import net.wildfyre.client.databinding.FragmentLoginBinding
import net.wildfyre.client.viewmodels.LoginFragmentViewModel
import net.wildfyre.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] showing a login screen to the user.
 */
class LoginFragment : FailureHandlingFragment(R.layout.fragment_login) {
    override val viewModels by lazy { listOf(viewModel) }
    private val viewModel by lazyViewModel<LoginFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        viewModel.authToken.observe(this, Observer {
            if (it.isNotEmpty()) {
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
            lifecycleOwner = this@LoginFragment
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

    override fun onFailure(failure: Failure) {
        super.onFailure(failure)

        if (failure.error == R.string.failure_login) {
            showProgress(false)
        }
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
            // Automatically hide the keyboard since otherwise it won't hide itself
            context?.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
                (it as InputMethodManager).hideSoftInputFromWindow(login.windowToken, 0)
                showProgress(true)
                viewModel.attemptLogin(usernameStr, passwordStr)
            }
        }
    }

    private fun showProgress(show: Boolean) {
        login.visibility = if (show) View.GONE else View.VISIBLE
        progress.visibility = if (show) View.VISIBLE else View.GONE
    }
}
