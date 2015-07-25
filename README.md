# AroundTheCorner V.01
This repository will contain the work pertaining to the app titled AroundTheCorner.
AroundTheCorner is an android application that allows a user to input a location, 
a distance away from that location, and a phone number. The application will check 
its gps location as well as the gps location input and will send an sms message to 
the phone number supplied when within the distance supplied.

This is a simple app that serves a simple purpose. It uses primarily GPS data with trace amounts of mobile data or Wi-Fi usage.
Allowing for a hands free way to alert someone of your impending arrival.

Application Flow:
1. Short splash screen on first open

2. Main screen that allows entry of information with finish button

3. On finish button click, activity closes and notification appears when service begins

4. notification displays distance and has cancel button

  4.1. Selecting cancel button closes notification and service

5. When distance is within entered range, send text message

6. Once text is sent, close notification and send new notification that states message was send, you are within x distance

7. New notification will have cancel button or can be swiped away, at the point the application is completely gone.


Needed Resources:
- Google fuse location api
- Android notifications
- Android services
