package com.dand.smslogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
* 
* SMSMonitor.java
* @author Trushank
* Date Dec 8, 2012
* Version 1.0
*
 */
 /**
 * @author Trushank
 *
 */
public class SMSMonitor 
{
    private ServiceController mainActivity;
   private ContentResolver contentResolver = null;
   private Handler smshandler = null;
   private ContentObserver smsObserver = null;
   public String smsNumber = "";
   public static boolean thCountStatus = false;
   public static int thIncreCount = 0;
   public boolean monitorStatus = false;
   String code;
 
  
   int smsCount = 0;
   File root ;
   File file;
   FileWriter out;
   
   public SMSMonitor(final ServiceController mainActivity, final Context mainContext) {
      this.mainActivity = mainActivity;
      contentResolver = mainActivity.getContentResolver();
      smshandler = new SMSHandler();
      smsObserver = new SMSObserver(smshandler);
      root = Environment.getExternalStorageDirectory();
      file = new File(root, "SMSLog.txt");
      try {
	out=new FileWriter(file,true);
    } catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
    }
   }

   public void startSMSMonitoring() {
      try {
         monitorStatus = false;
         if (!monitorStatus) {
            contentResolver.registerContentObserver(Uri
                  .parse("content://sms"), true, smsObserver);
         }
      } catch (Exception e) {
         Log.i("SMSMonitor :: startSMSMonitoring Exception == "+ e.getMessage(), "abc");
      }
   }
   
   public void stopSMSMonitoring() {
      try {
         monitorStatus = false;
         if (!monitorStatus) {
            contentResolver.unregisterContentObserver(smsObserver);
         }
      } catch (Exception e) {
         Log.e("KidSafe","SMSMonitor :: stopSMSMonitoring Exception == "+ e.getMessage());
      }
   }

   class SMSHandler extends Handler {
      public void handleMessage(final Message msg) {
      }
   }

   
   class SMSObserver extends ContentObserver {
      private Handler sms_handle = null;
      public SMSObserver(final Handler smshandle) {
         super(smshandle);
         sms_handle = smshandle;
      }

      public void onChange(final boolean bSelfChange) {
         super.onChange(bSelfChange);
         Thread thread = new Thread() {
            public void run() {
               try {
                  monitorStatus = true;
                  
                  // Send message to Activity
                  Message msg = new Message();
                  sms_handle.sendMessage(msg);
                  Uri uriSMSURI = Uri.parse("content://sms");
                  Cursor cur = mainActivity.getContentResolver().query(
                        uriSMSURI, null, null, null, "_id");
                                                
                  if (cur.getCount() != smsCount) {
                     smsCount = cur.getCount();
                                          
                     if (cur != null && cur.getCount() > 0) {
                        cur.moveToLast();
                        for (int i = 0; i < cur.getColumnCount(); i++) 
                        {
                           //Log("KidSafe","SMSMonitor :: incoming Column Name : " +
                              //cur.getColumnName(i));
                              //cur.getString(i));
                        }
                        
                        smsNumber = cur.getString(cur.getColumnIndex("address"));
                        if (smsNumber == null || smsNumber.length() <= 0)
                        {
                           smsNumber = "Unknown";
                     
                        }
                        
                        int type = Integer.parseInt(cur.getString(cur.getColumnIndex("type")));
                        String message = cur.getString(cur.getColumnIndex("body"));
                        Log.i("Sample","SMSMonitor :: SMS type == " + type);
                        Log.i("sample","SMSMonitor :: Message Txt == " + message);
                        Log.i("Sample","SMSMonitor :: Phone Number == " + smsNumber);
                        
                        cur.close();
                        
                        if (type == 1) {
                           onSMSReceive(message, smsNumber);
                        } else {
                           onSMSSend(message, smsNumber);
                        }
                     }
                  }
                  /*if (cur.getCount() < smsCount) {
                     Log("KidSafe","SMS Count last:: " + smsCount);
                     Log("KidSafe","SMS cur Count last:: " + cur.getCount());
                     smsCount = cur.getCount();
                     Log("KidSafe","SMS Count last:: " + smsCount);
                  }*/
               } catch (Exception e) {
                  Log.i("KidSafe","SMSMonitor :: onChange Exception == "+ e.getMessage());
               }
            }
         };
         thread.start();
      }

      
      private void onSMSReceive(final String message, final String number) {
         synchronized (this) {
            Log.i("Sample", "Message"+message);
                               Log.i("Sample", "Number"+number);
                              try {
				out.write("Received: "+number+": "+message+"\n\r");
				SMSLogger.txt.setText(SMSLogger.txt.getText()+"Received: "+number+": "+message+"\n\r");
				out.flush();
			    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
                               
         }
      }

      
      private void onSMSSend(final String message, final String number) {
         synchronized (this) {
            Log.i("Sample", "Message"+message);
                               Log.i("Sample", "Number"+number);
                               try {
                        	  
   				out.write("Sent: "+number+": "+message+"\n\r");
   				SMSLogger.txt.setText(SMSLogger.txt.getText()+"Sent: "+number+": "+message+"\n\r");
   				out.flush();
   			    } catch (IOException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			    }
         }
      }
   }
}
