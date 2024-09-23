package edu.towson.lehosky.coincontrol

import database.Account

interface IAccountRepository {
    suspend fun updateAccount(account: Account)
    suspend fun addAccount(fName: String,
                                   lName:String,
                                   email:String,
                                   password: String,
                                   income: String,
                                   expenses:String,
                                   bills: String,
                                   savings: String)
    suspend fun getAccount(currentEmail: String): Account

}