package OrlandoLeyva.login

import android.content.Context
import android.content.SharedPreferences

class GestorPreferencias(contexto: Context) {
    private val preferenciasCompartidas: SharedPreferences =
        contexto.getSharedPreferences("prefs_usuario", Context.MODE_PRIVATE)

    fun guardarEstadoSesion(haIniciadoSesion: Boolean) {
        preferenciasCompartidas.edit().putBoolean("ha_iniciado_sesion", haIniciadoSesion).apply()
    }

    fun haIniciadoSesion(): Boolean {
        return preferenciasCompartidas.getBoolean("ha_iniciado_sesion", false)
    }

    fun cerrarSesion() {
        preferenciasCompartidas.edit().clear().apply()
    }
}