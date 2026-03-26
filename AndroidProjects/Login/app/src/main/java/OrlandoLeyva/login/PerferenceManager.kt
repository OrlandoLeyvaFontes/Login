package OrlandoLeyva.login

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences( "user_prefs",  Context.MODE_PRIVATE)

    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in",  isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(  "is_logged_in",  false)
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}