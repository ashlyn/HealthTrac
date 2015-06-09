# RAIK383H-Group-7
Repository for Raik383H Software Engineering Group 7 HealthTrac project
The project will consist of a server-side API, web dashboard, and Android application that consumes the web API services.

# HealthTrac
RAIK383H-Group-7 :
 Mike Casper, Josh Dunne, Noah Gould, and Ashlyn Lee
### Version
1.0

###Overview

Welcome to HealthTrac!

HealthTrac is an innovative health and fitness application.  The goal of our product is to allow users to conveniently track the excercise they do.  In today's world, who doesn't have a smartphone?  That is why we have integrated all the benefits of expensive recording equipment into a seamless mobile and web service, driven completely by your phone!

###Use
To use HealthTrac, you only need an Android device and a Facebook account.  Simply log in with your Facebook credentials after opening our application.  If it is your first time logging in, you will be presented with a screen for creating your user profile.  Please enter your full name, preferred name, birthday, location, gender, height, weight, and email.  After creating your account, you are in our database and can be found by other people.  Your profile will display certain information about you to other people.  If you want to change information, there is a button to edit it.

You can form groups with other people, such as friends or colleagues.  You can search for these group pages.  On the display page for each one, you can view the group name and description as well as the members.  You can choose to join, or if already part of the group, leave.  If you are an admin, you can edit the group name and description.  If you want to form a team that does not exist, there is a button to create a group where you can fill in the required information.

We also have a web app that lets you view information.  You can login with your Facebook credentials and view profiles.  

As of right now, that is the majority of the functionality.  However, in the future, you will be able to log activities, view statistics, compete with groups, and earn badges.  

###Technical Information
The HealthTrac mobile app was created using Java for the Android operating system.  The web application was developed using C# and the entity framework, using SQL for a database.  Our mobile app is able to make API calls to the database (hosted on Azure) to receive information.  We have created data models to store and organize all this information. Some activities, such as generating end of day reports, must be completed with a regular time schedule. To accomplish these tasks, we set up Azure schedulers to call an endpoint that completes the task.

### Outside Libraries Used
We used a few outside libraries for Android, listed here: Otto, OkHttp, and Retrofit by Square, GSON by Google, Crouton by Github user KeyboardSurfer, EazeGraph, RoboGuice, Picasso, and Robolectric. These libraries assist with accessing information from outside databases. Otto is an event bus that decreases coupling between objects by simply posting information on a bus line and allowing any objects subscribed to it to access the object. Crouton improves upon Toast functionality. EazeGraph was used to create the UI of our goals view in the app. Picasso was used as a way to asyncronously load images. RoboGuice is a dependency injection framework for Android that allowed us to inject members to decrease coupling throughout the app. Robolectric is a tool used for testing that allows us to mock the android interface to avoid stub methods that would usually exist when using junit tests. We also used a few libraries to implement some aspects of material design throughout the application.

The server uses Entity Framework for data persistence and Unity for inversion of control through dependency injection in the constructors. Identity Framework is used to manage user logins on the server.

A basic predictive model has been implemented to classify the types of activities. A random forest model has been created and trained on sample data. To create the forest, an open-source tree & forest library in F# called Charon was used. The library capabilities were extended to work with the type of data associated with an activity (user age, user height, user weight, duration, distance, steps) and can be called utilizing an endpoint in the Web API. The forest will be created periodically to conserve time and resources and will typically be used by deserializing the forest classifier a binary-encoded file. This component can be treated as a black-box service or library, although the code is included in the repository to that the team can change the predictive model if needed.

### Server Configuration
Some changes must be made to the Web.config in order to run the server. Add ```MultipleActiveResultSets=true``` to the end of the connection string (likely a LocalDB). Disable WebDAV features in IIS by ensuring that a module and a handler is removed by added the following:

```
 <modules>
    <remove name="WebDAVModule" />
 </modules>
 <handlers>
    <remove name="WebDAV" />
 </handlers>
```

Additionally, you will need to include the Facebook App ID and App Secret in the Web.config by adding them to the appSettings section as follows, placing the keys in the values parameters. The keys allow the application to use Facebook to log users in. The server will not run without the keys. Contact developers for the Facebook application keys.

```
<appSettings>
   <add key="appId" value="" />
   <add key="appSecret" value="" />
 </appSettings>
```

Ensure that you have the proper binding for Identity core. It must list 2.0.0 as the max ```oldVersion``` and ```newVersion```.

```
<runtime>
    <assemblyBinding xmlns="urn:schemas-microsoft-com:asm.v1">
      <dependentAssembly>
    				<assemblyIdentity name="Microsoft.AspNet.Identity.Core" publicKeyToken="31BF3856AD364E35" culture="neutral" />
    				<bindingRedirect oldVersion="0.0.0.0-2.0.0.0" newVersion="2.0.0.0" />
    		</dependentAssembly>
    </assemblyBinding>
</runtime>
```

### Android Testing
In order to run the tests in Android Studio, you must install the plugin "Android Studio Unit Test" (version 1.4.0). To install this plugin, go to the File -> Settings and from the window that opens, under IDE Settings, click plugins. Click the Browse repositories button and search for "Android Studio Unit Test" and install. When running the tests, right click on the tests folder under ./app/src/test/ and click "Run Tests" (The one with a black and gray rectangular icon with a green and red arrow).

NOTE: Directories in your home path (C:/Users/UserName) <b>CANNOT</b> have a space (" ") in the path. This is due to an error in Robolectric.
