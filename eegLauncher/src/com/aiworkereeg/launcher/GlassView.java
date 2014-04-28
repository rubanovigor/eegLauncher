package com.aiworkereeg.launcher;

//public class GlassView {}

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
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
class GlassView extends SurfaceView implements SurfaceHolder.Callback {
    class GlassThread extends Thread {
    	float RR; float ZZ;
    	String GameMode="b"; //1 - 2 levels of stars; 2 - 3 levels of stars
    	String flag;
    	String s6 = "6s";
        int At = 50; int Med = 50;   int ApM = 100;    int AmM = 0;
        CharSequence TGStatus;
        float alpha = 0;   float StarScale = 0.2f; float alpha1_1=0;
        float CircleRadius = 300f;   // -- for android phone
      //  float CircleRadius = 120f;		// -- for google glass
        float accel_alpha = 0f;
        double elapsed = 0;   
        float curr_alpha_obj1 = 0; float curr_alpha_obj2 = 0; float curr_alpha_obj3 = 0; float curr_alpha_obj4 = 0;
        float curr_alpha_obj5 = 0; float curr_alpha_obj6 = 0; float curr_alpha_obj7 = 0; float curr_alpha_obj8 = 0;
        int[] obj1_center; int sel_action_i; float CursorX;
        boolean flag_Cursor = false;  int CursorI = 0; int CursorJ=1;
        float CursorX_delta = 100f; float CursorY_delta = 250f;
        
        //float Graviton = 0f; float Grav_scale = 1f; float StarPosGrR = 3.0f; //3f for s3, 6f for tablet
        //float[] ar_sigm_a = new float[] {110, 90, 70, 270, 360 }; //maximum rotation angle
        // set all values to 42
        //public ArrayList<String> bci_code = new ArrayList<String>();       
        int GenBinLet1 = -1;        int GenBinLet2 = -1;  String GenLetSucc = ""; 
        
        // -- new //
        //private NaturalSkyBody Object3;
        private SkyBody Object1; 
        private SkyBody Object1_1;  private SkyBody Object1_2;  private SkyBody Object1_3; 
        private SkyBody Object2;
        private SkyBody Object3;        private SkyBody Object4;
        private SkyBody Object5;		private SkyBody Object6;
        private SkyBody Object7;		private SkyBody Object8;
        private SkyBody ObjectA; private SkyBody ObjectC; private SkyBody ObjectT; private SkyBody ObjectG;
        private SkyBody ObjectCancel; private SkyBody ObjectCursor;
        
        private float StarR;  private float R_Gr_sphere_C;  private float R_Gr_sphere_S; 
        //float Sl1_0; float Sl1_1; float Sl1_2; float Sl1_3; float Sl1_4; float Sl1_5;
        float Cx_lb_l1; float Cx_lt_l1; float Cx_rt_l1; float Cx_rb_l1;
        float Cy_lb_l1; float Cy_lt_l1; float Cy_rt_l1; float Cy_rb_l1;
        float S2P_dist_rt; float S2P_dist_lt; float S2P_dist_rb; float S2P_dist_lb; 
        float P2C_dist;
        float S2C_dist;
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
        private Drawable BackGr_Image;
        private float BackGr_ImageScaleMax = 1f;
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


        public GlassThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            Resources res = context.getResources();           
            // -- define stars //
            	// level1
            Object1 = new SkyBody(res.getDrawable(R.drawable.t_object), 1f); // image,scale
            Object1_1 = new SkyBody(res.getDrawable(R.drawable.sun3_4), 1f); // image,scale
            Object1_2 = new SkyBody(res.getDrawable(R.drawable.sun3_2), 1f); // image,scale
            Object1_3 = new SkyBody(res.getDrawable(R.drawable.sun2_3), 1f); // image,scale
            //Object2 = new SkyBody(res.getDrawable(R.drawable.sun3_3), 1f); // image,scale
            Object2 = new SkyBody(res.getDrawable(R.drawable.star_object), 1f); // image,scale
            Object3 = new SkyBody(res.getDrawable(R.drawable.c_object), 1f); // image,scale
            Object4 = new SkyBody(res.getDrawable(R.drawable.inf_object), 1f); // image,scale
            Object5 = new SkyBody(res.getDrawable(R.drawable.g_object), 1f);
            Object6 = new SkyBody(res.getDrawable(R.drawable.hash_object), 1f);
            Object7 = new SkyBody(res.getDrawable(R.drawable.cancel_object), 1f);
            Object8 = new SkyBody(res.getDrawable(R.drawable.a_object), 1f);
            
