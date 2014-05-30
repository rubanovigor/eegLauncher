package com.aiworkereeg.launcher;

//public class MusicPlayerView {}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.aiworkereeg.launcher.MusicService;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;


/**
 * GlassView - main game screen
 */
class MusicPlayerView extends SurfaceView implements SurfaceHolder.Callback {
    class MusicPlayerThread extends Thread {
    	float RR; float ZZ;
    	String GameMode="b"; //1 - 2 levels of stars; 2 - 3 levels of stars
    	String flag; 
    	boolean play_flag = false; boolean stop_flag = false; boolean back_flag = false; boolean next_flag = false;
    	boolean EegLauncherFlag = true;	boolean MusicPlayerFlag = false; boolean DnaConsoleFlag = false; 
    	String s6 = "6s";
        int At = 50; int Med = 50;   int ApM = 100;    int AmM = 0;
        CharSequence TGStatus;
        float alpha = 0;  float alpha1_1=0;
        float scale_obj = 0.8f; float scale_obj_rot = 0.8f; float scale_obj_console = 0.8f;
        float CircleRadius = 1f; //280f;   // -- for android phone
      //  float CircleRadius = 120f;		// -- for google glass
        float accel_alpha = 0f;
        double elapsed = 0;   
        float curr_alpha_obj1 = 0; float curr_alpha_obj2 = 0; float curr_alpha_obj3 = 0; float curr_alpha_obj4 = 0;
        float curr_alpha_obj5 = 0; float curr_alpha_obj6 = 0; float curr_alpha_obj7 = 0; float curr_alpha_obj8 = 0;
        int[] obj1_center; int sel_action_i; float CursorX;
        boolean flag_Cursor = false;  int CursorI = 0; int CursorJ=1;
        float CursorX_delta = 1f; float CursorY_delta = 1f;      float TimeToSelect = 3f;
              
        int GenBinLet1 = -1;        int GenBinLet2 = -1;  String GenLetSucc = ""; 
        
        // -- declare objects for EEG Launcher
        private SkyBody LauncherMusicPlayer; private SkyBody LauncherDnaConsole; private SkyBody LauncherBack;
        
        // -- declare objects for Music Player
        private SkyBody mp_Stop1; //private SkyBody Object1_1;  private SkyBody Object1_2;  private SkyBody Object1_3; 
        private SkyBody mp_Play1; private SkyBody mp_Stop2; private SkyBody mp_Next;
        private SkyBody mp_Stop3; private SkyBody mp_Play2; private SkyBody mp_Play3; private SkyBody mp_Back;
        private SkyBody IconPlay; private SkyBody IconStop; private SkyBody IconSkip; 
        
        // -- declare objects for DNA Console
        private SkyBody DnaConsole_T; private SkyBody DnaConsole_rigth; private SkyBody DnaConsole_C; private SkyBody DnaConsole_Back;
        private SkyBody DnaConsole_G; private SkyBody DnaConsole_left; private SkyBody DnaConsole_Cancel; private SkyBody DnaConsole_A;
        
        private SkyBody ObjectA; private SkyBody ObjectC; private SkyBody ObjectT; private SkyBody ObjectG;
        private SkyBody ObjectCancel; private SkyBody ObjectCursor; private SkyBody ObjectCursorDel; private SkyBody ObjectClear; 
        
        
        private float StarR; private float CursorR;  private float R_Gr_sphere_C;  private float R_Gr_sphere_S; 
        //float Sl1_0; float Sl1_1; float Sl1_2; float Sl1_3; float Sl1_4; float Sl1_5;
        float Cx_lb_l1; float Cx_lt_l1; float Cx_rt_l1; float Cx_rb_l1;
        float Cy_lb_l1; float Cy_lt_l1; float Cy_rt_l1; float Cy_rb_l1;
        float S2P_dist_rt; float S2P_dist_lt; float S2P_dist_rb; float S2P_dist_lb; 
        float P2C_dist;     float S2C_dist;
        float S2P_scale_lt;  float S2P_scale_rt; float S2P_scale_rb;  float S2P_scale_lb;
        float S2P_scale_lt_l2;
        float Scale_Gr_sphere_C = 1;
        
        int Level1_flag;         int Level2_flag;         int Level3_flag;
        double P_S1l1_rr; double P_S2l1_rr; 
        double P_S1l2_rr; double P_S2l2_rr;
        double P_S1l3_rr; double P_S2l3_rr;
        
