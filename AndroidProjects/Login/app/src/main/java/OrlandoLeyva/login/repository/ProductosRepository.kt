package OrlandoLeyva.login.repository

import OrlandoLeyva.login.R
import OrlandoLeyva.login.model.Producto

class ProductosRepository {

    val PRODUCTOS = listOf(
        Producto(1, "Bestia Alterada", 15.99, R.drawable.bestialaterada, "Clásico juego de acción y transformación."),
        Producto(2, "Comix Zone", 12.50, R.drawable.comix_zone, "Peleas en un mundo interactivo con estilo de cómic."),
        Producto(3, "Dino Crisis", 25.00, R.drawable.dino_crisis, "Supervivencia y terror contra dinosaurios."),
        Producto(4, "Doom", 20.00, R.drawable.doom, "El padre de los juegos de disparos en primera persona."),
        Producto(5, "Golden Axe", 10.99, R.drawable.golden_axe, "Aventura épica de espadas y magia."),
        Producto(6, "Spider-Man", 30.50, R.drawable.hombrearana, "Aventuras del trepamuros en la gran ciudad."),
        Producto(7, "Super Mario RPG", 45.00, R.drawable.mariorpg, "Un juego de rol inolvidable protagonizado por Mario."),
        Producto(8, "Marvel vs Capcom", 35.00, R.drawable.marvelvzcapcom, "Crossover espectacular de peleas bidimensionales."),
        Producto(9, "Ninja Gaiden", 18.00, R.drawable.ninja_gaiden, "Acción rápida y desafiante de ninjas."),
        Producto(10, "Shinobi III", 14.50, R.drawable.shinobi_iii_sega_1993, "El retorno del ninja supremo con mejores movimientos."),
        Producto(11, "Sonic The Hedgehog", 10.00, R.drawable.sonic1, "El erizo azul más rápido del mundo."),
        Producto(12, "Sonic 3", 16.00, R.drawable.sonic_3, "Nuevas aventuras y el debut de Knuckles.")
    )

    fun buscarPorNombre(query: String): List<Producto> {
        if (query.isBlank()) return PRODUCTOS
        return PRODUCTOS.filter { it.nombre.contains(query, ignoreCase = true) }
    }

    fun obtenerPorId(id: Int): Producto? {
        return PRODUCTOS.find { it.id == id }
    }
}
