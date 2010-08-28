package com.phonegap;
/* License (MIT)
 * Copyright (c) 2008 Nitobi
 * website: http://phonegap.com
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * Software), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient; 
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.LinearLayout;
import android.os.Build.*;
import android.provider.MediaStore;  

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

//import com.phonegap.SimpleGestureFilter.SimpleGestureListener;


public class DroidGap extends Activity  { //implements SimpleGestureListener {    
  
  
      /*-------------------*/             
      
      // private SimpleGestureFilter detector;
      // 
      // @Override 
      // public boolean dispatchTouchEvent(MotionEvent me){ 
      //   this.detector.onTouchEvent(me);
      //   return super.dispatchTouchEvent(me); 
      // }
      //        
      // 
      // @Override
      // public void onSwipe(int direction) {
      //  String str = "";
      // 
      //  switch (direction) {
      // 
      //  case SimpleGestureFilter.SWIPE_RIGHT : 
      //     str = "Swipe Right";
      //     appView.loadUrl("javascript:keyEvent.backTrigger()");
      //     break;
      //  case SimpleGestureFilter.SWIPE_LEFT :  
      //     str = "Swipe Left";
      //     appView.loadUrl("javascript:keyEvent.forwardTrigger()"); 
      //     break;
      //  case SimpleGestureFilter.SWIPE_DOWN :  
      //     str = "Swipe Down";
      //     appView.pageUp(false);
      //     break;
      //  case SimpleGestureFilter.SWIPE_UP :    
      //     str = "Swipe Up";
      //     appView.pageDown(false);
      //     break;
      // 
      //  } 
      //  Log.d(LOG_TAG, "---onSwipe---- :: " + str);
      //   //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
      // }
      // 
      // //@Override
      // public void onDoubleTap() {
      //  Log.d(LOG_TAG, "---onDoubleTap----");
      //    //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show(); 
      // }  
      /*---------------------------*/
  	
	private static final String LOG_TAG = "PhoneGapDroidGap";
	protected WebView appView;
	private LinearLayout root;	
	
	private Device gap;
	private GeoBroker geo;
	private AccelBroker accel;
	private CameraLauncher launcher;
	private ContactManager mContacts;
	private FileUtils fs;
	private NetworkManager netMan;
	private CompassListener mCompass;
	private Storage	cupcakeStorage;
	private CryptoHandler crypto;
	private BrowserKey mKey;
	private AudioHandler audio;

	private Uri imageUri;
	
	private boolean startingCamera = false;
	
    @Override      
    public void onPause() { 
      if (!this.startingCamera){  
        appView.loadUrl("javascript:sp.android.onPause();");
      }
      super.onPause();    
        
      //appView.loadUrl("about:blank");
      Log.d(LOG_TAG, "onPause");
    }
    @Override      
    public void onResume() { 
      super.onResume();
      if (!this.startingCamera){                                   
        appView.loadUrl("javascript:if (sp && sp.android) { sp.android.onResume() };");  
        //appView.loadUrl("file:///android_asset/www/splash.html");
      }    
      this.startingCamera = false;
      Log.d(LOG_TAG, "onResume");
    }

     /** Called when the activity is first created. */
    @Override      
    public void onCreate(Bundle savedInstanceState) {   
        Log.d(LOG_TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
        // This builds the view.  We could probably get away with NOT having a LinearLayout, but I like having a bucket!        
        // LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
        //          ViewGroup.LayoutParams.FILL_PARENT, 0.0F);     
         
        LinearLayout.LayoutParams webviewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT, 0.0F);
                    		
        
        // root = new LinearLayout(this);           
        // root.setOrientation(LinearLayout.VERTICAL);
        // root.setBackgroundColor(Color.BLACK);
        // root.setLayoutParams(containerParams);
           
        //umjesto donje kontrole dodao ovo:                         
        //appView = new WebView(this);        
                                        
        appView = (new WebView(this){     
                    
          private long lastDownEvent = 0;    
          private float lastDownY = 0; 
          private float lastDownX = 0;
          private boolean isFroyo = android.os.Build.VERSION.RELEASE.startsWith("2.2");
          
          @Override           
          public boolean onTouchEvent(MotionEvent me) {                          
            
            //Log.d(LOG_TAG, "onTouchEvent " + me.toString() + (isFroyo ? " Froyo" : " nije Froyo") + " version: " + android.os.Build.VERSION.RELEASE );
                        
            if (me.getAction() == 0){
              lastDownEvent = System.currentTimeMillis();
              lastDownY = me.getY();
              lastDownX = me.getX();
            }
            if (me.getAction() == 2){
              boolean blizuZadnjegDown = Math.abs(me.getY() - lastDownY) < 20 &&  Math.abs(me.getX() - lastDownX) < 20;              
              if (blizuZadnjegDown){              
                if (System.currentTimeMillis() - lastDownEvent > 300){
                  me.setAction(0);
                  lastDownEvent = System.currentTimeMillis() - 200;
                }                                                                                   
              }
            }      

            if (!isFroyo){
              if (me.getAction() == 2 && Math.abs(me.getY() - lastDownY) / Math.abs(me.getX() - lastDownX) < 1){
                //Log.d(LOG_TAG, "onTouchEvent - " + me.toString());
              }else{
                //Log.d(LOG_TAG, "onTouchEvent + " + me.toString());
                super.onTouchEvent(me);              
              } 
            }else{
              super.onTouchEvent(me);
            }
                        
            return true;
          }
        });
                 
         
        //iskljucio start                       
        appView.setLayoutParams(webviewParams);
        
        WebViewReflect.checkCompatibility();
                
        if (android.os.Build.VERSION.RELEASE.startsWith("2.")){
          appView.setWebChromeClient(new EclairClient(this));
        }
        else
        {        
         appView.setWebChromeClient(new GapClient(this));
        }
        
        appView.setWebViewClient(new GapViewClient(this));       
                 
        //appView.setInitialScale(100);         
        appView.setVerticalScrollBarEnabled(true);
        appView.setVerticalScrollbarOverlay(true);
        appView.setHorizontalScrollbarOverlay(true);  
        appView.setHorizontalScrollBarEnabled(false);
        
        appView.setSoundEffectsEnabled(true); 
                        
        WebSettings settings = appView.getSettings();        
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);   
        settings.setBuiltInZoomControls(false);      
        
        
        Package pack = this.getClass().getPackage();
        String appPackage = pack.getName();             
        WebViewReflect.setStorage(settings, true, "/data/data/" + appPackage + "/app_database/");        
        // Turn on DOM storage!
        WebViewReflect.setDomStorage(settings);
        // Turn off native geolocation object in browser - we use our own :)
        WebViewReflect.setGeolocationEnabled(settings, false);
        // Bind the appView object to the gap class methods /
        bindBrowser(appView);
        if(cupcakeStorage != null)
          cupcakeStorage.setStorage(appPackage);     
               
        //iskljucio end                          
                 
        // // //dodao jer sam gore isljucio
        // WebSettings settings = appView.getSettings();
        // settings.setJavaScriptEnabled(true);
        // 
        // appView.setVerticalScrollbarOverlay(true);
        // appView.setHorizontalScrollbarOverlay(true);
                            
        //root.addView(appView);           
        //setContentView(root);                         
        setContentView(appView);
        
        //detector = new SimpleGestureFilter(this,this);                               
    } 
        	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      //don't reload the current page when the orientation is changed
      super.onConfigurationChanged(newConfig);
    } 
    
    private void bindBrowser(WebView appView)
    {
    	gap = new Device(appView, this);
    	geo = new GeoBroker(appView, this);
    	accel = new AccelBroker(appView, this);
    	launcher = new CameraLauncher(appView, this);
    	mContacts = new ContactManager(appView, this);
    	fs = new FileUtils(appView);
    	netMan = new NetworkManager(appView, this);
    	mCompass = new CompassListener(appView, this);  
    	crypto = new CryptoHandler(appView);
    	mKey = new BrowserKey(appView, this);
    	audio = new AudioHandler(appView, this);
    	
    	// This creates the new javascript interfaces for PhoneGap
    	appView.addJavascriptInterface(gap, "DroidGap");
    	appView.addJavascriptInterface(geo, "Geo");
    	appView.addJavascriptInterface(accel, "Accel");
    	appView.addJavascriptInterface(launcher, "GapCam");
    	appView.addJavascriptInterface(mContacts, "ContactHook");
    	appView.addJavascriptInterface(fs, "FileUtil");
    	appView.addJavascriptInterface(netMan, "NetworkManager");
    	appView.addJavascriptInterface(mCompass, "CompassHook");
    	appView.addJavascriptInterface(crypto, "GapCrypto");
    	appView.addJavascriptInterface(mKey, "BackButton");
    	appView.addJavascriptInterface(audio, "GapAudio");
    	
    	
    	if (android.os.Build.VERSION.RELEASE.startsWith("1."))
    	{
    		cupcakeStorage = new Storage(appView);
    		appView.addJavascriptInterface(cupcakeStorage, "droidStorage");
    	}
    }
           
 
	public void loadUrl(String url)
	{                
		appView.loadUrl(url);
	}

	public class GapViewClient extends WebViewClient {		
		
		Context mCtx;
		
		public GapViewClient(Context ctx)
		{
			mCtx = ctx;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 * 
		 * Note: Since we override it to make sure that we are using PhoneGap and not some other bullshit
		 * viewer that may or may not exist, we need to make sure that http:// and tel:// still work.
		 * 
		 */
		
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	      Log.d(LOG_TAG, "shouldOverrideUrlLoading url:" + url); 
	    	// TODO: See about using a switch statement
	    	if (url.startsWith("http://"))
	    	{
	    		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	    		startActivity(browse);
	    		return true;
	    	}
	    	else if(url.startsWith("tel://"))
	    	{
	    		Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
	    		startActivity(dial);
	    		return true;
	    	}
	    	else if(url.startsWith("sms:"))
	    	{  
	    		Uri smsUri = Uri.parse(url);
	    		Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
	    		intent.setType("vnd.android-dir/mms-sms");
	    		startActivity(intent);
	    		return true;
	    	}   
	    	//fix trackball click was reloading app
	    	else if(url.startsWith("file:///android_asset/www/index.html")){
	    	  return false;
	    	}
	    	else if(url.startsWith("mailto:"))
	    	{
	    		Intent mail = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
	    		startActivity(mail);
	    		return true;
	    	}
	    	else
	    	{
	    		//We clear the back button state
	    		mKey.reset();
	    		view.loadUrl(url);
	    		return false;
	    	}
	    }
	}
	
	
  /**
    * Provides a hook for calling "alert" from javascript. Useful for
    * debugging your javascript.
  */
	public class GapClient extends WebChromeClient {				
		
		Context mCtx;
		public GapClient(Context ctx)
		{
			mCtx = ctx;
		}
		
		@Override
	    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
	        //Log.d(LOG_TAG, message);
	        // This shows the dialog box.  This can be commented out for dev
	        AlertDialog.Builder alertBldr = new AlertDialog.Builder(mCtx);
	        GapOKDialog okHook = new GapOKDialog();
	        GapCancelDialog cancelHook = new GapCancelDialog();
	        alertBldr.setMessage(message);
	        alertBldr.setTitle("Upozorenje");
	        alertBldr.setCancelable(true);
	        alertBldr.setPositiveButton("OK", okHook);
	        //alertBldr.setNegativeButton("Cancel", cancelHook);
	        alertBldr.show();
	        result.confirm();
	        return true;
	    }
	    
	    
	    @Override
      public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) 
      {
          new AlertDialog.Builder(mCtx)
              .setTitle("Provjera")
              .setMessage(message)
              .setPositiveButton("Da", //android.R.string.ok, 
                      new DialogInterface.OnClickListener() 
                      {
                          public void onClick(DialogInterface dialog, int which) 
                          {
                              result.confirm();
                          }
                      })
              .setNegativeButton("Ne",//android.R.string.cancel, 
                      new DialogInterface.OnClickListener() 
                      {
                          public void onClick(DialogInterface dialog, int which) 
                          {
                              result.cancel();
                          }
                      })
          .create()
          .show();
      
          return true;
      };

		/*
		 * This is the Code for the OK Button
		 */
		
		public class GapOKDialog implements DialogInterface.OnClickListener {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}			
		
		}
		
		public class GapCancelDialog implements DialogInterface.OnClickListener {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}			
		
		}
	  

	}
	
	public final class EclairClient extends GapClient
	{		
		private String TAG = "PhoneGapLog";
		private long MAX_QUOTA = 100 * 1024 * 1024;
		
		public EclairClient(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}
		
		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
		    	     long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
		{
		  Log.d(TAG, "event raised onExceededDatabaseQuota estimatedSize: " + Long.toString(estimatedSize) + " currentQuota: " + Long.toString(currentQuota) + " totalUsedQuota: " + Long.toString(totalUsedQuota));  	
		  
			if( estimatedSize < MAX_QUOTA)
		    	{	                                        
		    	  //increase for 1Mb        		    	  		    	  
		    		long newQuota = currentQuota + 1024*1024;		    		
		    		Log.d(TAG, "calling quotaUpdater.updateQuota newQuota: " + Long.toString(newQuota) );  	
		    		quotaUpdater.updateQuota(newQuota);
		    	}
		    else
		    	{
		    		// Set the quota to whatever it is and force an error
		    		// TODO: get docs on how to handle this properly
		    		quotaUpdater.updateQuota(currentQuota);
		    	}		    	
		}		
		                    		                    
		// console.log in api level 7: http://developer.android.com/guide/developing/debug-tasks.html
    public void onConsoleMessage(String message, int lineNumber, String sourceID)
    {                  
      Log.d("PhoneGapClientLog", sourceID + ": Line " + Integer.toString(lineNumber) + " : " + message);              
    }
		
	}

  /*		
  public boolean onTouchEvent (MotionEvent ev)  {
    Log.d(LOG_TAG, "onTouchEvent 2 " + ev.toString());    
    return false; //ev.getAction() == 2;
  }    
  */    
  public boolean onTrackballEvent (MotionEvent ev){
    Log.d(LOG_TAG, "onTrackballEvent " + ev.toString());
    return false;
  }
  
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
      Log.d("PhoneGapLog", "onKeyDown " + Integer.toString(keyCode));
    
      if (keyCode == KeyEvent.KEYCODE_BACK) 
      {
        appView.loadUrl("javascript:keyEvent.backTrigger()");
        return true;
      }

      if (keyCode == KeyEvent.KEYCODE_MENU) 
      {
        appView.loadUrl("file:///android_asset/www/index.html");//"
        //appView.loadUrl("javascript:keyEvent.menuTrigger()");
        return true;
      }

      if (keyCode == KeyEvent.KEYCODE_SEARCH) 
      {
        appView.loadUrl("javascript:keyEvent.searchTrigger()");
        return true;
      }

      return false;
  }
    // This is required to start the camera activity!  It has to come from the previous activity
    public void startCamera()
    {  
      this.startingCamera = true;
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
    	   super.onActivityResult(requestCode, resultCode, intent);
    	   
    	   if (resultCode == Activity.RESULT_OK) {
    		   Uri selectedImage = imageUri;
    	       getContentResolver().notifyChange(selectedImage, null);
    	       ContentResolver cr = getContentResolver();
    	       Bitmap bitmap;
    	       try {
    	            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
    	            launcher.processPicture(bitmap);
    	       } catch (Exception e) {
    	    	   launcher.failPicture("Did not complete!");
    	       }
    	    }
    	   else
    	   {
    		   launcher.failPicture("Did not complete!");
    	   }
    }

    public WebView getView()
    {
      return this.appView;
    }
      
}
