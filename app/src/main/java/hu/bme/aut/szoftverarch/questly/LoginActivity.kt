package hu.bme.aut.szoftverarch.questly

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hu.bme.aut.szoftverarch.questly.fragments.auth.LoginFragment
import hu.bme.aut.szoftverarch.questly.fragments.auth.RegisterFragment

class LoginActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, LoginFragment())
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount == 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

    fun navigateToRegister() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, RegisterFragment())
            addToBackStack(null)
        }
    }
}