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
import kotlinx.coroutines.launch
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

        val dataStoreManager = DataStoreManager(context = this)
        val repositorio = ProductosRepository()

        enableEdgeToEdge()
        setContent {
            val isLoggedInFlow = dataStoreManager.isLoggedInFlow.collectAsState(initial = null)
            val scope = rememberCoroutineScope()
            var idProductoSeleccionado by remember { mutableStateOf<Int?>(null) }
            
            var estadoPantallaManual by remember { mutableStateOf<String?>(null) }
            
            if (isLoggedInFlow.value == null && estadoPantallaManual == null) {
                return@setContent
            }
            
            val estadoPantalla = estadoPantallaManual ?: if (isLoggedInFlow.value == true) "TIENDA" else "ACCESO"

            when (estadoPantalla) {
                "ACCESO" -> {
                    PantallaAcceso(alAcceder = {
                        scope.launch { dataStoreManager.guardarEstadoSesion(isLoggedIn = true) }
                        estadoPantallaManual = "TIENDA"
                    })
                }
                "TIENDA" -> {
                    PantallaTienda(
                        repositorio = repositorio,
                        dataStoreManager = dataStoreManager,
                        alNavegarDetalle = { id ->
                            idProductoSeleccionado = id
                            estadoPantallaManual = "DETALLE"
                        },
                        alNavegarCarrito = {
                            estadoPantallaManual = "CARRITO"
                        },
                        alCerrarSesion = {
                            scope.launch { dataStoreManager.cerrarSesion() }
                            estadoPantallaManual = "ACCESO"
                        }
                    )
                }
                "DETALLE" -> {
                    if (idProductoSeleccionado != null) {
                        PantallaDetalle(
                            idProducto = idProductoSeleccionado!!,
                            repositorio = repositorio,
                            dataStoreManager = dataStoreManager,
                            alRegresar = { estadoPantallaManual = "TIENDA" }
                        )
                    } else {
                        estadoPantallaManual = "TIENDA"
                    }
                }
                "CARRITO" -> {
                    PantallaCarrito(
                        repositorio = repositorio,
                        dataStoreManager = dataStoreManager,
                        alRegresar = { estadoPantallaManual = "TIENDA" }
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