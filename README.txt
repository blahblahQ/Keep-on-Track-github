/***********************************************************
// README file
// Name: cian rafferty
// ucd id: 11346101
//
// Description: This readme provides an overview description of 
//              the different bits of code that I did and helped 
//              with, as well as offering ways to access and/or 
//              test them
//
// Helped mainly by the following source: 
// Ravi Tamada @ androidhive.com
// in particular the following articles:
// http://www.androidhive.info/2014/01/how-to-create-rest-api-for-android-app-using-php-slim-and-mysql-day-12-2/
// 
***********************************************************/

This will include a description of the following
- Authorisation endpoint
- API design and implementation in php
- Database structure
- App functionality
- setup of database and api on remote ec2 aws server


/***********************************************************
// Server side api structure 
// Just a short decription of the structure of the api and authorisation endpoint
// that exists on the aws server
/***********************************************************


-- KeepOnTrack
	`-- images
		`-- forgot_password.png		- image thats included in html changed password email
		`-- welcome.png			- image that include in html welcome email
	`-- include
		`-- config.php			- contains server config parameters
		`-- DbConnect.php		- php to mysql database connector
		`-- DbHandler.php		- contains database handler functions
		`-- PassHash.php		- contains password hashing and checking functions
	`-- libs
		`-- Slim			- slim library
			`-- ...
	`-- v1
		`-- index.php			- contains main body of fucntions and http requests. Includes the api and authorisation endpoint



/***********************************************************/
//			SERVER SIDE
//
// some details about the server functionality i provided
/***********************************************************/

////////////////////////////////
// Authorisation endpoint
// Location: AWS server
// Files: index.php
// Libraries: php slim framework
// Languages: PHP
////////////////////////////////


The authorisation endpoint is the endpoint that describes any interaction with the db that 
doesnt require an api key in the http header, such as:

- login
- register
- profile (change password)



////////////////////////////////
// API design and implementation in php
// Location: AWS server
// Files: index.php
// Libraries: slim php framework
// Languages: PHP
////////////////////////////////

The API describes interactions with the db that require an api_key in the http header.
These include:

- create journey
- finish journey
- add journey data


////////////////////////////////
// Database structure
// Location: AWS server
// Languages: sql
////////////////////////////////

Our solution could be easily implemented with the use of 4 simple interlinked tables:
- users - contains all details about each users profile. 
- journyes - contains all journys made by all users
- user_journeys - links each user with their own journeys
- journey_data - contains all the raw data 


////////////////////////////////
// TESTING
//
// Setup of database and api on remote ec2 aws server
////////////////////////////////

After testing on the localhost using wamp, a ubuntu ec2 instance was setup on aws. 
On it a straightforward configuration of apache2, mysql, php5.5 and phpmyadmin was setup.

Logging onto AWS server:
host: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/

You can log on through:
- phpmyadmin to see the localhost database structure:
	url: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/phpmyadmin/
	username: root
	password: keepontrack

	...or...

- putty to look at the instance structure, installed programs etc [will need to email me, cian.rafferty@ucdconnect.ie and i can provide the ppk key needed to login]
- WinSCP to get a better picture of the file structure or to more easily see it [will need to email me, cian.rafferty@ucdconnect.ie and i can provide the ppk key needed to login]


Testing the api and authorisation endpoints
- to test my design i used Google Chromes Advanced Rest Client: https://chromerestclient.appspot.com/
- using the saved request provided, you can test my api in real time, and using phpmyadmin, you can view the results reflected in the db

Testing api using advanced rest client:

Adding URL: paste the given urls below into the header box near the top
Adding headers: clicking on the form box of the Headers section, you can add a new header, with the key, "Authorisation" and header value equal to the api_key given
Adding parameters: clicking on the form box of the Payload section, ou can add a new values, with the names and values given below

-- Register --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/register
HTTP METHOD: POST
HEADER: none needed
PARAMETERS: email(must be unique), name, username(must be unique), policy, password

-- Login --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/login
HTTP METHOD: POST
HEADER: none needed
PARAMETERS: email, password
RETURNED PARAMS: name, email, username, policy, created at, api_key

-- Change password--
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/profile
HTTP METHOD: POST
HEADER: none needed
PARAMETERS: email, (new)password 
RETURNED PARAMS: 

-- Create new journey --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/journey
HTTP METHOD: POST
HEADER: api_key			<- received in json response after logging into account
PARAMETERS: 
RETURNED PARAMS: journey_id

-- Add journey data point --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/journey/[journey id] <- received in json response after creating a new journey
HTTP METHOD: POST
HEADER: api_key			<- received in json response after logging into account
PARAMETERS: x_gps, y_gps, x_acl, y_acl, z_acl, sample_no, timestamp

-- Finishing Journey --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/journey/[journey id] <- received in json response after creating a new journey
HTTP METHOD: PUT
HEADER: api_key			<- received in json response after logging into account
PARAMETERS: 

-- Getting a single journey --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/journey/[journey id] <- received in json response after creating a new journey
HTTP METHOD: GET
HEADER: api_key			<- received in json response after logging into account
PARAMETERS: 
RETURNED PARAMS: journey_score

-- Getting all the users journeys --
URL: http://ec2-52-17-226-29.eu-west-1.compute.amazonaws.com/Keep_On_Track/v1/journey
HTTP METHOD: GET
HEADER: api_key			<- received in json response after logging into account
PARAMETERS: 
RETURNED PARAMS: journey_scores ...


Notes: email and username must be unique, ie not previously registered to db when signing up. Also a welcome email should be sent after registering, 
while a notification email is sent after a user changes their password 





/***********************************************************
// App Functionality
// Location: mobile app
// Languages: java, and sqlite to include mobile database handling
***********************************************************/


- Login/registration classes in app
- session manager and sqlite handler in app
- App config and controller classes, for correct link up with database
- provided functionality and help with the creation of classes to interact with aPI


In order to test my api on the app side, i added the following classes to an android studio java project.
	- AppConfig.java	- database config 
	- AppController.java	- application class, controlled sending and receiving of http requests
	- LoginActivity.java	- login class, contained methods to send volley http requests and receive responses when a user wanted to login. Stored api key in sqlite database
	- RegisterActivity.java - register class, contained methods to send volley http requests and receive responses when a user wanted to register
	- SQLiteHandler.java	- handled all the above classes interactions with the sqlite database, including storing of user details and deletion of details (log out)
	- SessionManager.java	- contained methods that controlled a users session, ie were they logged in or not, as well as a log out method

After this successfully worked, all these classes were given to Sam to provide him with login and logout capabilities, including a straightforward way to manage http requests through 
use of the volley library. Because these are all included in Sams code (although maybe slightly modified), im not including them in my zip file.



/***********************************************************
// Notes on libraries used
***********************************************************/

- PHP Slim Microframework (api-php)
	Encapsulated a lot of the default http methods, and provided a clean and simple way to quickly create and handle multiple http requests

- Volley library (app-java)
	Provided very much the same functionality on the android studio platform that Slim did with PHP. 
	Was straightforward (ish) to quickly create http requests, and be able to listen for responses from the server.