        	// -- Complex px py
        float pX_C = 0; float pY_C = 0;
        float Z_re = 0; float Z_im = 0; float Z_re_sq=0; float Z_im_sq=0;float Z_re_f=0; float Z_im_f=0;
        
               
        int V_coeff = 4; // Velocity V=At/V_coeff*/
        int ScreenFlag = 1; //control game room(screen)
        
        /* background block */
        private Drawable BackGr_Image; private Drawable BackGr_Image1; private Drawable BackGr_Image2;
        private float BackGr_ImageScaleMax = 0.7f;
        private float BackGr_ImageScale = BackGr_ImageScaleMax;
        private int BackGr_ImageScalePi = 1; //  Pi/BackGr_ImageScalePi
                  
        /** X/Y of lander center. */
        double pX;        double pY;
                
        //================================        
        /* State-tracking constants */
        public static final int STATE_LOSE = 1;       public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;      public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;

        /** UI constants (i.e. the speed & fuel bars) */
        private static final String KEY_X = "pX";      private static final String KEY_Y = "pY";

        /** Current height/width of the surface/canvas. @see #setSurfaceSize        */
        private int BackGr_H = 1;        private int BackGr_W = 1;

        /** Message handler used by thread to interact with TextView */
        private Handler mHandler;

        /** Used to figure out elapsed time between frames */
        private long mLastTime;

        /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;

        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;

        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;


        public MusicPlayerThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;   mHandler = handler;      mContext = context;
 
            
            Resources res = context.getResources();  
            // -- setup objects for EEG Launcher
            LauncherMusicPlayer = new SkyBody(res.getDrawable(R.drawable.icon_play), scale_obj);
            LauncherBack = new SkyBody(res.getDrawable(R.drawable.icon_back), scale_obj);
            LauncherDnaConsole = new SkyBody(res.getDrawable(R.drawable.a_object), scale_obj);
            
            // -- setup objects for MusicPlayer
            mp_Stop1 = new SkyBody(res.getDrawable(R.drawable.icon_stop), scale_obj); // image,scale
            mp_Play1 = new SkyBody(res.getDrawable(R.drawable.icon_play), scale_obj); // image,scale
            mp_Stop2 = new SkyBody(res.getDrawable(R.drawable.icon_stop), scale_obj); // image,scale
            mp_Next = new SkyBody(res.getDrawable(R.drawable.icon_next), scale_obj); // image,scale
            mp_Stop3 = new SkyBody(res.getDrawable(R.drawable.icon_stop), scale_obj);
            mp_Play2 = new SkyBody(res.getDrawable(R.drawable.icon_play), scale_obj);
            mp_Play3 = new SkyBody(res.getDrawable(R.drawable.icon_play), scale_obj);
            mp_Back = new SkyBody(res.getDrawable(R.drawable.icon_back), scale_obj);
            
            IconPlay = new SkyBody(res.getDrawable(R.drawable.icon_play), scale_obj_console); // image,scale
            IconStop = new SkyBody(res.getDrawable(R.drawable.icon_stop), scale_obj_console); // image,scale
            IconSkip = new SkyBody(res.getDrawable(R.drawable.icon_next), scale_obj_console); // image,scale
            
            // -- setup objects for DNA Console
            DnaConsole_T = new SkyBody(res.getDrawable(R.drawable.t_object_l), scale_obj); // image,scale
            DnaConsole_rigth = new SkyBody(res.getDrawable(R.drawable.cursor_rigth_l), scale_obj); // image,scale
            DnaConsole_C = new SkyBody(res.getDrawable(R.drawable.c_object_l), scale_obj); // image,scale
            DnaConsole_Back = new SkyBody(res.getDrawable(R.drawable.icon_back), scale_obj); // image,scale
            DnaConsole_G = new SkyBody(res.getDrawable(R.drawable.g_object_l), scale_obj);
            DnaConsole_left = new SkyBody(res.getDrawable(R.drawable.cursor_left_l), scale_obj);
            DnaConsole_Cancel = new SkyBody(res.getDrawable(R.drawable.cancel_object_l), scale_obj);
            DnaConsole_A = new SkyBody(res.getDrawable(R.drawable.a_object_l), scale_obj);
              
