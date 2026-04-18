package OrlandoLeyva.login

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val CARRITO_IDS = stringPreferencesKey("contenido_carrito")
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun guardarEstadoSesion(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN)
        }
    }

    val carritoFlow: Flow<Map<Int, Int>> = context.dataStore.data.map { preferences ->
        val data = preferences[CARRITO_IDS] ?: ""
        if (data.isEmpty()) emptyMap()
        else {
            val map = mutableMapOf<Int, Int>()
            data.split(",").forEach {
                val parts = it.split(":")
                if (parts.size == 2) {
                    map[parts[0].toInt()] = parts[1].toInt()
                }
            }
            map
        }
    }

    suspend fun agregarAlCarrito(idProducto: Int, cantidad: Int = 1) {
        context.dataStore.edit { preferences ->
            val data = preferences[CARRITO_IDS] ?: ""
            val carritoActual = if (data.isEmpty()) mutableMapOf() else {
                val map = mutableMapOf<Int, Int>()
                data.split(",").forEach {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        map[parts[0].toInt()] = parts[1].toInt()
                    }
                }
                map
            }
            carritoActual[idProducto] = (carritoActual[idProducto] ?: 0) + cantidad
            preferences[CARRITO_IDS] = carritoActual.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    suspend fun eliminarDelCarrito(idProducto: Int) {
        context.dataStore.edit { preferences ->
            val data = preferences[CARRITO_IDS] ?: ""
            if (data.isEmpty()) return@edit
            
            val map = mutableMapOf<Int, Int>()
            data.split(",").forEach {
                val parts = it.split(":")
                if (parts.size == 2) {
                    map[parts[0].toInt()] = parts[1].toInt()
                }
            }
            
            map.remove(idProducto)
            
            if (map.isEmpty()) {
                preferences.remove(CARRITO_IDS)
            } else {
                preferences[CARRITO_IDS] = map.entries.joinToString(",") { "${it.key}:${it.value}" }
            }
        }
    }

    suspend fun limpiarCarrito() {
        context.dataStore.edit { preferences ->
            preferences.remove(CARRITO_IDS)
        }
    }
}
