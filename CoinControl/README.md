This Mobile Application allows users to balance, track and manage their current finances

A user can create an account or log into their current account based on previously creating one. By creating an account, you data is sent to a SQLlite database
where everything is stored for later use. Once you have an account you can login with the username and password you used when creating the account and this will
allow you to have access to the features of our app.

If you successfully login, you will get a notification on your phone. You are directed to the home page after logging in which contains a working calender and fields to 
set the spending amounts on a specific date. You can click any day of
the current month and fill out the financial fields below the calender. This represents the total amount you spent that day on each field. Finally you can click
the 'Add Finances" button which will send the selected day along with the values in the fields to a RestAPI. Every time you login to the app the current data in the
RestAPI is pulled so your infromation is up to date.

On this screen you can also click the Calculator button which will redirect you to the calculator app on your phone if you deem it necessary to calculate certain things 
before submitting your spending.

If you go over to the Charts tab along the bottom, you will see a bar chart visualizing the amount you spent on a particular day for each category. If you wish to view
a chart for a different day, simply go back to the calender, click a different day and go back to the Charts tab. This will show the chart with the spending correlated
to that specific date.

You can navigate to the account info tab along the bottom to see your current login information. You are able to edit these fields to change your name, email, and password.
By clicking the button this will update those field in the database. Meaning the next time you login, you must use the new email and password or else you cannot login.

Finally we have a balance tab which shows the amount you are spending each month based on each financial category. You can edit/change your financials by editing the text
fields and clicking the button. Very similarly to the account info page, this button will update the financial information in the database.

When trying to open calculator, if your emulator does not come with it, install this google calculator apk
https://www.apkmirror.com/apk/google-inc/google-calculator/google-calculator-8-4-1-520193683-release/google-calculator-8-4-1-520193683-3-android-apk-download/


Third Party Libraries:
Android Room
https://developer.android.com/jetpack/androidx/releases/room

OkHttp3
https://square.github.io/okhttp/

YCharts
https://github.com/codeandtheory/YCharts
