package edu.towson.lehosky.coincontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import database.Account

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfo(
    updateAccount: () -> Unit,
    setEmail: (String) -> Unit,
    setPassword: (String) -> Unit,
    currentAccount: Account,
    setFirstName: (String) -> Unit
) {
    val name = remember{ mutableStateOf(currentAccount.firstName)}
    val email = remember{ mutableStateOf(currentAccount.email)}
    val password = remember { mutableStateOf(currentAccount.password)}

    Scaffold (
        topBar = {
            TopAppBar(
                title = {Text(
                    text = "Account Info",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "First Name"
                )

                //Send to new name in the TextField to be updated in the ViewModel
                OutlinedTextField(
                    value = name.value, onValueChange = { newName ->
                        name.value = newName
                        setFirstName(newName)
                    },
                    singleLine = true,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Email Address"
                )

                //Send to new email in the TextField to be updated in the ViewModel
                OutlinedTextField(
                    value = email.value, onValueChange = { newEmailAddress ->
                        email.value = newEmailAddress
                        setEmail(newEmailAddress)
                    },
                    singleLine = true,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Password"
                )

                //Send to new password in the TextField to be updated in the ViewModel
                OutlinedTextField(
                    value = password.value, onValueChange = { newPassword ->
                        password.value = newPassword
                        setPassword(newPassword)
                    },
                    singleLine = true,
                    modifier = Modifier.padding(10.dp)
                )
            }

            //Calls ViewModel function to change the name, email, and password attributes in the database
            Button(onClick = {
                updateAccount()
            }) {
                Text("Update Account")
            }
        }
    }
}

