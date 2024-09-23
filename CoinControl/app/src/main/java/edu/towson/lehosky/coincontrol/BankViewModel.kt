package edu.towson.lehosky.coincontrol

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import database.Account
import database.DatabaseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import network.GetFinancials
import network.UserFinancial

class BankViewModel(app: Application) : AndroidViewModel(app) {
    //Access to the database repository
    private val _repository: IAccountRepository

    //Holds the current account based on the login
    private val _currentAccount = mutableStateOf(Account("", "", "", "", "", "", "", ""))
    val currentAccount = _currentAccount

    //Holds the login password
    private val _password = mutableStateOf("")

    //Holds the email
    private val _email = mutableStateOf("")
    val email = _email

    //Boolean to check if the login information matches the current account from the database
    private val _isLoginCredentials = mutableStateOf(false)
    val isLoginCredentials = _isLoginCredentials

    //List of UserFinancials to hold the data being pulled from the RestAPI
    private val _userFinancials: MutableState<List<UserFinancial>>
    val userFinancials: MutableState<List<UserFinancial>>

    //Access to the GetFinancials class which handles HTTP requests to the API
    val getFinancials = GetFinancials()

    //Store the current date selected from the calender
    val _currentDate: MutableState<String> = mutableStateOf("")
    val currentDate = _currentDate

    //Holds the state of the emulator (device) being online
    private var online = false

    init {
        //Get access to the database
        _repository = DatabaseRepository(getApplication())

        _userFinancials = mutableStateOf(listOf())
        userFinancials = _userFinancials


        //Check if the device is currently online so we can make HTTP requests to the API
        val connManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()

        connManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapabilities = connManager.getNetworkCapabilities(network)
                if (networkCapabilities != null) {
                    Log.d("Wifi Connection", "Connected to WiFi")
                }
                online = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                val networkCapabilities = connManager.getNetworkCapabilities(network)
                if (networkCapabilities != null) {
                    Log.d("Wifi Connection", "Connected to WiFi")
                }
                Log.d("Wifi Connection", "Not Connected to WiFi")
                online = false
                viewModelScope.cancel()
            }
        })
    }

    //Set the current date based on which date is clicked in the calender
    fun setDate(date: String) {
        _currentDate.value = date
    }

    //Get the list of financials from the RestAPI based on the current email
    suspend fun getFinancials() {
        viewModelScope.launch {
            _userFinancials.value = getFinancials.getFinancials(_email.value)
        }
    }

    //Add a new UserFinancial object to the list on the RestAPI
    fun updateFinancials(userFinancial: UserFinancial) {
        _userFinancials.value += userFinancial
        viewModelScope.launch {
            getFinancials.addFinancials(_email.value, userFinancial)
        }
    }

    //Set the current password
    fun setPassword(password: String) {
        _password.value = password
        _currentAccount.value.password = password
    }

    //Set the current email
    fun setEmail(email: String) {
        _email.value = email
        _currentAccount.value.email = email
    }

    //Set the current First Name
    fun setFirstName(fName: String) {
        _currentAccount.value.firstName = fName
    }

    //Set the current Bills value
    fun setBills(bills: String) {
        _currentAccount.value.bills = bills
    }

    //Set the current income value
    fun setIncome(income: String) {
        _currentAccount.value.income = income
    }

    //Set the current savings value
    fun setSavings(savings: String) {
        _currentAccount.value.savings = savings
    }

    //Set the current expenses value
    fun setExpenses(exp: String) {
        _currentAccount.value.expenses = exp
    }

    //Calls the database query that updates the current account attributes if any changes were made to them
    fun updateAccount() {
        viewModelScope.launch {
            _repository.updateAccount(_currentAccount.value)
        }
    }

    //This is called when a user tries to click the login button on the Login Screen
    //This takes the email the user is trying to login with and gets the account matching that email from the database
    //If the current email and password being logged in with match the username and password from the database,
    //set the boolean to be true and allow them to login
    //If they don't match then they do not have an account so they cannot login
    fun getAccount(currentEmail: String) {
        viewModelScope.launch {
            try {
                val tempAccount = _repository.getAccount(currentEmail)
                if ((tempAccount.email != _email.value) && (tempAccount.password != _password.value))
                    _isLoginCredentials.value = false
                else {
                    _isLoginCredentials.value = true
                    _currentAccount.value = tempAccount.copy(
                        firstName = tempAccount.firstName,
                        lastName = tempAccount.lastName,
                        email = tempAccount.email,
                        password = tempAccount.password,
                        income = tempAccount.income,
                        expenses = tempAccount.expenses,
                        bills = tempAccount.bills,
                        savings = tempAccount.savings
                    )
                }
            } catch (e: Exception) {
                Log.e("Account Error", e.toString())
            }
        }
    }

    //This is called when a user hits the create account button on the SignUp page
    //It simply calls the query to add the Account with the information they typed in to the database
    fun addAccount(
        fName: String,
        lName: String,
        email: String,
        password: String,
        income: String,
        expenses: String,
        bills: String,
        savings: String
    ) {
        viewModelScope.launch {
            _repository.addAccount(fName, lName, email, password, income, expenses, bills, savings)
        }
    }

    //Creates an intent that opens up the Calculator App when the button is clicked that calls this function
    //**** Note that our emulators did not come stock with calculator so we had to download the APK file online to create
    // the intent for this specific calculator app ****
    //https://www.apkmirror.com/apk/google-inc/google-calculator/google-calculator-8-4-1-520193683-release/google-calculator-8-4-1-520193683-3-android-apk-download/
    fun openCalculator(context: Context) {
        try {
            val calculatorIntent = Intent(Intent.ACTION_MAIN)
            calculatorIntent.setPackage("com.google.android.calculator")
            context.startActivity(calculatorIntent)
        } catch (e: Exception) {
            Log.e("Calculator Error", e.toString())
            Toast.makeText(context, "No calculator app found on device", Toast.LENGTH_SHORT).show()
        }

    }

}