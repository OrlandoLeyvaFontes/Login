package OrlandoLeyva.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import OrlandoLeyva.login.DataStoreManager
import kotlinx.coroutines.launch
import OrlandoLeyva.login.model.Producto
import OrlandoLeyva.login.repository.ProductosRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTienda(
    repositorio: ProductosRepository,
    dataStoreManager: DataStoreManager,
    alNavegarDetalle: (Int) -> Unit,
    alNavegarCarrito: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    var consultaBusqueda by remember { mutableStateOf("") }
    val productos = repositorio.buscarPorNombre(consultaBusqueda)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda de Videojuegos") },
                actions = {
                    IconButton(onClick = alNavegarCarrito) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                    }
                    Button(onClick = alCerrarSesion) {
                        Text("Salir")
                    }
                }
            )
        }
    ) { relleno ->
        val context = LocalContext.current
        Column(modifier = Modifier.padding(relleno).fillMaxSize()) {
            OutlinedTextField(
                value = consultaBusqueda,
                onValueChange = { consultaBusqueda = it },
                label = { Text("Buscar videojuego...") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            val scope = rememberCoroutineScope()

            LazyColumn {
                items(productos) { productoLista ->
                    ElementoProducto(
                        producto = productoLista,
                        alHacerClic = { alNavegarDetalle(productoLista.id) },
                        alAgregarCarrito = {
                            scope.launch { dataStoreManager.agregarAlCarrito(productoLista.id) }
                            Toast.makeText(context, "Se agregó ${productoLista.nombre} al carrito", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ElementoProducto(producto: Producto, alHacerClic: () -> Unit, alAgregarCarrito: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = alHacerClic
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${producto.precio}", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = alAgregarCarrito) {
                Text("Añadir")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    idProducto: Int,
    repositorio: ProductosRepository,
    dataStoreManager: DataStoreManager,
    alRegresar: () -> Unit
) {
    val producto = repositorio.obtenerPorId(idProducto)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Detalle") },
                navigationIcon = {
                    Button(onClick = alRegresar) { Text("X") }
                }
            )
        }
    ) { relleno ->
        if (producto != null) {
            Column(modifier = Modifier.padding(relleno).fillMaxSize().padding(16.dp)) {
                Image(
                    painter = painterResource(id = producto.imagen),
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = producto.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(text = "$${producto.precio}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = producto.descripcion, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                Button(
                    onClick = {
                        scope.launch { dataStoreManager.agregarAlCarrito(producto.id) }
                        Toast.makeText(context, "Se agregó ${producto.nombre} al carrito", Toast.LENGTH_SHORT).show()
                        alRegresar()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Añadir al carrito")
                }
            }
        } else {
            Text("Producto no encontrado", modifier = Modifier.padding(relleno))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito(
    repositorio: ProductosRepository,
    dataStoreManager: DataStoreManager,
    alRegresar: () -> Unit
) {
    val articulosCarrito by dataStoreManager.carritoFlow.collectAsState(initial = emptyMap())
    val productosEnCarrito = articulosCarrito.mapNotNull { (idArticulo, cantidad) ->
        val prod = repositorio.obtenerPorId(idArticulo)
        if (prod != null) Pair(prod, cantidad) else null
    }

    val totalArticulos = productosEnCarrito.sumOf { it.second }
    val precioTotal = productosEnCarrito.sumOf { it.first.precio * it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    Button(onClick = alRegresar) { Text("Volver") }
                }
            )
        }
    ) { relleno ->
        Column(modifier = Modifier.padding(relleno).fillMaxSize()) {
            if (productosEnCarrito.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(productosEnCarrito) { (productoLista, cantidad) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${productoLista.nombre} (x$cantidad)", modifier = Modifier.weight(1f))
                            Text(text = "$${productoLista.precio * cantidad}", modifier = Modifier.padding(end = 8.dp))
                            val scope = rememberCoroutineScope()
                            IconButton(onClick = {
                                scope.launch { dataStoreManager.eliminarDelCarrito(productoLista.id) }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar articulo", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Resumen de compra", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Total de artículos: $totalArticulos")
                        Text(text = "Total a pagar: $$precioTotal", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
