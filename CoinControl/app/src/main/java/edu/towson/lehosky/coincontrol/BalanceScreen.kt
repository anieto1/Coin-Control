package edu.towson.lehosky.coincontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import database.Account


@Composable
fun BalanceScreen(
    account: Account,
    updateAccount: () -> Unit,
    setBills: (String) -> Unit,
    setIncome: (String) -> Unit,
    setExpenses: (String) -> Unit,
    setSavings: (String) -> Unit
) {
    val newIncome = remember { mutableStateOf("")}
    val newExpenses = remember { mutableStateOf("")}
    val newBills = remember { mutableStateOf("")}
    val newSavings = remember { mutableStateOf("")}

//I made each container basically an item that can store values coming from our database depending on the title of the item
    Surface(modifier = Modifier.fillMaxSize(), color = Color.LightGray) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BalanceItem(title = "Income", amount = account.income ?: "Loading...", color = MaterialTheme.colorScheme.primary, width = 150.dp)
                BalanceItem(title = "Expenses", amount = account.expenses ?: "Loading...", color = MaterialTheme.colorScheme.primary, width = 150.dp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BalanceItem(title = "Bills", amount = account.bills ?: "Loading...", color = MaterialTheme.colorScheme.primary, width = 150.dp)
                BalanceItem(title = "Savings", amount = account.savings ?: "Loading...", color = MaterialTheme.colorScheme.primary, width = 150.dp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Change Income")
            TextField(value = newIncome.value, onValueChange = { income:String ->
                newIncome.value = income
                setIncome(income)
            })

            Spacer(modifier = Modifier.height(10.dp))
            Text("Change Expenses")
            TextField(value = newExpenses.value, onValueChange = { expenses:String ->
                newExpenses.value = expenses
                setExpenses(expenses)
            })

            Spacer(modifier = Modifier.height(10.dp))
            Text("Change Bills")
            TextField(value = newBills.value, onValueChange = { bills:String ->
                newBills.value = bills
                setBills(bills)
            })

            Spacer(modifier = Modifier.height(10.dp))
            Text("Change Savings")
            TextField(value = newSavings.value, onValueChange = { savings:String ->
                newSavings.value = savings
                setSavings(savings)
            })

            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                updateAccount()
                newIncome.value = ""
                newExpenses.value = ""
                newBills.value = ""
                newSavings.value = ""
            }) {
                Text("Update Balance Info")
            }
        }
    }
}
//this is the functionality of the BalanceItem i used earlier basically styling it
@Composable
fun BalanceItem(title: String, amount: String, color: Color, width: Dp) {
    Card(
        modifier = Modifier
            .width(width)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, style = TextStyle(fontSize = 18.sp))
            Text(text = amount, color = Color.White, style = TextStyle(fontSize = 16.sp))
        }
    }
}