            ObjectA = new SkyBody(res.getDrawable(R.drawable.a_object), scale_obj_console); // image,scale
            ObjectC = new SkyBody(res.getDrawable(R.drawable.c_object), scale_obj_console); // image,scale
            ObjectT = new SkyBody(res.getDrawable(R.drawable.t_object), scale_obj_console); // image,scale
            ObjectG = new SkyBody(res.getDrawable(R.drawable.g_object), scale_obj_console); // image,scale
              
            ObjectCancel = new SkyBody(res.getDrawable(R.drawable.black), scale_obj_console); // image,scale
            ObjectCursor = new SkyBody(res.getDrawable(R.drawable.p_amber_1px), 5f); // image,scale
            ObjectCursorDel = new SkyBody(res.getDrawable(R.drawable.p_black_10px), 0.8f); // image,scale
            ObjectClear = new SkyBody(res.getDrawable(R.drawable.black), scale_obj_console); // image,scale 
              
            
            StarR = mp_Stop1.getImageWidth()/2; // all stars has the same Radius
            StarR = StarR * scale_obj;  // adopt star size to screan using scale
            CursorR = ObjectCursor.getImageWidth()/2f;           
            
            
            // cache handles to our key drawables
            BackGr_Image = context.getResources().getDrawable(R.drawable.music_player_bcgrnd);
           
        }

        /** Starts the game, setting parameters for the current difficulty.  */
        public void doStart() {
            synchronized (mSurfaceHolder) {
                Level1_flag = 0;  Level2_flag = 0;  Level3_flag = 0; 
                                               
                // shuttle1
                // pick a convenient initial location for the lander sprite
                /* ===set random initial shuttle X coordinate=== */
                pY = BackGr_H/2;   
                pX = BackGr_W/2;
                CursorX = BackGr_W/4;
                CursorY_delta = BackGr_ImageScale*BackGr_W/2;
                CircleRadius = BackGr_ImageScale*BackGr_W / 2.7f;
                CursorX_delta = StarR*2f;
              //==========SkyBody===========
                //Sl1_0 = 0;  /*<-->*/ Sl1_1 = Cx_lb_l1 - StarR;  /*<-->*/ Sl1_2 = Cx_lb_l1 + StarR;
	                //Level1 - setup initial stars position/center coordinates and scale
                	// 1 left-bottom
	            Cx_lb_l1 = (float)(pX - CircleRadius) ;   // 720 -> 360
	            Cy_lb_l1 = (float)pY;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
	            mp_Stop1.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);    mp_Stop1.setScale(scale_obj);	                
	                
	            	// 2 right-top
	            Cx_rt_l1 = (float)(pX + CircleRadius);   // 720 -> 360
                Cy_rt_l1 = (float)pY;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                mp_Stop2.setCenterCoordinates(Cx_rt_l1, Cy_rt_l1); mp_Stop2.setScale(scale_obj); mp_Stop2.setAlpha(0f);
                
                	// 3 left-top
	            Cx_lt_l1 = (float)pX;   // 720 -> 360
	            Cy_lt_l1 = (float)(pY - CircleRadius);  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                mp_Play1.setCenterCoordinates(Cx_lt_l1, Cy_lt_l1); mp_Play1.setScale(scale_obj); mp_Play1.setAlpha(0f);	                
	                
	            	// 4 right-bottom
	            Cx_rb_l1 = (float)pX;   // 720 -> 360
                Cy_rb_l1 = (float)(pY + CircleRadius);  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                mp_Next.setCenterCoordinates(Cx_rb_l1, Cy_rb_l1); mp_Next.setScale(scale_obj); mp_Next.setAlpha(0f);
                
                IconPlay.setCenterCoordinates(CursorX, (float)pY*2f); IconPlay.setScale(scale_obj_console);
                IconStop.setCenterCoordinates(CursorX, (float)pY*2f); IconStop.setScale(scale_obj_console);
                IconSkip.setCenterCoordinates(CursorX, (float)pY*2f); IconSkip.setScale(scale_obj_console);
                ObjectG.setCenterCoordinates(CursorX, (float)pY*2f); ObjectG.setScale(scale_obj_console);
                
                ObjectCursor.setCenterCoordinates((float)(4*pX), (float)pY );
                ObjectCursorDel.setCenterCoordinates((float)(4*pX), (float)pY );
                
                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }

