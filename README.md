This is a Tasker Plugin. I create just to sync my phone and watch ringer mode.
When called, it sends a message to all Bluetooth devices.
Only WearOS devices with the wear .apk installed will be able to handle the message.
The message just contains the phone's current ringer mode.
The WearOS app will read the payload and set the watch's ringer mode to that. 

The watch app needs to be granted DND access via adb with:
`adb shell cmd notification allow_dnd com.rockmanx77777.ringermodesync`

