package hu.bme.aut.szoftverarch.questly

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit

class LoginActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, LoginFragment())
            }
        }
    }

    fun navigateToRegister() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, RegisterFragment())
            addToBackStack(null)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}