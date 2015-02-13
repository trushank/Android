package com.dand.smslogger;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
* 
* ServiceController.java
* @author Trushank
* Date Dec 8, 2012
* Version 1.0
* 
 * 
 */
/**
 * @author Trushank
 *
 */
public class ServiceController extends Service
{
   private Activity mainActivity;
   SMSMonitor sms;
   
   public IBinder onBind(Intent intent) {
      // TODO Auto-generated method stub
      return null;
   }
   
   public void onCreate() 
   {
      super.onCreate();
     
     // /**** Start Listen to Outgoing SMS ****/
       Log.i("KidSafe","###### ServiceController :: CallSMS Monitor method ######");
       Toast.makeText(getApplicationContext(), "SMS Logging",1).show();
      sms = new SMSMonitor(this , mainActivity);
     sms.startSMSMonitoring();
      
   }
   
   @Override
     public void onDestroy() {
        super.onDestroy();

        /**** Stop Listen to Outgoing SMS ****/
        Log.i("KidSafe","###### ServiceController :: StopSMS Monitor method ######");
        sms.stopSMSMonitoring();
       
    }
}
