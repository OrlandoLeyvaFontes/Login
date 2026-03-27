package OrlandoLeyva.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import OrlandoLeyva.login.ui.PantallaTienda
import OrlandoLeyva.login.ui.PantallaDetalle
import OrlandoLeyva.login.ui.PantallaCarrito
import OrlandoLeyva.login.repository.ProductosRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = PreferenceManager(context = this)
        val gestorCarrito = GestorCarrito(contexto = this)
        val repositorio = ProductosRepository()

        enableEdgeToEdge()
        setContent {
            var estadoPantalla by remember { mutableStateOf(if (pref.isLoggedIn()) "TIENDA" else "ACCESO") }
            var idProductoSeleccionado by remember { mutableStateOf<Int?>(null) }

            when (estadoPantalla) {
                "ACCESO" -> {
                    PantallaAcceso(alAcceder = {
                        pref.saveLoginStatus(isLoggedIn = true)
                        estadoPantalla = "TIENDA"
                    })
                }
                "TIENDA" -> {
                    PantallaTienda(
                        repositorio = repositorio,
                        gestorCarrito = gestorCarrito,
                        alNavegarDetalle = { id ->
                            idProductoSeleccionado = id
                            estadoPantalla = "DETALLE"
                        },
                        alNavegarCarrito = {
                            estadoPantalla = "CARRITO"
                        },
                        alCerrarSesion = {
                            pref.logout()
                            estadoPantalla = "ACCESO"
                        }
                    )
                }
                "DETALLE" -> {
                    if (idProductoSeleccionado != null) {
                        PantallaDetalle(
                            idProducto = idProductoSeleccionado!!,
                            repositorio = repositorio,
                            gestorCarrito = gestorCarrito,
                            alRegresar = { estadoPantalla = "TIENDA" }
                        )
                    } else {
                        estadoPantalla = "TIENDA"
                    }
                }
                "CARRITO" -> {
                    PantallaCarrito(
                        repositorio = repositorio,
                        gestorCarrito = gestorCarrito,
                        alRegresar = { estadoPantalla = "TIENDA" }
                    )
                }
            }
        }
    }

    @Composable
    fun PantallaAcceso(alAcceder: () -> Unit) {
        var correo by remember { mutableStateOf("") }
        var contrasena by remember { mutableStateOf("") }
        var mensajeError by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; mensajeError = "" },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it; mensajeError = "" },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            if (mensajeError.isNotEmpty()) {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (correo == "admin@mail.com" && contrasena == "1234") {
                        alAcceder()
                    } else {
                        mensajeError = "Credenciales incorrectas. Intenta de nuevo."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }
        }
    }
}