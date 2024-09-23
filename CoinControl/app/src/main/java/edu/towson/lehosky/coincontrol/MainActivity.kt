package edu.towson.lehosky.coincontrol


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.towson.lehosky.coincontrol.ui.theme.CoinControlTheme
import kotlinx.coroutines.launch


//Tutorial for creating Bottom Nav bar: https://www.youtube.com/watch?v=c8XP_Ee7iqY
//variables for nav bar that specifiy the route title, icon when selected, icon when not selected, and the page that it routes to
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //create the notification channel (call the function)
        createNotificationChannel()
        setContent {
            val vm: BankViewModel by viewModels()
            val navController = rememberNavController()
            val context = this
            //This below (lines 56 - 81) outlines basically the icons to be shown in the nav bar, will be used for routing
            //Uses Icons library (subject to change) just for an illustration with the page name
            CoinControlTheme {
                val items = listOf(
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        route = "home"
                    ),
                    BottomNavigationItem(
                        title = "Charts",
                        selectedIcon = Icons.Filled.Create,
                        unselectedIcon = Icons.Outlined.Create,
                        route = "planner"
                    ),
                    BottomNavigationItem(
                        title = "Balance",
                        selectedIcon = Icons.Filled.Info,
                        unselectedIcon = Icons.Outlined.Info,
                        route = "balance-screen"
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        route = "accountinfo"
                    ),
                )
                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }
                var userIsLoggedIn by remember { mutableStateOf(false) }


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            if (userIsLoggedIn) {
                                NavigationBar {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                selectedItemIndex = index
                                                navController.navigate(item.route)
                                            },
                                            label = {
                                                Text(
                                                    text = item.title
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex) {
                                                        item.selectedIcon
                                                    } else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = if (userIsLoggedIn) "home" else "login",
                            modifier = Modifier.padding(it)
                        ) {
                            composable("login") {
                                UserLoginScreen(
                                    navController,
                                    vm::getAccount,
                                    vm::setEmail,
                                    vm::setPassword,
                                    vm.isLoginCredentials.value
                                ) {
                                    userIsLoggedIn = true
                                    vm.viewModelScope.launch {
                                        vm.getFinancials()
                                        sendNotification()
                                    }
                                }
                            }
                            composable("signup") { // New route for Sign Up
                                SignUpScreen(
                                    navController,
                                    vm::addAccount
                                )
                            }
                            composable("balance-screen") {
                                BalanceScreen(
                                    vm.currentAccount.value,
                                    vm::updateAccount,
                                    vm::setBills,
                                    vm::setIncome,
                                    vm::setExpenses,
                                    vm::setSavings
                                )
                            }
                            composable("home") {
                                HomeScreen(
                                    updateFinancials = vm::updateFinancials,
                                    setDate = vm::setDate,
                                    context = context,
                                    vm = vm
                                )
                            }
                            composable("accountinfo") {
                                AccountInfo(
                                    vm::updateAccount,
                                    vm::setEmail,
                                    vm::setPassword,
                                    vm.currentAccount.value,
                                    vm::setFirstName
                                )
                            }
                            composable("planner") {
                                Planner(
                                    vm.userFinancials.value,
                                    vm.currentDate.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    //Create a notification channel
    //https://developer.android.com/develop/ui/views/notifications
    //https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder
    //https://developer.android.com/develop/ui/views/notifications/channels
    //https://developer.android.com/develop/ui/views/notifications/build-notification
    private fun createNotificationChannel() {
        val notificationName = "CoinControl"
        val notificationDescription = "Notifying a successful login"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CoinControl-Login", notificationName, importance).apply { description = notificationDescription }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    //function to send notification saying the user successfully logged in
    private fun sendNotification() {
        //intent for main activity to launch when notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "CoinControl-Login")
            .setContentTitle("CoinControl Login")
            .setContentText("Successfully logged into CoinControl.")
            .setSmallIcon(R.drawable.gold_coin_logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        //send the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}
