# Silenus
Review your favourite restaurants and bars, and locate them on a map!

# Description
This is an app that allows you to create an account, add reviews of your favourite restaurants and bars along with a rating and a location, and view them at a later date.  If you forget where the establishment was, no problem! You can view the location on a map with the help of the Google Maps API!

I should say, just in case, please drink responsibly. This is an app for having a good time, either at a nice restaurant, or a pub, or a bar, or even bowling or an arcade.  The emphasis here is that you should have good time, which can and should be done sensibly!

## Dependencies
* Android Studio. See also [app level](https://github.com/Carkzis/Silenus/blob/master/app/build.gradle) and [project level](https://github.com/Carkzis/Silenus/blob/master/build.gradle) gradle builds.
* Android SDK 30 for running the app.

## Installing
* You can download the code from the Silenus repository by clicking "Code", then "Download ZIP".
* You can then install this from within Android Studio onto an emulator or a mobile device with a minimum SDK of 26 via the "Run 'app'" command (Shift+F10 by default).

## Executing the program
* You can run the app off a suitable emulator/mobile device.
* Login Screen: 
	* Click the "LOGIN!" button! Or don't... but then you can't use the app!
	* You will then have the choice of either logging in using your Google account or by using an E-Mail and Password.
	* If this is your first time using the app, you can create an account here by provided an email, first and last name, and password.
	* Or, provide your password!
	
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/login.png?raw=true" width="300" />
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/googleemail.png?raw=true" width="300" />
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/register.png?raw=true" width="300" />
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/login_dev.png?raw=true" width="300" />

* Welcome Screen:
	* Click the round Your Reviews button to view your current reviews, or logout! Note: You can add a review from within the Your Reviews screen.
	* You can logout by clicking the round Log out? button in the top righthand corner.
	
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/main_screen_dev.png?raw=true" width="300" />

* Your Reviews Screen:
	* Click the "+" in the top right hand corner on the action bar to navigate to the Add Review screen.
	* Click on the map in an individual review to view the location of said review on a map.
	* Click the description in order to view the review in greater detail on the Single Review Screen.
	* You can search for particular reviews using the search bar on the top of the screen.  Currently, you can only search by establishment currently.
	
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/your_reviews_screen_dev.png?raw=true" width="300" />

* Add Review Screen:
	* Add a name, rating, location and (optionally) a description. When you are done, click the tick in the top righthand corner.
	* When you click on location textbox, it will open the map.  On here, drag the map, zoom in or and long press the screen when you have located the establishment. Select "Yes" to confirm your choice; you will be navigated back to the Add Review Screen and the location address will be added.
		* Note: Do not worry if the location says "Nowhere Land"; this either means no address is held or you currently have no internet.  The GeoPoint data will still be held to show you location when you revisit the map at a later time.
		
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/add_review_screen_dev.png?raw=true" width="300" />
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/map_dev.png?raw=true" width="300" />

* Single Review Screen:
	* You can delete the current review by clicking the dustbin button on the action bar. A dialogue will ask you to confirm your decision. Be careful!
	* You can view the establishment on a map by clicking the map button on the action bar.
	* You can edit the review by clicking the pencil button on the action bar. This will direct you to the Edit Review Screen.
	
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/single_review_dev.png?raw=true" width="300" />

* Edit Review Screen:
	* Edit the name, rating, location and (optionally) a description. When you are done, click the tick in the top righthand corner.
	* When you click on location textbox, it will open the map.  On here, drag the map, zoom in or and long press the screen when you have located the establishment. Select "Yes" to confirm your choice; you will be navigated back to the Edit Review Screen and the location address will be updated.
	* Note: Do not worry if the location says "Nowhere Land"; this either means no address is held or you currently have no internet.  The GeoPoint data will still be held to show you location when you revisit the map at a later time.
	
<img src="https://github.com/Carkzis/Silenus/blob/master/silenus_screenshots/edit_screen_dev.png?raw=true" width="300" />

## Authors
Marc Jowett (carkzis.apps@gmail.com)

## Version History
* 1.0
  * Initial Release.  See [commits](https://github.com/Carkzis/Silenus/commits/master).

## Acknowledgements
* [The Android Open Source Project](https://source.android.com/) for the fantastic amount of information to help coders in an accessible way.
* [Jose Alcerreca](https://gist.github.com/JoseAlcerreca/5b661f1800e1e654f07cc54fe87441af) for the Event class, no more reappearing toasts or unwanted navigations!
