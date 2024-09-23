package database

import android.app.Application
import androidx.room.Room
import edu.towson.lehosky.coincontrol.IAccountRepository

class DatabaseRepository(appContext: Application):IAccountRepository {
    private val accountDb: AccountDatabase

    init {
        accountDb = Room.databaseBuilder(appContext, AccountDatabase::class.java, "account.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    //Call updateAccount query
    override suspend fun updateAccount(account: Account) {
        accountDb.accountDao().updateAccount(account)
    }

    //Call addAccount query
    override suspend fun addAccount(
        fName: String,
        lName:String,
        email:String,
        password: String,
        income: String,
        expenses:String,
        bills: String,
        savings: String
    ) {
        accountDb.accountDao().addAccount(Account(
            firstName = fName,
            lastName =lName,
            email = email,
            password = password,
            income = income,
            expenses = expenses,
            bills = bills,
            savings = savings
        ))
    }


    //Call getAccount query
    override suspend fun getAccount(currentEmail: String): Account {
        return accountDb.accountDao().getAccount(currentEmail)
    }

}