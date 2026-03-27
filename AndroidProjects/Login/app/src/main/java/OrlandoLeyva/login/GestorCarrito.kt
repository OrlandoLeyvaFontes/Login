package OrlandoLeyva.login

import android.content.Context
import android.content.SharedPreferences

class GestorCarrito(contexto: Context) {
    private val preferencias: SharedPreferences =
        contexto.getSharedPreferences("datos_carrito", Context.MODE_PRIVATE)

    fun obtenerArticulosCarrito(): Map<Int, Int> {
        val cadenaCarrito = preferencias.getString("contenido_carrito", "") ?: ""
        if (cadenaCarrito.isEmpty()) return emptyMap()

        val mapa = mutableMapOf<Int, Int>()
        val pares = cadenaCarrito.split(",")
        for (par in pares) {
            val partes = par.split(":")
            if (partes.size == 2) {
                mapa[partes[0].toInt()] = partes[1].toInt()
            }
        }
        return mapa
    }

    fun agregarAlCarrito(idProducto: Int, cantidad: Int = 1) {
        val carritoActual = obtenerArticulosCarrito().toMutableMap()
        carritoActual[idProducto] = (carritoActual[idProducto] ?: 0) + cantidad
        guardarCarrito(carritoActual)
    }

    private fun guardarCarrito(carrito: Map<Int, Int>) {
        val cadenaCarrito = carrito.entries.joinToString(",") { "${it.key}:${it.value}" }
        preferencias.edit().putString("contenido_carrito", cadenaCarrito).apply()
    }
    
    fun limpiarCarrito() {
        preferencias.edit().remove("contenido_carrito").apply()
    }
}
