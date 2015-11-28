/////============== Read Me - Keep On Track Application ==============\\\\\

Date: 			27th November 2015
Author: 		Sam Sinnott
Student No: 	11402892
Module Code:	COMP41670
Module Name:	Software Engineering 
University:		University College Dublin, Ireland

-> Overview
This read me gives a brief overview of each activity in the KeepOnTrack
application and the classes behind each activity.
For testing of this application, the following username and password may be
used,
Username: tester@keepontrack.ie
Password: asdf

-> Application Structure:
The Keep On Track Application consists of eight pages:

Page Name:					Java Activity Class:
> Login Page				Login Activity
> Register Page				Register Activity
> Forgot Password Page		Forgot Activity
> Main Page					Main Activity
> Journey Page				Timer Activity
> Settings Page				Settings Activity
> Results Page				Results Activity

-> Functional Classes:
Some of the pages above use the following java classes to add functionality.
These extra pages are as follows:

Class Name:					Functionality:
> App Config 				Declaring URL's for API
> App Controller			Application controller for API Volley requests
> Database Helper			SQLite database functions for user details database
> GPS Tracker				GPS functions for gathering users location
> Session Manager			User login manager
> SQLite Handler			SQLite database for journey data handling

-> Outline for each Java Class:

> Login Activity:
This activity checks the user SQLite database on the phone and checks if
the user is already logged in on the device.
If not logged in, the user will be prompted to enter their details in the 
text fields and this data will be collected and sent to the server for 
validation.
If correct it will be accepted and an API key will be relayed to the users
database to be used for future application login.
This page also has links to other pages such as the register page and
"forgot my password" page.
The layout file associated to this activity is activity_login.xml.

> Register Activity
This activity allows the user to register their details with the KeepOnTrack
server by filling out the form provided.
Once the register button has been clicked, the details will be sent to the 
server for verification and the user will be redirected to the login page
to login using the details outlined before.
The layout file associated with this activity is the activity_register.xml.

> Forgot Activity
This activity is used when users have forgotten the password for their account.
This allows the user to replace the password previously on the account by
entering their email address and the new password.
This page was chosen to show the capability of allowing users to change 
their password from the phone. 
We acknowledge that this method is a security risk if email addresses are known
of other users but this is currently for demo purposes.
The layout file associated with this activity is the activity_forgot_password.xml.

> Main Activity
This activity is where logged in users will be redirected to if already logged in
and newly logged in users will be redirected once logging in correctly.
This activity consists of a selection of three different buttons which will 
redirect the user to different activities accordingly.
The username and email address of the logged in user is printed out at the
top of the screen from the user database.
The layout file used for this activity is the activity_main.xml.

> Timer Activity
This activity is the main functional activity of the application.
When loaded, the user is shown two buttons, a timer and print outs of GPS 
coordinates and maximum accelerometer data.
Once a journey is started, the timer on the top of the screen will start and
a periodic capture of the users data occurs ever 10 seconds. This will also
send a request to the server that this user (identified by api key) is requesting
a unique journey id. The journey id is then received and stored in the users
journey id table as the first entry.

Every ten seconds, the users GPS coordinates are logged and the accelerometers
absolute maximum value (value from 0) will be recorded in an SQLite database
on the phone. Once collected it is then added to a queue to be transmitted to
the server using the "updateJourney" function.

Once the user has logged a journey (greater than 20 seconds long), and the user
presses the "Stop Journey" button. A print out of the users data along that 
journey will be displayed to the user. This originates from the data in the 
SQLite journey database on the phone. While this is being displayed, a close
journey message will be sent to the server to outline that this journey has been
completed correctly. Finally the journey data will be deleted from the journey
database on the phone for preservation of storage space.
The layout file used for this activity is the activity_timer.xml.

Credit to the following sources for inspiration: 
> Timer: http://tekartlife.blogspot.ie/2014/04/creating-timer-in-android.html
> Accelerometer: http://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
> GPS: http://stackoverflow.com/questions/30191360/how-to-use-google-maps-v2-on-android-without-load-the-map

> Settings Activity
This activity in its simplicity allows the user to log out of their account
by pressing the "Logout" button. This will release the users data from both the
user database on the phone and the results database on the phone.
When pressed, the user will be redirected to the login page of the application.
The layout file used for this activity is the activity_settings.xml.

> Results Activity
This activity when created, highlights some data to the user about scores they 
have gathered to-date. This includes the number of journeys they have logged,
the average score on those journeys and the highest score on a journey.
This data is gathered from the journey results table on the phones results database.
There is a text description on the bottom of this page to act as a key for the
data outlined above.
To request new results, the "Get Results" button is on the bottom of the page 
where the user can send a request to the database for all of their results by
sending their api key in the header of the request.
The layout file used for this activity is the activity_results.xml.

Note: In testing this activity appeared to have problems when new user accounts
had been created. This problem outlined was that users couldn't access their results
when querying them. This is due to the user either A) not having created a journey yet
or B) journey results having not been calculated yet.
When accessed with an account which has journeys logged, this is functional.
EG) 
Username: tester1@keepontrack.ie
Password: asdf
