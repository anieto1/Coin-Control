package edu.towson.lehosky.coincontrol



import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UserLoginScreen(
    navController: NavController,
    getAccount: (String) -> Unit,
    setEmail: (String) -> Unit,
    setPassword: (String) -> Unit,
    isLoginCredentials: Boolean,
    userLoggedIn: () -> Unit
) {
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var isAttemptingLogin by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        isVisible = true
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Logo Design
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000))
        ) {
            Image(
                painter = painterResource(id = R.drawable.gold_coin_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "CoinControl",
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        //Email Field
        TextField(
            value = loginEmail,
            onValueChange = { loginEmail = it },
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        //Password Field
        TextField(
            value = loginPassword,
            onValueChange = { loginPassword = it },
            placeholder = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(4.dp))

        Text(loginError, color = Color.Red)

        Button(
            onClick = {
                if (loginEmail.isNotBlank() && loginPassword.isNotBlank()) {
                    isAttemptingLogin = true
                    setEmail(loginEmail)
                    setPassword(loginPassword)
                    getAccount(loginEmail)
                } else {
                    loginError = "Email and password cannot be empty."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        // Handle login credentials check
        LaunchedEffect(isLoginCredentials, isAttemptingLogin) {
            if (isAttemptingLogin) {
                if (isLoginCredentials) {
                    loginError = ""
                    //navController.navigate("home")
                    userLoggedIn()
                    isAttemptingLogin = false
                } else {
                    loginError = "Incorrect Login! Please Try Again"
                    isAttemptingLogin = false
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Sign Up option
        Text(
            text = "Don't have an account? Sign Up",
            color = Color.Blue,
            modifier = Modifier
                .clickable { navController.navigate("signup") }  // Use the actual route name for the sign-up screen
                .align(Alignment.CenterHorizontally)
        )
    }
}