        /** Pauses the physics update & animation.   */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
            }
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                setState(STATE_PAUSE);
               // mRotating = 0;
                pX = savedState.getDouble(KEY_X);
                pY = savedState.getDouble(KEY_Y);
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) updatePhysics();
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                if (map != null) {
                    map.putDouble(KEY_X, Double.valueOf(pX));
                    map.putDouble(KEY_Y, Double.valueOf(pY));
                }
            }
            return map;
        }
        
        /** Set current Attention.   */
        public void setAttention(int Attention) {
            synchronized (mSurfaceHolder) {
                At = Attention;
            }
        }
        
        /** Set current Meditation.  */
        public void setMeditation(int Meditation) {
            synchronized (mSurfaceHolder) {
                Med = Meditation;
            }
        }
  
        /** Set current Genome Letter in binary format   */
        public void setGenome(int G1, int G2, String GL) {
            synchronized (mSurfaceHolder) {
            	GenBinLet1 = G1;
            	GenBinLet2 = G2;
            	GenLetSucc = GL;
            }
        }
        
        /** Set current Meditation.  */
        public void setGameMode(String GM) {
            synchronized (mSurfaceHolder) {
                GameMode = GM;
                //flag = String.valueOf(GameMode);
            }
        }
        
        /** Set TG status.  */
        public void setTGStatus(CharSequence TGStatus_l) {
            synchronized (mSurfaceHolder) {
            	TGStatus = TGStatus_l;
            }
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }

        /**
         * Sets the game mode. That is, whether we are running, paused etc.
         *
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
            synchronized (mSurfaceHolder) {           	
                mMode = mode;
                CharSequence str ="";
                if (mMode == STATE_RUNNING) {
                	//str = "pY: " + String.valueOf(Math.round(pY));//ir mYOld
                	str =  "";
                //	+ "play_flag: "+ String.valueOf(play_flag) + "  \n  "
                	//+ "play_stop: "+ String.valueOf(stop_flag) + "  \n  ";
                	//+ "pY+CursorY_delta  "+ String.valueOf(pY+CursorY_delta) + "\n"
                	//+ "BackGr_H/2f: " + String.valueOf(BackGr_H/2f ) + "\n"
                	//+ "pY " + String.valueOf(pY ) + "\n";
                	//+"obj1_center[] " + String.valueOf(obj1_center[0]) + " | "+ String.valueOf(obj1_center[1]) + "\n";
                			//TGStatus;//ir
                	
                } else {
                    Resources res = mContext.getResources();      str = "";
                    
                    if (message != null) {str = message + "\n" + str; }
                  }
                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("text", str.toString());
                    b.putInt("viz", View.VISIBLE);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                //}
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                BackGr_W = width;
                BackGr_H = height;

                // don't forget to resize the background image
               // mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
            }
        }

        /** Resumes from a pause.    */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }


        /**
         * Draws the shuttle, space bodies and background to the provided Canvas.
         */
        private void doDraw(Canvas canvas) {
            // Draw the background image. Operations on the Canvas accumulate so this is like clearing the screen.
                     
            //moving one image on background
            canvas.save();
            canvas.scale(BackGr_ImageScale,BackGr_ImageScale, (float)BackGr_W/2 , (float)BackGr_H/2); // scale
            BackGr_Image.setBounds(0, (int)(BackGr_H/2f - BackGr_W/2), BackGr_W, (int)(BackGr_H/2f + BackGr_W/2));
            BackGr_Image.draw(canvas);        
            canvas.restore();   
                    
            if (EegLauncherFlag == true){
            	LauncherMusicPlayer.drawTo(canvas); LauncherDnaConsole.drawTo(canvas); LauncherBack.drawTo(canvas);
            }
            	
            if (MusicPlayerFlag == true){
	            mp_Stop1.drawTo(canvas); mp_Play1.drawTo(canvas); mp_Stop2.drawTo(canvas); mp_Next.drawTo(canvas);
	            mp_Stop3.drawTo(canvas); mp_Play2.drawTo(canvas); mp_Play3.drawTo(canvas); mp_Back.drawTo(canvas);
	            
	            // -- displey icon that was selected
	            if (CursorI>0){
		            if (sel_action_i==1){IconPlay.drawTo(canvas);}
		            if (sel_action_i==2){IconStop.drawTo(canvas);}
		            if (sel_action_i==3){IconSkip.drawTo(canvas);}	            
	            }
            }
         
            if (DnaConsoleFlag == true){
	            DnaConsole_T.drawTo(canvas); DnaConsole_rigth.drawTo(canvas); DnaConsole_C.drawTo(canvas); DnaConsole_Back.drawTo(canvas);
	            DnaConsole_G.drawTo(canvas); DnaConsole_left.drawTo(canvas); DnaConsole_Cancel.drawTo(canvas); DnaConsole_A.drawTo(canvas);
	         
	            if (CursorI>0){
		            if (sel_action_i==1){ObjectT.drawTo(canvas);}
		            if (sel_action_i==2){ObjectG.drawTo(canvas);}
		            if (sel_action_i==3){ObjectC.drawTo(canvas);}
		            if (sel_action_i==4){ObjectA.drawTo(canvas);}
		            if (sel_action_i==5){ObjectCancel.drawTo(canvas);}
		            if (sel_action_i==10){ObjectClear.drawTo(canvas);}
		            
	            }
	            ObjectCursorDel.drawTo(canvas);
	            ObjectCursor.drawTo(canvas);
            }
            
    }

        /**
         * Figures the objects state (x, y, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void updatePhysics() {
            long now = System.currentTimeMillis();

            // -- Do nothing if mLastTime is in the future.
            // -- This allows the game-start to delay the start of the physics by 100ms or whatever.
            if (mLastTime > now) return;
            // -- double elapsed = (now - mLastTime) / 1000.0;
            elapsed = (now - mLastTime) / 1000.0;
            
            /* -- =========== -- */
            ApM = At+Med;                       
            AmM = At-Med;
            	// 1s: 10 * 0.015 = 0.15 
            if (accel_alpha>=2.5f) {accel_alpha = 2.5f; scale_obj = scale_obj_rot; }  // -- limit rotational speed
            if (accel_alpha>=2f && accel_alpha<=2.5f) {scale_obj = scale_obj_rot; }
            if (accel_alpha>=1.5f && accel_alpha<2.0f) {scale_obj = scale_obj_rot; }
            if (accel_alpha>=1f && accel_alpha<1.5f) {scale_obj = scale_obj_rot; }          
            if (accel_alpha<=0f){ accel_alpha = 0f; scale_obj = scale_obj_rot;  }
            
            if (accel_alpha<=0f && CursorI != 0){ TimeToSelect = TimeToSelect - 0.015f;}
            	else {TimeToSelect = 3f;}
            	if (TimeToSelect<=0f){ TimeToSelect = 0f;}
            
            if (Math.abs(At-Med) <= 30 && accel_alpha > 0) 
            	{ 
            		accel_alpha = accel_alpha - 0.015f ; 
            		alpha = alpha + accel_alpha; 
            	}
            else {
                if (At-Med > 30 ){ accel_alpha = accel_alpha + 0.015f ; alpha = alpha + accel_alpha; } 
                else {
                	if (At-Med < -30 ){accel_alpha = accel_alpha + 0.015f ; alpha = alpha + accel_alpha; }
                }                    
            }       
            if (alpha >=360) {alpha = alpha-360; }
                // -- reset flag if acceleration increase above 1 
            if (accel_alpha>1f && CursorI<=7) {flag_Cursor=true; }
            	
            if (CursorI == 0 && accel_alpha>0.5f) {CursorI = 1; }
              
              // -- define position of 8 objects
            curr_alpha_obj1 = alpha; curr_alpha_obj2 = alpha + 180;
            curr_alpha_obj3 = alpha + 90;  curr_alpha_obj4 = alpha + 270;
            curr_alpha_obj5 = alpha + 45;  curr_alpha_obj6 = alpha + 135;
            curr_alpha_obj7 = alpha + 225; curr_alpha_obj8 = alpha + 315;
            
            if (curr_alpha_obj1 > 360) {curr_alpha_obj1=curr_alpha_obj1 - 360;}
            if (curr_alpha_obj2 > 360) {curr_alpha_obj2=curr_alpha_obj2 - 360;}
            if (curr_alpha_obj3 > 360) {curr_alpha_obj3=curr_alpha_obj3 - 360;}
            if (curr_alpha_obj4 > 360) {curr_alpha_obj4=curr_alpha_obj4 - 360;}
            if (curr_alpha_obj5 > 360) {curr_alpha_obj5=curr_alpha_obj5 - 360;}
            if (curr_alpha_obj6 > 360) {curr_alpha_obj6=curr_alpha_obj6 - 360;}
            if (curr_alpha_obj7 > 360) {curr_alpha_obj7=curr_alpha_obj7 - 360;}
            if (curr_alpha_obj8 > 360) {curr_alpha_obj8=curr_alpha_obj8 - 360;}
                     
            
            /* -- ======EEG Launcher===== -- */
            if (EegLauncherFlag == true){
	            float[] alpha_rot = new float[] {0.0f, 0.0f, 0.8f, 0.8f, 1f, 0.9f };  Random rs1 =new Random();
	            LauncherMusicPlayer.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj8, CircleRadius, pX, pY); 
	            	LauncherMusicPlayer.setScale(scale_obj);          
	            LauncherDnaConsole.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj2, CircleRadius, pX, pY); 
	            	LauncherDnaConsole.setScale(scale_obj);     
	            LauncherBack.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj3, CircleRadius, pX, pY); 
	            	LauncherBack.setScale(scale_obj);             
                             
	           float lb = 337.5f; float rb = 22.5f;  
	            if (accel_alpha <= 0 && flag_Cursor == true && TimeToSelect<=0){ 
	            	if ((curr_alpha_obj2 >= lb || curr_alpha_obj2 <= rb) && play_flag == false){ // -- Console
	            		EegLauncherFlag = false; DnaConsoleFlag = true;
	            	} 
	            	if (curr_alpha_obj3 >= lb || curr_alpha_obj3 <= rb){  // -- Back
		            	back_flag = true;
		           	}            	
	            	if (curr_alpha_obj8 >= lb || curr_alpha_obj8 <= rb){  // -- MusicPlayer
	            		MusicPlayerFlag = true; EegLauncherFlag = false;
	            	}
	            	
	            		// -- not used for now
	            	/*if (curr_alpha_obj1 >= lb || curr_alpha_obj1 <= rb){ EegLauncherFlag = false; MusicPlayerFlag = true;}
	            	if (curr_alpha_obj4 >= lb || curr_alpha_obj4 <= rb){ EegLauncherFlag = false; MusicPlayerFlag = true;} 
	            	if (curr_alpha_obj5 >= lb || curr_alpha_obj5 <= rb){ EegLauncherFlag = false; MusicPlayerFlag = true;}            		            		            	
	            	if (curr_alpha_obj6 >= lb || curr_alpha_obj6 <= rb){EegLauncherFlag = false; MusicPlayerFlag = true; }            	           	
	            	if (curr_alpha_obj7 >= lb || curr_alpha_obj7 <= rb){EegLauncherFlag = false; MusicPlayerFlag = true; }            	
           	    	       */     	
	            	flag_Cursor = false;            	
	
	            }
            }
            /* -- ======END EEG Launcher===== -- */
         
            
            /* -- ======MusicPlayer===== -- */
            if (MusicPlayerFlag == true){
	            float[] alpha_rot = new float[] {0.0f, 0.0f, 0.8f, 0.8f, 1f, 0.9f };  Random rs1 =new Random();
	            mp_Stop1.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj1, CircleRadius, pX, pY);   mp_Stop1.setScale(scale_obj);          
	            mp_Play1.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj2, CircleRadius, pX, pY);   mp_Play1.setScale(scale_obj);     
	            mp_Stop2.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj3, CircleRadius, pX, pY);   mp_Stop2.setScale(scale_obj);             
	            mp_Next.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj4, CircleRadius, pX, pY);   mp_Next.setScale(scale_obj);
	            mp_Stop3.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj5, CircleRadius, pX, pY);   mp_Stop3.setScale(scale_obj);
	            mp_Play2.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj6, CircleRadius, pX, pY);   mp_Play2.setScale(scale_obj);
	            mp_Play3.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj7, CircleRadius, pX, pY);   mp_Play3.setScale(scale_obj);
	            mp_Back.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj8, CircleRadius, pX, pY);   mp_Back.setScale(scale_obj);
	                             
	            float lb = 337.5f; float rb = 22.5f;  
	            if (accel_alpha <= 0 && flag_Cursor == true && TimeToSelect<=0){ 
	            	
	            	if (curr_alpha_obj1 >= lb || curr_alpha_obj1 <= rb){ // -- stop
	            		sel_action_i = 2; flag_Cursor = false; 
		            	IconStop.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		stop_flag = true; play_flag = false;
	            	}
	            	if (curr_alpha_obj5 >= lb || curr_alpha_obj5 <= rb){ // -- stop
	            		sel_action_i = 2; flag_Cursor = false; 
		            	IconStop.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		stop_flag = true; play_flag = false;
	            	}            	
	            	if (curr_alpha_obj3 >= lb || curr_alpha_obj3 <= rb){  // -- stop
	            		sel_action_i = 2; flag_Cursor = false; 
		            	IconStop.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		stop_flag = true; play_flag = false;
	            	}            	
	            	if (curr_alpha_obj8 >= lb || curr_alpha_obj8 <= rb){  // -- back
	            		MusicPlayerFlag = false; EegLauncherFlag = true;
		            	back_flag = true;
	            	}
	            	
	            	if ((curr_alpha_obj6 >= lb || curr_alpha_obj6 <= rb) && play_flag == false){ // -- play
	            		sel_action_i = 1; flag_Cursor = false; 
		            	IconPlay.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		play_flag = true; stop_flag = false;
	            	}            	
	            	if ((curr_alpha_obj2 >= lb || curr_alpha_obj2 <= rb) && play_flag == false){ // -- play
	            		sel_action_i = 1; flag_Cursor = false; 
		            	IconPlay.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		play_flag = true; stop_flag = false;
	            		
	            	}            	
	            	if ((curr_alpha_obj7 >= lb || curr_alpha_obj7 <= rb) && play_flag == false){ // -- play
	            		sel_action_i = 1; flag_Cursor = false; 
		            	IconPlay.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		play_flag = true; stop_flag = false;
	            	}            	
	            	if (curr_alpha_obj4 >= lb || curr_alpha_obj4 <= rb){ // -- next
	            		sel_action_i = 3; flag_Cursor = false; 
		            	IconSkip.updateDNA(CursorX, (int)(BackGr_H/2f - BackGr_W/2)-2*CursorR);
	            		next_flag = true; stop_flag = false;
	            	}            	    
	            	
	            	flag_Cursor = false;
	            	// -- for testing only
            		//DnaConsoleFlag = false; EegLauncherFlag = true;   		back_flag = true;
	            }
            }
            /* -- ======END MusicPlayer===== -- */
         
            /* -- ======DNA Console===== -- */
            if (DnaConsoleFlag == true){
	            float[] alpha_rot = new float[] {0.0f, 0.0f, 0.8f, 0.8f, 1f, 0.9f }; Random rs1 =new Random();
	            DnaConsole_T.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj1, CircleRadius, pX, pY);
	            DnaConsole_rigth.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj2, CircleRadius, pX, pY);        
	            DnaConsole_C.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj3, CircleRadius, pX, pY);                
	            DnaConsole_Back.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj4, CircleRadius, pX, pY);
	            DnaConsole_G.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj5, CircleRadius, pX, pY);
	            DnaConsole_left.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj6, CircleRadius, pX, pY);
	            DnaConsole_Cancel.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj7, CircleRadius, pX, pY);            
	            DnaConsole_A.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj8, CircleRadius, pX, pY);
	            
	            
	            DnaConsole_T.setScale(scale_obj); DnaConsole_rigth.setScale(scale_obj); DnaConsole_C.setScale(scale_obj); DnaConsole_Back.setScale(scale_obj); 		
	            DnaConsole_G.setScale(scale_obj); DnaConsole_left.setScale(scale_obj); DnaConsole_Cancel.setScale(scale_obj); DnaConsole_A.setScale(scale_obj);
	            /* -- =========== -- */
	            float lb = 337.5f; float rb = 22.5f; float scale_k = 1; 
	            if (accel_alpha <= 0 && flag_Cursor == true && TimeToSelect<=0){ 
	            	ObjectCursorDel.updateDNA(CursorX, pY-CursorY_delta-2*CursorR);  // -- delete old cursor position
	            	
	            	if (curr_alpha_obj1 >= lb || curr_alpha_obj1 <= rb){ // -- T
	            		DnaConsole_T.updatePhysics(alpha_rot[rs1.nextInt(2)], alpha + 0, 0, pX, pY);
	            		DnaConsole_T.setScale(scale_k * scale_obj); 
	            		if(CursorI!=7){
		            		sel_action_i = 1;  flag_Cursor = false; ObjectT.updateDNA(CursorX, pY-CursorY_delta-2*CursorR-StarR);
		            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
	            		}
	            	}
	            	if (curr_alpha_obj5 >= lb || curr_alpha_obj5 <= rb){ // -- G
	            		DnaConsole_G.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
	            		DnaConsole_G.setScale(scale_k * scale_obj); 
	            		if(CursorI!=7){
		            		sel_action_i = 2; flag_Cursor = false; ObjectG.updateDNA(CursorX, pY-CursorY_delta-2*CursorR-StarR);
		            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
	            		}
	            	}            	
	            	if (curr_alpha_obj3 >= lb || curr_alpha_obj3 <= rb){  // -- C
	            		DnaConsole_C.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
	            		DnaConsole_C.setScale(scale_k *  scale_obj); 
	            		if(CursorI!=7){
		            		sel_action_i = 3; flag_Cursor = false; ObjectC.updateDNA(CursorX, pY-CursorY_delta-2*CursorR-StarR);
		            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
	            		}
	            	}            	
	            	if (curr_alpha_obj8 >= lb || curr_alpha_obj8 <= rb){  // -- A
	            		DnaConsole_A.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
	            		DnaConsole_A.setScale(scale_k * scale_obj); 
	            		if(CursorI!=7){
		            		sel_action_i = 4; flag_Cursor = false; ObjectA.updateDNA(CursorX, pY-CursorY_delta-2*CursorR-StarR);
		            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
		            	}
	            	}
	            	
	            	if (curr_alpha_obj6 >= lb || curr_alpha_obj6 <= rb){ // -- hash - move cursor left
	            		if(CursorI>=2){  // -- moving cursor left not working if cursor at 1st position
		             		sel_action_i = 0;   flag_Cursor = false; CursorI=CursorI-1;
		             		CursorX=CursorX-CursorX_delta;
		             		
		            		DnaConsole_left.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
		            		DnaConsole_left.setScale(scale_k * scale_obj);
	            		}
	            	}            	
	            	if (curr_alpha_obj2 >= lb || curr_alpha_obj2 <= rb){ // -- star - move cursor right
	            		if(CursorI!=7){
		            		sel_action_i = 0; flag_Cursor = false; CursorI=CursorI+1;
		            		CursorX=CursorX+CursorX_delta; 
		            		
		            		DnaConsole_rigth.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
		            		DnaConsole_rigth.setScale(scale_k * scale_obj);
		            	}
	            	}            	
	            	if (curr_alpha_obj7 >= lb || curr_alpha_obj7 <= rb){ // -- cancel button
	            		DnaConsole_Cancel.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
	            		DnaConsole_Cancel.setScale(scale_k * scale_obj);
	            		if(CursorI>=2){  // -- cancel not working if cursor at 1st position
		             		sel_action_i = 5;
		             		CursorX=CursorX-CursorX_delta; CursorI=CursorI-1;
		            		flag_Cursor = false; ObjectCancel.updateDNA(CursorX, pY-CursorY_delta-2*CursorR-StarR);
	            		}
	            	}            	
	            	if (curr_alpha_obj4 >= lb || curr_alpha_obj4 <= rb){ // --inf - submit and clear if cursor at position 7
	            		DnaConsoleFlag = false; EegLauncherFlag = true;
	            		back_flag = true;
	            	}            	
	            	
	            	ObjectCursor.updateDNA(CursorX, pY - CursorY_delta -2*CursorR);         
	            	
	            	flag_Cursor = false;
	            	// -- for testing only
            		//DnaConsoleFlag = false; EegLauncherFlag = true;   		back_flag = true;
	            } 
            }
            /* -- ======END DNA Console===== -- */
            
            
            setState(STATE_RUNNING);  //set game status and update and print message
            
            mLastTime = now;
            
        }
    }

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private MusicPlayerThread thread;

    public MusicPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new MusicPlayerThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this MusicPlayerView.
     *
     * @return the animation thread
     */
    public MusicPlayerThread getThread() {
        return thread;
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    /**     * Installs a pointer to the text view used for messages.     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        
        
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}

