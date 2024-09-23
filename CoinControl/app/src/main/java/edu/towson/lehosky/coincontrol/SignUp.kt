package edu.towson.lehosky.coincontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.navigation.NavController

//This page is meant to handle all the signup functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    addAccount: (String, String, String, String, String, String, String, String) -> Unit
) {
    //values used for the functions
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var expenses by remember { mutableStateOf("") }
    var bills by remember { mutableStateOf("") }
    var savings by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }  // State for storing the error message
//Used scaffold to organize the containers used to collect user data
    Scaffold(
        topBar = {
            TopAppBar(title = {Text("Create your Account", color = Color.White)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary))
        }
    ) {padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                item {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") }
                    )
                }
                item {
                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") }
                    )
                }
                item {
                    TextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = !isValidEmail(email)  // Validate email on change
                        },
                        isError = emailError,  // Use the error state to indicate a problem
                        label = { Text("Email") },
                        placeholder = { Text("example@email.com") }
                    )
                }
                //error message if email is not valid
                item {
                    if (emailError) {
                        Text("Please enter a valid email address.", color = Color.Red)
                    }
                }
                item {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
                //we added password confirmation to make sure you enter it correctly
                item {
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
                item {
                    MoneyInputField(
                        label = "Income",
                        value = income,
                        onValueChange = { income = it }
                    )
                }
                item {
                    MoneyInputField(
                        label = "Expenses",
                        value = expenses,
                        onValueChange = { expenses = it }
                    )
                }
                item {
                    MoneyInputField(
                        label = "Bills",
                        value = bills,
                        onValueChange = { bills = it }
                    )
                }
                item {
                    MoneyInputField(
                        label = "Savings",
                        value = savings,
                        onValueChange = { savings = it }
                    )
                }
                item {
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = Color.Red)
                    }
                }
                //the button checks if passwords match and everything is filled, the user data is stored
                item {
                    Button(
                        onClick = {
                            if (password != confirmPassword) {
                                errorMessage = "Passwords do not match"
                            } else {
                                // Assume further validation passes...
                                errorMessage = ""
                                // Proceed with storing the data and navigating
                                addAccount(
                                    firstName,
                                    lastName,
                                    email,
                                    password,
                                    income,
                                    expenses,
                                    bills,
                                    savings
                                )
                                //navigates back to the login page to enter your information
                                navController.navigate("login")
                            }
                        },
                    ) {
                        Text("Sign Up")
                    }
                }
            }
    }
}
@Composable
fun MoneyInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var internalText by remember { mutableStateOf(value) }

    TextField(
        value = internalText,
        onValueChange = {
            //made sure the values entered are numerical and have two decimals points
            if (it.matches(Regex("^\\$?(([1-9][0-9]{0,2}(,[0-9]{3})*|[0-9]+)(\\.[0-9]{0,2})?|\\.[0-9]{0,2})$"))) {
                internalText = it
                onValueChange(it.filter { it.isDigit() || it == '.' })
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = {
            if (it.text.isNotEmpty()) {
                val numericOnly = it.text.filter { char -> char.isDigit() || char == '.' }
                val formatted = numericOnly.toDoubleOrNull()?.let { num ->
                    "$%.2f".format(num)
                } ?: ""
                TransformedText(AnnotatedString(formatted), OffsetMapping.Identity)
            } else {
                TransformedText(AnnotatedString(""), OffsetMapping.Identity)
            }
        },
        modifier = modifier
    )
}
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

