package com.aiworkereeg.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiworkereeg.launcher.MusicService;
import com.aiworkereeg.launcher.GlassView;
import com.aiworkereeg.launcher.GlassView.GlassThread;
import com.aiworkereeg.launcher.MusicPlayerView;
import com.aiworkereeg.launcher.MusicPlayerView.MusicPlayerThread;
import com.aiworkereeg.launcher.R;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;



public class MainActivity extends Activity implements SurfaceHolder.Callback{
	
	private BluetoothAdapter bluetoothAdapter;
	TGDevice tgDevice;
	private static final boolean RAW_ENABLED = false; // false by default
	
	TextView tv_T1;	TextView tv_A;	TextView tv_M; TextView tv_TimeToSel; TextView tv_consoleBoard;
	private int At = 50;     private int Med = 50;
	TextView tv_Med;    TextView tv_Att;    TextView tv_Vel;    TextView tv_AmM;    
	Button b; 
    	/** A handle to the thread that's actually running the animation. */
   // private GlassThread mGlassThread;
    private MusicPlayerThread mMusicPlayerThread;
    
    	/** A handle to the View in which the game is running. */
    //private GlassView mGlassView;
    private MusicPlayerView mMusicPlayerView;
    final int ActivityTwoRequestCode = 0;
    Bitmap myBitmap;
    
    // -- camera 
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    private boolean flag_camera = true; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		//setContentView(R.layout.fragment_main);
		   
        // tell system to use the layout defined in our XML file
        //setContentView(R.layout.glass_layout);
        setContentView(R.layout.musicplayer_layout);

       // Intent myIntent = new Intent(this, dnaConsoleActivity.class);
    	//startActivity(myIntent);       
    	
        // get handles to the GlassView from XML, and its GlassThread
        //mGlassView = (GlassView) findViewById(R.id.lunar);
        //mGlassThread = mGlassView.getThread();         
        
        
        mMusicPlayerView = (MusicPlayerView) findViewById(R.id.lunar);
        mMusicPlayerThread = mMusicPlayerView.getThread();
        
     //   mGlassView = (GlassView) findViewById(R.id.lunarGlass);
       // mGlassThread = mGlassView.getThread();

        // give the GlassView a handle to the TextView used for messages
        mMusicPlayerView.setTextView((TextView) findViewById(R.id.text));
        
       // b = (Button) findViewById(R.id.b_RunDNAconsole);
        // give the GlassView a handle to the TextView used for messages
        tv_Att = (TextView) findViewById(R.id.Att_text);
        tv_Med = (TextView) findViewById(R.id.Med_text);
        
        tv_Vel = (TextView) findViewById(R.id.Vel_text);
        tv_AmM = (TextView) findViewById(R.id.AmM_text);
        
        tv_TimeToSel = (TextView) findViewById(R.id.TimeToSel);
        tv_consoleBoard = (TextView) findViewById(R.id.console_info);
        
        if (savedInstanceState == null) {
            // we were just launched: set up a new game
           // mGlassThread.setState(GlassThread.STATE_READY);
          //  mMusicPlayerThread.setState(MusicPlayerThread.STATE_READY);
            //Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
           // mMusicPlayerThread.restoreState(savedInstanceState);        	
            //Log.w(this.getClass().getName(), "SIS is nonnull");
        }
		
		
		tv_T1 = (TextView) findViewById(R.id.text);
		tv_A = (TextView) findViewById(R.id.Att_text);
        tv_M = (TextView) findViewById(R.id.Med_text);
		
        /* Checking BT and connecting to the TG device */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {	// Alert user that Bluetooth is not available
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            tgDevice = new TGDevice(bluetoothAdapter, handler);
            //Toast.makeText(this, "Bluetooth is available ir", Toast.LENGTH_LONG).show();
            doStuff();
        }	
         
       // doCameraShot();

       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
		Log.d(getString(R.string.app_name), "camera on create"); 
		
        //controlInflater = LayoutInflater.from(getBaseContext());
       // View viewControl = controlInflater.inflate(R.layout.control, null);
       // LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, 	LayoutParams.FILL_PARENT);
       // this.addContentView(viewControl, layoutParamsControl);
        
