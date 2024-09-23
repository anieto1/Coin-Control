package edu.towson.lehosky.coincontrol

import android.content.Context
import android.util.Log
import android.widget.CalendarView
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import network.UserFinancial
import java.util.Calendar
import java.util.Date


@Composable
fun HomeScreen(
    vm: BankViewModel,
    updateFinancials: (UserFinancial) -> Unit,
    setDate: (String) -> Unit,
    context: Context
) {
    //Reference to showing Calendar: https://developer.android.com/reference/android/icu/util/Calendar
    //variables used by the user to enter in their financial spending on a certain calendar day
    var selectedDate by remember { mutableStateOf(Date().time) }
    var rentExpense by remember { mutableStateOf(0f) }
    var billsExpense by remember { mutableStateOf(0f) }
    var utilitiesExpense by remember { mutableStateOf(0f) }
    var personalSpendingExpense by remember { mutableStateOf(0f) }
    var groceriesExpense by remember { mutableStateOf(0f) }
    var unsavedChangesDialogVisible by remember { mutableStateOf(false) }
    var currentSelectedDate by remember { mutableStateOf(selectedDate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                //Calendar creation
                val calendarView = CalendarView(context)
                calendarView.date = selectedDate
                calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val newSelectedDate = calendar.timeInMillis
                    //setDate(selectedDate.toDateString())

                    //check if the user input for text fields are not the default 0
                    if (hasUnsavedChanges(
                            rentExpense, billsExpense, utilitiesExpense,
                            personalSpendingExpense, groceriesExpense
                        )
                    ) {
                        unsavedChangesDialogVisible = true
                        currentSelectedDate = selectedDate
                        selectedDate = newSelectedDate
                    } else {
                        selectedDate = newSelectedDate
                        setDate(selectedDate.toDateString())
                    }
                }
                calendarView
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        //LazyColumn to show the date the user selected and all expense fields the user can enter spending habits on that day
        LazyColumn {
            item { Text(text = "Selected Date: ${selectedDate.toDateString()}") }
            item {
                ExpenseField("Rent", rentExpense) { rentExpense = it }
                ExpenseField("Bills", billsExpense) { billsExpense = it }
                ExpenseField("Utilities", utilitiesExpense) { utilitiesExpense = it }
                ExpenseField(
                    "Personal Spending",
                    personalSpendingExpense
                ) { personalSpendingExpense = it }
                ExpenseField("Groceries", groceriesExpense) { groceriesExpense = it }
            }
            //button in lazy column to send financials to BankViewModel and calculate spending
            item {
                Button(onClick = {
                    updateFinancials(
                        UserFinancial(
                            date = selectedDate.toDateString(),
                            bills = billsExpense,
                            rent = rentExpense,
                            utilities = utilitiesExpense,
                            personalSpending = personalSpendingExpense,
                            groceries = groceriesExpense
                        )
                    )
                }) {
                    Text("Add Financials")
                }
            }
            //Button to all the user to open the calculator app for manual calculation
            item {
                Button(
                    onClick = { vm.openCalculator(context) },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Calculator")
                }
            }
        }
        //if statement to check if the user changed any default values of the text fields, used to show an Alert Dialog
        if (unsavedChangesDialogVisible) {
            UnsavedChangesDialog(
                showDialog = unsavedChangesDialogVisible,
                onDismiss = { unsavedChangesDialogVisible = false },
                onConfirm = {
                    unsavedChangesDialogVisible = false
                    selectedDate = currentSelectedDate
                    setDate(selectedDate.toDateString())
                }
            )
        }
    }
}

//Composable for Expense text fields
@Composable
fun ExpenseField(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        TextField(
            value = value.toString(),
            onValueChange = { onValueChange(it.toFloatOrNull() ?: 0f) },
            modifier = Modifier.weight(1f)
        )
    }
}
//Calendar date string formation
fun Long.toDateString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)

    return "$month/$day/$year"
}

//Function to check if the user modified any text fields for expenses
fun hasUnsavedChanges(
    rentExpense: Float,
    billsExpense: Float,
    utilitiesExpense: Float,
    personalSpendingExpense: Float,
    groceriesExpense: Float
): Boolean {
    // Check if any text field is not the default 0
    return rentExpense != 0f || billsExpense != 0f || utilitiesExpense != 0f ||
            personalSpendingExpense != 0f || groceriesExpense != 0f
}

//Function to build the alert dialog when a user tries to switch dates without submitting
@Composable
fun UnsavedChangesDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        val ctx = LocalContext.current
        Log.d("UnsavedChangesDialog", "Context: $ctx")
        AlertDialog.Builder(ctx, R.style.Theme_CoinControl)
            .setTitle("Unsaved Changes")
            .setMessage("You have unsaved changes. Are you sure you want to switch dates?")
            .setPositiveButton("Yes") { _, _ ->
                Log.d("UnsavedChangesDialog", "PositiveButton clicked")
                onConfirm()
            }
            .setNegativeButton("No") { _, _ ->
                Log.d("UnsavedChangesDialog", "NegativeButton clicked")
                onDismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}
