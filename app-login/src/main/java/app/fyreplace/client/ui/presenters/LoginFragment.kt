package app.fyreplace.client.ui.presenters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.observe
import app.fyreplace.client.app.login.R
import app.fyreplace.client.app.login.databinding.FragmentLoginBinding
import app.fyreplace.client.ui.hideSoftKeyboard
import app.fyreplace.client.viewmodels.CentralViewModel
import app.fyreplace.client.viewmodels.LoginFragmentViewModel
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException

/**
 * [androidx.fragment.app.Fragment] showing a login screen to the user.
 */
class LoginFragment : FailureHandlingFragment(R.layout.fragment_login) {
    override val viewModel by viewModel<LoginFragmentViewModel>()
    override lateinit var bd: FragmentLoginBinding
    private val centralViewModel by sharedViewModel<CentralViewModel>()
    private val navigator by inject<Navigator> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.authToken.observe(this) {
            if (it.isNotEmpty()) {
                centralViewModel.login()
                navigator.navigateToHome()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLoginBinding.inflate(inflater, container, false).run {
        model = viewModel
        lifecycleOwner = viewLifecycleOwner
        bd = this
        return@run root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // For both the username and the password fields, require the input to not be empty
        mapOf(
            viewModel.username to bd.username,
            viewModel.password to bd.password
        ).forEach { (data, field) ->
            data.observe(viewLifecycleOwner) {
                launch {
                    delay(ERROR_DELAY)
                    field.error =
                        if (it.isEmpty()) getString(R.string.login_error_field_required)
                        else null
                }
            }
        }

        bd.password.setOnEditorActionListener { _, id, _ ->
            // Allow the user to use the keyboard "done" button to trigger a login attempt
            if (id == EditorInfo.IME_ACTION_DONE) true.also { bd.login.callOnClick() } else false
        }

        bd.login.setOnClickListener { attemptLogin() }
        bd.register.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.login_register_link))
                )
            )
        }
    }

    private fun attemptLogin() {
        val usernameStr = bd.username.text.toString()
        val passwordStr = bd.password.text.toString()
        var cancel = false
        var focusView: View? = null

        if (usernameStr.isEmpty()) {
            focusView = bd.username
            cancel = true
        }

        if (passwordStr.isEmpty()) {
            focusView = bd.password
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else launch {
            hideSoftKeyboard(bd.login)

            try {
                viewModel.setLoginAllowed(false)
                viewModel.attemptLogin(usernameStr, passwordStr)
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    Toast.makeText(context, R.string.login_failure_login, Toast.LENGTH_SHORT).show()
                } else {
                    throw e
                }
            } finally {
                viewModel.setLoginAllowed(true)
            }
        }
    }

    interface Navigator {
        fun navigateToHome()
    }

    private companion object {
        const val ERROR_DELAY = 150L
    }
}