      /*  Button buttonTakePicture = (Button)findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
			}});*/
      
               
	}


    	/** Invoked when the Activity loses user focus.    */
    @Override
    protected void onPause() {

    
        super.onPause();
        mMusicPlayerView.getThread().pause(); // pause game when Activity pauses
        mMusicPlayerView.getThread().setRunning(false); //correctly destroy SurfaceHolder, ir          
    }
	
    @Override
	public void onDestroy() {    	
	        tgDevice.close();        
	        super.onDestroy();       
	}
	    
	@Override
	protected void onStop() {       
	        super.onStop();  // Always call the superclass method first	       
	}
	
	    /**
	     * Notification that something is about to happen, to give the Activity a
	     * chance to save state.
	     * * @param outState a Bundle into which this Activity should save its state
	     */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	        // just have the View's thread save its state into our Bundle
	        super.onSaveInstanceState(outState);
	       // mGlassThread.saveState(outState);
	}
	    //=======================================
	public void doStuff() {
	        //Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
	        if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
	            tgDevice.connect(RAW_ENABLED);
	        }
	}
	      
	    
	    // -- save bitmap of screenshot
	    public void saveBitmap(Bitmap bitmap) {
	     //   String filePath = Environment.getExternalStorageDirectory() + File.separator + "Pictures/screenshot.png";
	       // File imagePath = new File(filePath);
	        
	        File pictureFileDir = getDir();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
    	    String date = dateFormat.format(new Date());
    	    String photoFile = "eeg_scr" + date + ".png";

    	    String filePath = pictureFileDir.getPath() + File.separator + photoFile;
    	    File imagePath = new File(filePath);
	        
	        FileOutputStream fos;
	        try {
	            fos = new FileOutputStream(imagePath);
	            bitmap.compress(CompressFormat.PNG, 100, fos);
	            fos.flush();
	            fos.close();
	           // sendMail(filePath);
	        } catch (FileNotFoundException e) {
	            Log.e("GREC", e.getMessage(), e);
	        } catch (IOException e) {
	            Log.e("GREC", e.getMessage(), e);
	        }
	    }
	    
	    // start dnaConsol
	    public void start_dnaConsol(View view) {
	    	//Toast.makeText(this, "starting space shuttle", Toast.LENGTH_SHORT).show();
	        Intent myIntent = new Intent(view.getContext(), dnaConsoleActivity.class);
	       // myIntent.putExtra("user_name", displayName);
	       // myIntent.putExtra("GameMode", "4s");
	    	startActivity(myIntent);
	    	//startActivityForResult(myIntent, ActivityTwoRequestCode);
	    	
	    }
	    
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        switch (keyCode) {
	            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
	            case KeyEvent.KEYCODE_HEADSETHOOK:
	                startService(new Intent(MusicService.ACTION_TOGGLE_PLAYBACK));
	                return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	    	    
	    
	    /* Handles messages from TGDevice */
	    private final Handler handler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {               
	                switch (msg.what) {
	                case TGDevice.MSG_STATE_CHANGE:
	                    /*display message according to state change type */
	                    switch (msg.arg1) {
	                    case TGDevice.STATE_IDLE:
	                        break;
	                    case TGDevice.STATE_CONNECTING:
	                    	//mGlassThread.setTGStatus("Connecting...");
	                    	tv_T1.setText("Connecting...");
	                        break;
	                    case TGDevice.STATE_CONNECTED:
	                        tgDevice.start();
	                        tv_T1.setText("Connected");
	                        //mGlassThread.setTGStatus("Connected");
	                        //mGlassThread.setGameMode(GameMode_str);
	                        mMusicPlayerThread.doStart(); //start game
	                       // flag_camera = true;
	        	    		/*camera.stopPreview();
	        	    		camera.release();
	        	    		camera = null;
	        	    		previewing = false;
	        	    		Log.d(getString(R.string.app_name), "camera release ir");
	        	    		camera = Camera.open();  		//camera.setDisplayOrientation(90);
	        	    		Log.d(getString(R.string.app_name), "camera open ir"); 
	        	    		
	                        try {
	    	    				camera.setPreviewDisplay(surfaceHolder);
	    	    				camera.startPreview();
	    	    				previewing = true;
	    	    				Log.d(getString(R.string.app_name), "camera start preview ir");
	    	    			} catch (IOException e) {
	    	    				// TODO Auto-generated catch block
	    	    				e.printStackTrace();
	    	    			}*/
	                      /*  camera.stopPreview();
	                        camera.release();
	        	    		camera = null;
	        	    		previewing = false;*/
	        	    		
	                        //mGlassThread.doStart();
	                        break;
	                    case TGDevice.STATE_NOT_FOUND:
	                    	//mGlassThread.setTGStatus("Can't find");
	                    	tv_T1.setText("Can't find");
	                        break;
	                    case TGDevice.STATE_NOT_PAIRED:
	                    	//mGlassThread.setTGStatus("not paired");
	                    	tv_T1.setText("not paired !!!!!");
	                        break;
	                    case TGDevice.STATE_DISCONNECTED:
	                    	//mGlassThread.setTGStatus("Disconnected");
	                    	tv_T1.setText("Disconnected");
	                    }

	                    break;
	                case TGDevice.MSG_POOR_SIGNAL:
	                         //int TGState = msg.arg1;
	                    break;
	                case TGDevice.MSG_RAW_DATA:
	                	//raw1 = msg.arg1;
	                    //tv.append("Got raw: " + msg.arg1 + "\n");                  
	                    break;
	                case TGDevice.MSG_HEART_RATE:
	                    //tv.append("Heart rate: " + msg.arg1 + "\n");
	                    break;
	                case TGDevice.MSG_ATTENTION:
	                    // First send Attention data to the backend in async way
	                    //APIClient.postData(null, "attention", String.valueOf(msg.arg1), null);

	                	//Log.e(getString(R.string.app_name), "camera.takePicture()");  
	                	
	 	               
	                    At = msg.arg1;         
	                    tv_A.setText(String.valueOf(At));
	                    mMusicPlayerThread.setAttention(At);
	                    
	                    // -- do appropriate action for music player
	                    if (mMusicPlayerView.getThread().play_flag == true)
                    		{ 
	                    	 startService(new Intent(MusicService.ACTION_PLAY)); 
	                    	}
	                    if (mMusicPlayerView.getThread().stop_flag == true)
                			{ 
	                    	 startService(new Intent(MusicService.ACTION_STOP));
                			}
	                    
	                    if (mMusicPlayerView.getThread().next_flag == true)
                			{ 
	                    	 startService(new Intent(MusicService.ACTION_SKIP));
                			 mMusicPlayerView.getThread().next_flag = false;
                			}
	                    
	                    if (mMusicPlayerView.getThread().back_flag == true)
            				{ 
	                    	 //startService(new Intent(MusicService.ACTION_PAUSE));
	                    	 startService(new Intent(MusicService.ACTION_STOP)); 
	                    	 mMusicPlayerView.getThread().back_flag = false;
            				// onBackPressed();
            				} 
	                    
	                    if (mMusicPlayerView.getThread().Picture_flag == true)
        				{ 	                    	
	                    	camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
	                    	
	                    	mMusicPlayerView.getThread().Picture_flag = false;
        				}
	                    
	                    if (mMusicPlayerView.getThread().Prtscr_flag == true)
        				{ 	                    	
		                    View v1 = findViewById(android.R.id.content).getRootView() ; //this works too but gives only content
		                   // View v1 = getWindow().getDecorView().getRootView();
	                    	v1.setDrawingCacheEnabled(true);
		                    myBitmap = v1.getDrawingCache();
		                    saveBitmap(myBitmap);
	                    	
	                    	mMusicPlayerView.getThread().Prtscr_flag = false;
        				// onBackPressed();
        				}
	                    
	                   // if (mMusicPlayerView.getThread().MusicPlayerFlag == true) { camera.stopPreview(); previewing = false;}
	                   // if (mMusicPlayerView.getThread().DnaConsoleFlag == true) {camera.stopPreview(); previewing = false;}
	                   // if (mMusicPlayerView.getThread().MusicPlayerFlag == false) { previewing = true;}
	                   // if (mMusicPlayerView.getThread().DnaConsoleFlag == false) { previewing = true;}
	                    
	                    	// -- display velocity based on accel_alpha [0..2.5]
	                    float vel = mMusicPlayerView.getThread().accel_alpha;
	                    if (vel>=2f) {tv_Vel.setText("4");}
	                    if (vel>=1.5f && vel<2f) {tv_Vel.setText("3");}
	                    if (vel>=1f && vel<1.5f) {tv_Vel.setText("2");}
	                    if (vel>=0.5f && vel<1f) {tv_Vel.setText("1");}
	                    if (vel<0.5f) {tv_Vel.setText("0");}  
	                    
	                    	// -- display time to action selection
	                    tv_TimeToSel.setTextColor(Color.WHITE);
	                    float tts = mMusicPlayerView.getThread().TimeToSelect;
	                    tts = tts*10f;
	                    tts = Math.round(tts);
	                    tts = tts/10f;
	                    //if (tts < 3f && vel<=0 && mMusicPlayerView.getThread().flag_Cursor)
	                    //if (mMusicPlayerView.getThread().action_cancel_flag){tv_TimeToSel.setText("cancel");}
	                    if (tts < 3f &&  mMusicPlayerView.getThread().flag_Cursor) {
	                    	tv_TimeToSel.setTextSize(20); tv_TimeToSel.setText(String.valueOf(Math.round(tts)) ); 
	                    	mMusicPlayerView.getThread().msgBoard = " ";}
	                    else {
	                    	tv_TimeToSel.setTextSize(20); tv_TimeToSel.setText(mMusicPlayerView.getThread().msgBoard);
	                    	}
	                    if (mMusicPlayerView.getThread().action_cancel_flag){
	                    	tv_TimeToSel.setTextSize(20);tv_TimeToSel.setText("cancel");
	                    	mMusicPlayerView.getThread().msgBoard = "";
	                    	}
	                    
	                    tv_consoleBoard.setTextSize(10);tv_consoleBoard.setText(mMusicPlayerView.getThread().consoleBoard);
	                    

	                   /* tv_Att.setText(String.valueOf(At)); // display meditation
	                    // change size and color of Att text view
	                    if (At < 30) {
	                    	//tv_Att.setTextColor(Color.YELLOW);
	                    	tv_Att.setTextSize(20);
	                    } else {
	                        if (At <70) {
	                        	//tv_Att.setTextColor(Color.GREEN);
	                        	tv_Att.setTextSize(30);
	                        } else {
	                        	//tv_Att.setTextColor(Color.RED);
	                        	tv_Att.setTextSize(40);
	                        }                    
	                    } 
	                    
	                    tv_Vel.setText(String.valueOf(At+Med)); // display Att-Med
	                    // change size and color of Att+Med text view 
	                    if (At+Med < 60) {
	                    	tv_Vel.setTextSize(20);
	                    } else {
	                        if (At+Med <140) {
	                        	tv_Vel.setTextSize(30);
	                        } else {
	                        	tv_Vel.setTextSize(40);
	                        }                    
	                    }   */                
	                    
	                    //display numer of correct/incorrect words
	                    //tv_CorrectWords.setText(String.valueOf(mGlassView.getThread().CorrectW));
	                    //tv_IncorrectWords.setText(String.valueOf(mGlassView.getThread().IncorrectW));
	                    
	                    // -- display Genome TextViews
	                    /*genome =mGlassView.getThread().DesGenSequence;
	                    //tv_ActGenSeq.setText(String.valueOf(mGlassView.getThread().ScreenFlag));
	                    if (mGlassView.getThread().ScreenFlag<9){
	                    	genome_letter = Character.toString(genome.charAt(mGlassView.getThread().ScreenFlag-1));
	                    }*/
	                                        
	                    // -- send seq to GlassThread
	                   // tv_DesGenSeq.setText(mGlassView.getThread().DesGenSequence);
	                   // tv_GenLet.setText(genome + "\n" + Character.toString(mGlassView.getThread().CurrentLetter));
	                    //tv_GenLet.setTextColor(Color.GREEN);
	                    	
	                    /*if (genome_letter.equals("A")) { 
	                    	mGlassThread.setGenome(0,0,genome_letter); 
	                    	tv_GenLet.setText(genome_letter + "(00)");
	                    }                     
	                    if (genome_letter.equals("C")){ 
	                    	mGlassThread.setGenome(0,1,genome_letter);
	                    	tv_GenLet.setText(genome_letter + "(01)");
	                    }
	                    if (genome_letter.equals("T")){ 
	                    	mGlassThread.setGenome(1,0,genome_letter); 
	                    	tv_GenLet.setText(genome_letter + "(10)");
	                    }
	                    if (genome_letter.equals("G")){ 
	                    	mGlassThread.setGenome(1,1,genome_letter);
	                    	tv_GenLet.setText(genome_letter + "(11)");
	                    }*/
	                    	
	                    //tv_ActGenSeq.setText(String.valueOf(mGlassView.getThread().ActGenSequence));
	                   // tv_ActGenSeq.setText(String.valueOf(GenLetLengthScore.length()));
	                    //tv_ActGenSeq.setTextColor(Color.BLUE);
	                    
	                    //tv_ActGenSeqBin.setText(String.valueOf(mGlassView.getThread().ActGenSequenceBin));
	                    
	                   /* Intent output = new Intent();
	                    //output.putExtra(com.aiworkereeg.saveopportunity.Main.Number1Code, mGlassView.getThread().GameScore);
	                    //output.putExtra(com.aiworkereeg.saveopportunity.Main.Number1Code, GenLetLengthScore.length());
	                    output.putExtra(com.aiworkereeg.saveopportunity.Main.Number1Code, 
	                    				mGlassView.getThread().CorrectW);
	                    setResult(RESULT_OK, output);
	                    //finish();
	                    
	                    // --saving data to file
	                    String filename ="so_v2_<date_time>.csv";
	                    Time now = new Time();
	                    now.setToNow();
	                    String date_time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));                    
	                    filename = "so_v2_" + date_time + ".csv";
	                    
	                   // writeToExternalStoragePublic(filename, user_g, now, At, Med);
	                    */
	                    break;
	                    
	                case TGDevice.MSG_MEDITATION:
	                    //APIClient.postData(null, "meditation", String.valueOf(msg.arg1), null);

	                    Med = msg.arg1;
	                    tv_M.setText(String.valueOf(Med));
	                    mMusicPlayerThread.setMeditation(Med);
	                    
	                    tv_Med.setText(String.valueOf(Med)); // display meditation
	                    // change size and color of Med text view
	                    if (Med < 30) {
	                    	//tv_Med.setTextColor(Color.YELLOW);
	                    	//tv_Med.setTextSize(20);
	                    } else {
	                        if (Med <70){
	                        	//tv_Med.setTextColor(Color.GREEN);
	                        	//tv_Med.setTextSize(30);
	                        } else {
	                        	//tv_Med.setTextColor(Color.RED);
	                        	//tv_Med.setTextSize(40);
	                        }                    
	                    }
	 
	                    tv_AmM.setText(String.valueOf(At-Med)); // display Att-Med
	                    // change size and color of Att-Med text view                   
	                    if (Math.abs(At-Med) <= 15)	{tv_AmM.setTextSize(30); tv_AmM.setTextColor(Color.WHITE);}
	                    	else if (Math.abs(At-Med) <= 30) {tv_AmM.setTextSize(30); tv_AmM.setTextColor(Color.WHITE); }
	                    
	                    if (At-Med < -45 || At-Med > 45) {tv_AmM.setTextSize(30); tv_AmM.setTextColor(Color.GREEN);}
	                    	else if (At-Med < -30 || At-Med > 30) {tv_AmM.setTextSize(30); tv_AmM.setTextColor(Color.GREEN); }
	                                       
	                    
	                    
	                    
	                    
	                    
	                    break;
	                case TGDevice.MSG_BLINK:
	                    //tv.append("Blink: " + msg.arg1 + "\n");
	                    //tv_b.setText(String.valueOf(msg.arg1));
	                    break;
	                case TGDevice.MSG_RAW_COUNT:
	                    //tv.append("Raw Count: " + msg.arg1 + "\n");
	                    break;
	                case TGDevice.MSG_LOW_BATTERY:
	                    Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
	                    break;
	                case TGDevice.MSG_RAW_MULTI:
	                    //TGRawMulti rawM = (TGRawMulti)msg.obj;
	                    //tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
	                
	                case TGDevice.MSG_SLEEP_STAGE:
	                	//sleep_stage = msg.arg1;
	                	break;
	                case TGDevice.MSG_EEG_POWER:
	                    TGEegPower eegPower = (TGEegPower) msg.obj;
	                    
	                   /* delta = eegPower.delta;
	                    high_alpha = eegPower.highAlpha;
	                    high_beta = eegPower.highBeta;
	                    low_alpha = eegPower.lowAlpha;
	                    low_beta = eegPower.lowBeta;
	                    low_gamma = eegPower.lowGamma;
	                    mid_gamma = eegPower.midGamma;
	                    theta = eegPower.theta;*/
	                    break;
	                default:
	                    break;
	                }

	            }
	        };
	        
	    /* Handles messages from Camera */
	        ShutterCallback myShutterCallback = new ShutterCallback(){

	    		@Override
	    		public void onShutter() {
	    			// TODO Auto-generated method stub
	    			
	    		}
	    	};
	    		
	    	PictureCallback myPictureCallback_RAW = new PictureCallback(){

	    		@Override
	    		public void onPictureTaken(byte[] arg0, Camera arg1) {
	    			// TODO Auto-generated method stub
	    			
	    		}
	    	};
	    		
	    PictureCallback myPictureCallback_JPG = new PictureCallback(){

	    	@Override
	    	public void onPictureTaken(byte[] arg0, Camera arg1) {
	    			// TODO Auto-generated method stub
	    		//Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
	    		
	    		Log.e(getString(R.string.app_name), "camera bitmapPicture");
	    		try {
					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				previewing = true;
				
				// --- saving picture
				File pictureFileDir = getDir();
	    	    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
	    	      //Log.d(MakePhotoActivity.DEBUG_TAG, "Can't create directory to save image.");
	    	     // Toast.makeText(context, "Can't create directory to save image.", Toast.LENGTH_LONG).show();
	    	      return;
	    	    }
	    	    
	    	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
	    	    String date = dateFormat.format(new Date());
	    	    String photoFile = "eeg_" + date + ".jpg";

	    	    String filename = pictureFileDir.getPath() + File.separator + photoFile;

	    	    File pictureFile = new File(filename);
	    	    try {
		    	      FileOutputStream fos = new FileOutputStream(pictureFile);
		    	      fos.write(arg0);
		    	      fos.close();
		    	     // Toast.makeText(context, "New Image saved:" + photoFile,  Toast.LENGTH_LONG).show();
		    	    } catch (Exception error) {
		    	     // Log.d(MakePhotoActivity.DEBUG_TAG, "File" + filename + "not saved: "  + error.getMessage());
		    	      //Toast.makeText(context, "Image could not be saved.", Toast.LENGTH_LONG).show();
		    	    }
	    	}
	    };

	    	  
	        private File getDir() {
	    	    File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    	    return new File(sdDir, "eeg_picture");
	        }
	    	
	    	
	    	@Override
	    	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
	    		// TODO Auto-generated method stub
	    		if(previewing){
	    			camera.stopPreview();
	    			previewing = false;
	    			Log.d(getString(R.string.app_name), "camera stop preview");
	    		}
	    		
	    		if (camera != null){
	    			try {
	    				camera.setPreviewDisplay(surfaceHolder);
	    				camera.startPreview();
	    				previewing = true;
	    				Log.d(getString(R.string.app_name), "camera start preview");
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}
	    		Log.d(getString(R.string.app_name), "camera surfaceChanged ir");
	    	}

	    	@Override
	    	public void surfaceCreated(SurfaceHolder holder) {
	    		// TODO Auto-generated method stub
	    		if(flag_camera ){
	    		camera = Camera.open();
	    		////camera.startPreview();
	    		//camera.setDisplayOrientation(90);
	    		Log.d(getString(R.string.app_name), "camera open"); 
	    		}
	    	}

	    	@Override
	    	public void surfaceDestroyed(SurfaceHolder holder) {
	    		// TODO Auto-generated method stub
	    		camera.stopPreview();
	    		camera.release();
	    		camera = null;
	    		previewing = false;
	    		Log.d(getString(R.string.app_name), "camera release");
	    	}

}
