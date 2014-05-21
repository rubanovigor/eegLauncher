package com.aiworkereeg.launcher;

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
import android.graphics.Color;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aiworkereeg.launcher.GlassView;
import com.aiworkereeg.launcher.GlassView.GlassThread;
import com.aiworkereeg.launcher.MusicPlayerView;
import com.aiworkereeg.launcher.MusicPlayerView.MusicPlayerThread;
import com.aiworkereeg.launcher.R;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;


public class MainActivity extends Activity {
	
	private BluetoothAdapter bluetoothAdapter;
	TGDevice tgDevice;
	private static final boolean RAW_ENABLED = false; // false by default
	
	TextView tv_T1;	TextView tv_A;	TextView tv_M; TextView tv_TimeToSel;
	private int At = 50;     private int Med = 50;
	TextView tv_Med;    TextView tv_Att;    TextView tv_Vel;    TextView tv_AmM;    
	
    	/** A handle to the thread that's actually running the animation. */
    //private GlassThread mGlassThread;
    private MusicPlayerThread mGlassThread;
    	/** A handle to the View in which the game is running. */
    //private GlassView mGlassView;
    private MusicPlayerView mGlassView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		//setContentView(R.layout.fragment_main);
		   
        // tell system to use the layout defined in our XML file
        //setContentView(R.layout.glass_layout);
        setContentView(R.layout.musicplayer_layout);

        // get handles to the GlassView from XML, and its GlassThread
        //mGlassView = (GlassView) findViewById(R.id.lunar);
        //mGlassThread = mGlassView.getThread();
        
        mGlassView = (MusicPlayerView) findViewById(R.id.lunar);
        mGlassThread = mGlassView.getThread();

        // give the GlassView a handle to the TextView used for messages
        mGlassView.setTextView((TextView) findViewById(R.id.text));
        
        // give the GlassView a handle to the TextView used for messages
        tv_Att = (TextView) findViewById(R.id.Att_text);
        tv_Med = (TextView) findViewById(R.id.Med_text);
        
        tv_Vel = (TextView) findViewById(R.id.Vel_text);
        tv_AmM = (TextView) findViewById(R.id.AmM_text);
        
        tv_TimeToSel = (TextView) findViewById(R.id.TimeToSel);
             
        
        if (savedInstanceState == null) {
            // we were just launched: set up a new game
           // mGlassThread.setState(GlassThread.STATE_READY);
            mGlassThread.setState(MusicPlayerThread.STATE_READY);
            //Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mGlassThread.restoreState(savedInstanceState);        	
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
              
        
	}


    	/** Invoked when the Activity loses user focus.    */
    @Override
    	protected void onPause() {
        super.onPause();
        mGlassView.getThread().pause(); // pause game when Activity pauses
        mGlassView.getThread().setRunning(false); //correctly destroy SurfaceHolder, ir          
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
	                        mGlassThread.doStart(); //start game
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

	                    At = msg.arg1;         
	                    tv_A.setText(String.valueOf(At));
	                    mGlassThread.setAttention(At);
	                    
	                    // -- display velosity based on accel_alpha [0..2.5]
	                    float vel = mGlassView.getThread().accel_alpha;
	                    if (vel>=2f) {tv_Vel.setText("4");}
	                    if (vel>=1.5f && vel<2f) {tv_Vel.setText("3");}
	                    if (vel>=1f && vel<1.5f) {tv_Vel.setText("2");}
	                    if (vel>=0.5f && vel<1f) {tv_Vel.setText("1");}
	                    if (vel<0.5f) {tv_Vel.setText("0");}                    
	                   
	                    float tts = mGlassView.getThread().TimeToSelect;
	                    if (tts < 3f && vel<=0 && mGlassView.getThread().flag_Cursor)
	                    	{tv_TimeToSel.setText(String.valueOf(Math.round(tts)) ); }
	                    else {tv_TimeToSel.setText("");}
	                    
	                    
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
	                    mGlassThread.setMeditation(Med);
	                    
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
	   
}
