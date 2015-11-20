# Writing-In-Air
## android application development class final project 
<i>(using multiple sensors, accelerometer, gravity, magnetic etc to track clients gesture inputs)</i>
* UI thread: used only to communicate with the user/client (ensure the interaction between user and Views) 
* SensorEventListener thread: used to monitor the sensor events, like onSensorChanged(), as the sampling frequency of the sensor events is relatively high, we would better seperate it from UI to avoid ANR Error
* Background service thread: used to interact with the DatabaseManager, to add,update,delete,select data from SQLiteDatabase on android. (ServiceIntent)
* Using BroadcastReceiver/Handler/Asynctask to send message back to UI and update the UI widgets.