            ObjectA = new SkyBody(res.getDrawable(R.drawable.a_object), 1f); // image,scale
            ObjectC = new SkyBody(res.getDrawable(R.drawable.c_object), 1f); // image,scale
            ObjectT = new SkyBody(res.getDrawable(R.drawable.t_object), 1f); // image,scale
            ObjectG = new SkyBody(res.getDrawable(R.drawable.g_object), 1f); // image,scale
            
            ObjectCancel = new SkyBody(res.getDrawable(R.drawable.p_red), 0.3f); // image,scale
            ObjectCursor = new SkyBody(res.getDrawable(R.drawable.p_amber_1px), 5f); // image,scale
            
            
            StarR = Object1.getImageWidth()/2; // all stars has the same Radius
            StarR = StarR * StarScale;  // adopt star size to screan using scale
            //Object1.setAlpha(0f);
            
            
            
            // cache handles to our key drawables
            //BackGr_Image = context.getResources().getDrawable(R.drawable.bg_real);
            BackGr_Image = context.getResources().getDrawable(R.drawable.mars);
           
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
              //==========SkyBody===========
                //Sl1_0 = 0;  /*<-->*/ Sl1_1 = Cx_lb_l1 - StarR;  /*<-->*/ Sl1_2 = Cx_lb_l1 + StarR;
	                //Level1 - setup initial stars position/center coordinates and scale
                	// 1 left-bottom
	            Cx_lb_l1 = (float)(pX - CircleRadius) ;   // 720 -> 360
	            Cy_lb_l1 = (float)pY;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
	            Object1.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);    Object1.setScale(StarScale);
	            Object1_1.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);    Object1_1.setScale(StarScale);
	            Object1_2.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);    Object1_2.setScale(StarScale);
	            Object1_3.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);    Object1_3.setScale(StarScale);
	                
	                
	            	// 2 right-top
	            Cx_rt_l1 = (float)(pX + CircleRadius);   // 720 -> 360
                Cy_rt_l1 = (float)pY;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                Object3.setCenterCoordinates(Cx_rt_l1, Cy_rt_l1); Object3.setScale(StarScale); Object3.setAlpha(0f);
                
                	// 3 left-top
	            Cx_lt_l1 = (float)pX;   // 720 -> 360
	            Cy_lt_l1 = (float)(pY - CircleRadius);  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                Object2.setCenterCoordinates(Cx_lt_l1, Cy_lt_l1); Object2.setScale(StarScale); Object2.setAlpha(0f);	                
	                
	            	// 4 right-bottom
	            Cx_rb_l1 = (float)pX;   // 720 -> 360
                Cy_rb_l1 = (float)(pY + CircleRadius);  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                Object4.setCenterCoordinates(Cx_rb_l1, Cy_rb_l1); Object4.setScale(StarScale); Object4.setAlpha(0f);
                
                ObjectA.setCenterCoordinates(CursorX, (float)pY*2f); ObjectA.setScale(0.15f);
                ObjectC.setCenterCoordinates(CursorX, (float)pY*2f); ObjectC.setScale(0.15f);
                ObjectT.setCenterCoordinates(CursorX, (float)pY*2f); ObjectT.setScale(0.15f);
                ObjectG.setCenterCoordinates(CursorX, (float)pY*2f); ObjectG.setScale(0.15f);
                
                ObjectCursor.setCenterCoordinates(CursorX, (float)pY*2f+CursorY_delta);
                
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
                	str =  ""
                	+"accel_alpha = "+ String.valueOf(accel_alpha) + "  \n  "
                	+"BackGr_H = "+ String.valueOf(BackGr_H) + "\n"
                	//+ "rotation speed: " + String.valueOf(Math.round(accel_alpha) ) ;
                	+ "CursorI  = " + String.valueOf(CursorI) + "\n";
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
            		//canvas.rotate(0, 0, 0);
            		//canvas.rotate(alpha, (float)BackGr_W/2, (float)BackGr_H/2);
            		//canvas.scale(BackGr_ImageScale,BackGr_ImageScale, (float)BackGr_W/2 , (float)BackGr_H/2); // scale
            	//BackGr_Image.setBounds(0, (int)pY - BackGr_H, BackGr_W, (int)pY + BackGr_H);
            //BackGr_Image.setBounds(0, 0, BackGr_W, BackGr_H);
            //BackGr_Image.setBounds(0, (int)(BackGr_H/2 - BackGr_W/2 - StarR), BackGr_W, (int)(BackGr_H/2 + BackGr_W/2 + StarR))
            // -- add left/right/top/down movements adjust bounds !!!!!
            
            //BackGr_Image.setBounds(- BackGr_W/2, (int)(BackGr_H/2 - BackGr_W/2 - StarR), BackGr_W + BackGr_W/2, (int)(BackGr_H/2 + BackGr_W/2 + StarR));
            BackGr_Image.setBounds(- BackGr_W/2, (int)(BackGr_H/1.7f - BackGr_W/2 - StarR), BackGr_W + BackGr_W/2, (int)(BackGr_H/2.3f +BackGr_W/2 + StarR));
            BackGr_Image.draw(canvas);        
            canvas.restore();
            
            // draw levelel1 stars
           /* Object3.setScale(S2P_scale_rt);
            Object2.setScale(S2P_scale_lt);
            Object4.setScale(S2P_scale_rb);
            Object1.setScale(S2P_scale_lb);*/
                        
            Object1.drawTo(canvas);
            //Object1_1.drawTo(canvas); Object1_2.drawTo(canvas); Object1_3.drawTo(canvas);     
            Object2.drawTo(canvas); Object3.drawTo(canvas); Object4.drawTo(canvas);
            Object5.drawTo(canvas); Object6.drawTo(canvas); Object7.drawTo(canvas); Object8.drawTo(canvas);
         
            if (CursorI>0){
	            if (sel_action_i==1){ObjectT.drawTo(canvas);}
	            if (sel_action_i==2){ObjectG.drawTo(canvas);}
	            if (sel_action_i==3){ObjectC.drawTo(canvas);}
	            if (sel_action_i==4){ObjectA.drawTo(canvas);}
	            if (sel_action_i==5){ObjectCancel.drawTo(canvas);}
	            
            }
            ObjectCursor.drawTo(canvas);
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

            // -- parameters of the Sigmoid transfer function 
            //float[] ar_sigm_a = new float[] {110, 90, 70, 270, 360 }; //maximum rotation angle
            //float[] ar_sigm_k = new float[] {0.05f, 0.10f, 0.15f, 0.20f }; //smoothing
            //float[] ar_sigm_c = new float[] {50, 25, 75}; //point bend
            // new coordinates of shuttle based on angle and current velocity
            //V = At / V_coeff;
            
            /* -- =========== -- */
            // -- Vertical Movement
           // float accel_V = 1.5f;
            //V = (At+Med) / V_coeff;
            ApM = At+Med;           
            /*if (ApM >= 80 && ApM<=130) { pY = pY + 0; }
        	else { if (ApM <60){ pY = pY - accel_V; } 
        		else { if (ApM > 110){ pY = pY + accel_V;}
        		}                    
        	}  */
            
            
            // -- Rotational Movement defined by alpha based on Att-Med
            AmM = At-Med;
            if (accel_alpha>=2.5f) {accel_alpha = 2.5f; StarScale = 0.3f; }  // -- limit rotational speed
            if (accel_alpha>=2f && accel_alpha<=2.5f) {StarScale = 0.3f; }
            if (accel_alpha>=1.5f && accel_alpha<2.0f) {StarScale = 0.3f; }
            if (accel_alpha>=1f && accel_alpha<1.5f) {StarScale = 0.3f; }
            if (accel_alpha<=0f) {accel_alpha = 0f; StarScale = 0.3f; }
            
            
            if (Math.abs(At-Med) <= 30 && accel_alpha > 0) 
            	{ 
            		accel_alpha = accel_alpha - 0.025f ; 
            		alpha = alpha + accel_alpha; 
            	}
            else {
                if (At-Med > 30 ){ accel_alpha = accel_alpha + 0.025f ; alpha = alpha + accel_alpha; } 
                else {
                	if (At-Med < -30 ){accel_alpha = accel_alpha + 0.025f ; alpha = alpha + accel_alpha; }
                }                    
            }       
            if (alpha >=360) {alpha = alpha-360; }
                // -- reset flag if acceleration increase above 1 
            if (accel_alpha>1f && CursorI<=7) {flag_Cursor=true; }
            	
            if (CursorI == 0 && accel_alpha>0.5f) {CursorI = 1; }
            
           /* if (flag_Cursor==true && accel_alpha>1f && CursorI<7)
            	{flag_Cursor=false; 
            	 if (sel_action_i == 5 && CursorI!=0 ) 
            	 	{CursorX=CursorX-CursorX_delta; CursorI=CursorI-1;} 
            	 if (sel_action_i < 5)
            	 	{CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;}  
            	 }*/
            
            /*if (CursorI > 6 && CursorJ==1)
            	{CursorX = BackGr_W/4 + 0f; // -- new line
            	 CursorY_delta = CursorY_delta + 200f; // -- new line
            	 CursorJ=CursorJ+1; 
            	 	if (CursorJ==1){CursorI=1;} } */
            
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
            
            // -- SkyBody: rotate stars
            float[] alpha_rot = new float[] {0.0f, 0.0f, 0.8f, 0.8f, 1f, 0.9f };
            Random rs1 =new Random();
            Object1.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj1, CircleRadius, pX, pY);
            float centerX; float centerY = 0;
        	// -- calculate new center coordinates based on radius and angle
        	centerX  = (float) (pX + CircleRadius * Math.sin(Math.toRadians(curr_alpha_obj1)) );
        	centerY  = (float) (pY + CircleRadius * Math.cos(Math.toRadians(curr_alpha_obj1)) );
        	alpha1_1 = alpha1_1 + 5f;
            float CircleR1_1 = Object1.getImageWidth()/3;
            Object1_1.updatePhysics(alpha_rot[rs1.nextInt(2)], alpha1_1, CircleR1_1, centerX, centerY);
            Object1_2.updatePhysics(alpha_rot[rs1.nextInt(2)], alpha1_1+120, CircleR1_1, centerX, centerY);
            Object1_3.updatePhysics(alpha_rot[rs1.nextInt(2)], alpha1_1+240, CircleR1_1, centerX, centerY);
            
            Object2.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj2, CircleRadius, pX, pY);        
            Object3.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj3, CircleRadius, pX, pY);                
            Object4.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj4, CircleRadius, pX, pY);
            Object5.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj5, CircleRadius, pX, pY);
            Object6.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj6, CircleRadius, pX, pY);
            Object7.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj7, CircleRadius, pX, pY);            
            Object8.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj8, CircleRadius, pX, pY);
            
            
            Object1.setScale(StarScale); 
            Object1_1.setScale(0.5f*StarScale);  Object1_2.setScale(0.5f*StarScale);  Object1_3.setScale(0.5f*StarScale);
            Object2.setScale(StarScale); 	Object3.setScale(StarScale);   	Object4.setScale(StarScale); 		
            Object5.setScale(StarScale);		Object6.setScale(StarScale);
            Object7.setScale(StarScale); 		Object8.setScale(StarScale);
            /* -- =========== -- */
            float lb = 337.5f; float rb = 22.5f; float scale_k = 1; 
            if (accel_alpha <= 0 && flag_Cursor == true){            
            	if (curr_alpha_obj1 >= lb || curr_alpha_obj1 <= rb){ // -- T
            		Object1.updatePhysics(alpha_rot[rs1.nextInt(2)], alpha + 0, 0, pX, pY);
            		Object1.setScale(scale_k * StarScale); 
            		if(CursorI!=7){
	            		sel_action_i = 1;  flag_Cursor = false; ObjectT.updateDNA(CursorX, pY+CursorY_delta);
	            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
            		}
            	}
            	if (curr_alpha_obj5 >= lb || curr_alpha_obj5 <= rb){ // -- G
            		Object5.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object5.setScale(scale_k * StarScale); 
            		if(CursorI!=7){
	            		sel_action_i = 2; flag_Cursor = false; ObjectG.updateDNA(CursorX, pY+CursorY_delta);
	            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
            		}
            	}            	
            	if (curr_alpha_obj3 >= lb || curr_alpha_obj3 <= rb){  // -- C
            		Object3.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object3.setScale(scale_k *  StarScale); 
            		if(CursorI!=7){
	            		sel_action_i = 3; flag_Cursor = false; ObjectC.updateDNA(CursorX, pY+CursorY_delta);
	            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
            		}
            	}            	
            	if (curr_alpha_obj8 >= lb || curr_alpha_obj8 <= rb){  // -- A
            		Object8.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object8.setScale(scale_k * StarScale); 
            		if(CursorI!=7){
	            		sel_action_i = 4; flag_Cursor = false; ObjectA.updateDNA(CursorX, pY+CursorY_delta);
	            		CursorX=CursorX+CursorX_delta; CursorI=CursorI+1;
	            	}
            	}
            	
            	if (curr_alpha_obj6 >= lb || curr_alpha_obj6 <= rb){ 
            		Object6.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object6.setScale(scale_k * StarScale);
            		//sel_action_i = 5; flag_Cursor = true; ObjectCancel.updateDNA(CursorX-CursorX_delta, pY+CursorY_delta);
            	}            	
            	if (curr_alpha_obj2 >= lb || curr_alpha_obj2 <= rb){ 
            		Object2.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object2.setScale(scale_k * StarScale);
            		//sel_action_i = 5; flag_Cursor = false; ObjectCancel.updateDNA(CursorX-CursorX_delta, pY+CursorY_delta);
            	}            	
            	if (curr_alpha_obj7 >= lb || curr_alpha_obj7 <= rb){ // -- cancel button
            		Object7.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object7.setScale(scale_k * StarScale);
            		if(CursorI>=2){  // -- cancel not working if cursor at 1st position
	             		sel_action_i = 5;
	             		CursorX=CursorX-CursorX_delta; CursorI=CursorI-1;
	            		flag_Cursor = false; ObjectCancel.updateDNA(CursorX, pY+CursorY_delta);
            		}
            	}            	
            	if (curr_alpha_obj4 >= lb || curr_alpha_obj4 <= rb){ 
            		Object4.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 45, 0, pX, pY);
            		Object4.setScale(scale_k * StarScale);
            		//sel_action_i = 5; flag_Cursor = true; ObjectCancel.updateDNA(CursorX-CursorX_delta, pY+CursorY_delta);
            	}            	
            	
            	ObjectCursor.updateDNA(CursorX, pY+CursorY_delta);          	
                        
            	flag_Cursor = false;
            	
            	//Object2.setScale(4*StarScale);
            	//Object2.updatePhysics(alpha_rot[rs1.nextInt(1)], alpha + 180, 0, pX, pY);
            } else {Object2.setScale(StarScale);}
            
            /* -- =========== -- */
            
            
            setState(STATE_RUNNING);  //set game status and update and print message
            
            mLastTime = now;
            
        }
    }

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private GlassThread thread;

    public GlassView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new GlassThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this GlassView.
     *
     * @return the animation thread
     */
    public GlassThread getThread() {
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

