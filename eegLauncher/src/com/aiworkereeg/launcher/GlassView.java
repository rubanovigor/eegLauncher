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
        double elapsed = 0;        double V=0;        float alpha = 0;
        float Graviton = 0f; float Grav_scale = 1f; float StarPosGrR = 3.0f; //3f for s3, 6f for tablet
        //float[] ar_sigm_a = new float[] {110, 90, 70, 270, 360 }; //maximum rotation angle
        // set all values to 42
        public ArrayList<String> bci_code = new ArrayList<String>();       
        int GenBinLet1 = -1;        int GenBinLet2 = -1;
        int GenBinLet1_flag = -1;        int GenBinLet2_flag = -1;
        String ActGenSequence = "";     String GenLetSucc = "";  String ActGenSequenceBin = ""; 
        //String[] GenomeWords = new String[] {"ATG","GCT","CGT"};
        String[] GenomeLetters = new String[] {"A","C","T","G"}; 
 
        String DesGenSequence = "";  char CurrentLetter;
        int IncorrectW = 0;   int CorrectW = 0; 
        
        // -- new //
        //private NaturalSkyBody Star_RT_l1;
        private SkyBody Star_LB_l1;
        private SkyBody Star_LT_l1;
        private SkyBody Star_RT_l1;
        private SkyBody Star_RB_l1;
        private SkyBody Star_LT_l1_lb;
        private SkyBody Star_LT_l1_lt;
        private SkyBody Star_LT_l1_rb;
        private SkyBody Star_LT_l1_rt;
        private SkyBody Gr_sphere_C;        private SkyBody Gr_sphere_S;
        private SkyBody Gr_sphere_lt; private SkyBody Gr_sphere_rt; private SkyBody Gr_sphere_lb; private SkyBody Gr_sphere_rb;
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
        
        	float Cx_lt_l1_lb; float Cx_lt_l1_lt;
        	float Cy_lt_l1_lb; float Cy_lt_l1_lt;
        	float Cx_lt_l1_rb; float Cx_lt_l1_rt;
        	float Cy_lt_l1_rb; float Cy_lt_l1_rt;
        	
        int Level1_flag;         int Level2_flag;         int Level3_flag;
        float StarScale = 0.8f;
        double P_S1l1_rr; double P_S2l1_rr; 
        double P_S1l2_rr; double P_S2l2_rr;
        double P_S1l3_rr; double P_S2l3_rr;
        
        	// -- Complex px py
        float pX_C = 0; float pY_C = 0;
        float Z_re = 0; float Z_im = 0; float Z_re_sq=0; float Z_im_sq=0;float Z_re_f=0; float Z_im_f=0;
        
        	//letter of the level1
        Paint l1_Lett1_A1 = new Paint(); Paint l1_Lett1_A2 = new Paint(); Paint l1_Lett1_C = new Paint();
        Paint l1_Lett1_T = new Paint(); Paint l1_Lett1_G = new Paint();
        	//letter of the level2
        Paint l2_Lett1_A1 = new Paint(); Paint l2_Lett1_A2 = new Paint(); Paint l2_Lett1_C = new Paint();
        Paint l2_Lett1_T = new Paint(); Paint l2_Lett1_G = new Paint();
        	//letter of the level3
        Paint l3_Lett1_A1 = new Paint(); Paint l3_Lett1_A2 = new Paint(); Paint l3_Lett1_C = new Paint();
        Paint l3_Lett1_T = new Paint(); Paint l3_Lett1_G = new Paint();
        
        int L1_DesLett = 0; int L2_DesLett = 0;  int L3_DesLett = 0; 
        
        int V_coeff = 4; // Velocity V=At/V_coeff*/
        int ScreenFlag = 1; //control game room(screen)
        
        /* background block */
        private Drawable BackGr_Image;
        private float BackGr_ImageScaleMax = 1f;
        private float BackGr_ImageScale = BackGr_ImageScaleMax;
        private int BackGr_ImageScalePi = 1; //  Pi/BackGr_ImageScalePi
               
        /*collision block*/
        boolean ReachingPLanet = false;
        boolean bci_code_CometFlag = true;     
        boolean ReachingComet = false;
        
        /** Lander heading in degrees, with 0 up, 90 right. Kept in the range 0..360. */
        private double PonterHeading;

        /** X/Y of lander center. */
        double pX;        double pY;
        
        /** Pixel height of lander image. */
        private int PointerHeight;
        /** Pixel width of lander image. */
        private int PointerWidth;  
        /** What to draw for the Lander in its normal state */
        private Drawable PointerImage; private Drawable PointerImage2;
        private Drawable Pointer_grey; private Drawable Pointer_red; private Drawable Pointer_amber;
        private float PointerScale = 1f; // don't change!!!
        private float PointerR; 
        
        private float Pointer_grey_w; private float Pointer_grey_R;
        
        //================================        
        /* State-tracking constants */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;

        /** UI constants (i.e. the speed & fuel bars) */
        private static final String KEY_HEADING = "PonterHeading";
        private static final String KEY_LANDER_HEIGHT = "PointerHeight";
        private static final String KEY_LANDER_WIDTH = "PointerWidth";
        private static final String KEY_X = "pX";
        private static final String KEY_Y = "pY";

        /** Current height/width of the surface/canvas. @see #setSurfaceSize        */
        private int BackGr_H = 1;
        private int BackGr_W = 1;

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
            Star_LB_l1 = new SkyBody(res.getDrawable(R.drawable.sun3_2), 1f); // image,scale
            //Star_LT_l1 = new SkyBody(res.getDrawable(R.drawable.sun3_3), 1f); // image,scale
            Star_LT_l1 = new SkyBody(res.getDrawable(R.drawable.photo_grid), 1f); // image,scale
            Star_RT_l1 = new SkyBody(res.getDrawable(R.drawable.sun3_4), 1f); // image,scale
            Star_RB_l1 = new SkyBody(res.getDrawable(R.drawable.sun2_3), 1f); // image,scale
            	// left-top level2
           /* Star_LT_l1_lb = new SkyBody(res.getDrawable(R.drawable.sun3_2), 1f); 
            Star_LT_l1_lt = new SkyBody(res.getDrawable(R.drawable.sun3_3), 1f);
            Star_LT_l1_rb = new SkyBody(res.getDrawable(R.drawable.sun2_3), 1f);
            Star_LT_l1_rt = new SkyBody(res.getDrawable(R.drawable.sun3_4), 1f);*/
            
            Star_LT_l1_lb = new SkyBody(res.getDrawable(R.drawable.tz1), 1f); 
            Star_LT_l1_lt = new SkyBody(res.getDrawable(R.drawable.ir2), 1f);
            Star_LT_l1_rb = new SkyBody(res.getDrawable(R.drawable.ir3), 1f);
            Star_LT_l1_rt = new SkyBody(res.getDrawable(R.drawable.ar1), 1f);
            
            StarR = Star_LB_l1.getImageWidth()/2; // all stars has the same Radius
            StarR = StarR * StarScale;  // adopt star size to screan using scale
            //Star_LB_l1.setAlpha(0f);
            
            Gr_sphere_C = new SkyBody(res.getDrawable(R.drawable.t30), 0f);
            R_Gr_sphere_C = Gr_sphere_C.getImageWidth()/2;
            
            Gr_sphere_S = new SkyBody(res.getDrawable(R.drawable.t30), 0f);
            R_Gr_sphere_S = Gr_sphere_C.getImageWidth()/2;
            //R_Gr_sphere_S = R_Gr_sphere_S *2;
            
            Gr_sphere_lt = new SkyBody(res.getDrawable(R.drawable.t30), StarScale*StarPosGrR);
            Gr_sphere_rt = new SkyBody(res.getDrawable(R.drawable.t30), StarScale*StarPosGrR);
            Gr_sphere_lb = new SkyBody(res.getDrawable(R.drawable.t30), StarScale*StarPosGrR);
            Gr_sphere_rb = new SkyBody(res.getDrawable(R.drawable.t30), StarScale*StarPosGrR);
            
            // cache handles to our key drawables
            PointerImage2 = context.getResources().getDrawable(R.drawable.s2); //plain
            PointerImage = context.getResources().getDrawable(R.drawable.s1); //plain
            Pointer_grey = context.getResources().getDrawable(R.drawable.p_grey_1px);
            Pointer_red = context.getResources().getDrawable(R.drawable.p_red_1px);
            Pointer_amber = context.getResources().getDrawable(R.drawable.p_amber_1px);
            BackGr_Image = context.getResources().getDrawable(R.drawable.bg_real);
            //BackGr_Image = context.getResources().getDrawable(R.drawable.space); 
           
            // Pointer size and ridius
            PointerWidth = PointerImage.getIntrinsicWidth();
            PointerHeight = PointerImage.getIntrinsicHeight();
            PointerR = PointerScale * PointerWidth/2;        
	            // initial show-up of lander (not yet playing)
	            pX = -2*PointerR;
	            pY = -2*PointerR;
	            PonterHeading = 0;
            
	        Pointer_grey_w = Pointer_grey.getIntrinsicWidth();
	        Pointer_grey_R = Pointer_grey_w/2f;
        }

        /** Starts the game, setting parameters for the current difficulty.  */
        public void doStart() {
            synchronized (mSurfaceHolder) {
            	ReachingComet = false;
            	ReachingPLanet = false;
                Level1_flag = 0;  Level2_flag = 0;  Level3_flag = 0; 
                                               
                // shuttle1
                // pick a convenient initial location for the lander sprite
                /* ===set random initial shuttle X coordinate=== */
                //pX = BackGr_W / 2;
                pY = BackGr_H/2;   
                pX = BackGr_W/2;
               /* if (ScreenFlag == 1){
	                Random r2 =new Random();
	                int i2=r2.nextInt(6); // 6 random positions
	                pX = 4*BackGr_W / (i2+5) - PointerR;
	                //mXOld = pX; mYOld = pY; // ir, save ini position
                } else pX = pX; //dont change X coordinate
				*/
                PonterHeading = 0;
                
              //==========SkyBody===========
                //Sl1_0 = 0;  /*<-->*/ Sl1_1 = Cx_lb_l1 - StarR;  /*<-->*/ Sl1_2 = Cx_lb_l1 + StarR;
	                //Level1 - setup initial stars position/center coordinates and scale
                	// 1 left-bottom
	            Cx_lb_l1 = StarR ;   // 720 -> 360
	            Cy_lb_l1 = BackGr_H/2 + BackGr_W/2 -StarR;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
	            Star_LB_l1.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);
	            	Gr_sphere_lb.setCenterCoordinates(Cx_lb_l1, Cy_lb_l1);
	            Star_LB_l1.setScale(StarScale);
	            Star_LB_l1.setAlpha(40f);
	                //pX = Cx_lb_l1; pY = Cy_lb_l1
	                
	            	// 2 right-top
	            Cx_rt_l1 = BackGr_W - StarR;   // 720 -> 360
                Cy_rt_l1 = BackGr_H/2 - BackGr_W/2 + StarR;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                Star_RT_l1.setCenterCoordinates(Cx_rt_l1, Cy_rt_l1);
                	Gr_sphere_rt.setCenterCoordinates(Cx_rt_l1, Cy_rt_l1);
                Star_RT_l1.setScale(StarScale);
                Star_RT_l1.setAlpha(60f);
                
                	// 3 left-top
	            Cx_lt_l1 = StarR;   // 720 -> 360
	            Cy_lt_l1 = BackGr_H/2 - BackGr_W/2 + StarR;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                //if (GameMode.equalsIgnoreCase("4s")){Cx_lt_l1 = 3*BackGr_W / 10;   Cy_lt_l1 = 5*BackGr_H / 10;}
	            Star_LT_l1.setCenterCoordinates(Cx_lt_l1, Cy_lt_l1);
	            	Gr_sphere_lt.setCenterCoordinates(Cx_lt_l1, Cy_lt_l1);
	            Star_LT_l1.setScale(StarScale);
	            Star_LT_l1.setAlpha(80f);	                
	                
	            	// 4 right-bottom
	            Cx_rb_l1 = BackGr_W - StarR;   // 720 -> 360
                Cy_rb_l1 = BackGr_H/2 + BackGr_W/2 -StarR;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
                //if (GameMode.equalsIgnoreCase("4s")){Cx_rb_l1 = 7*BackGr_W / 10;   Cy_rb_l1 = 5*BackGr_H / 10;}
                Star_RB_l1.setCenterCoordinates(Cx_rb_l1, Cy_rb_l1);
            		Gr_sphere_rb.setCenterCoordinates(Cx_rb_l1, Cy_rb_l1);
                Star_RB_l1.setScale(StarScale);
                Star_RB_l1.setAlpha(100f);
                
            		// Left-Top - level2
                	// 1 left-bottom
	            Cx_lt_l1_lb = Cx_lt_l1;   // 720 -> 360
	            Cy_lt_l1_lb = Cy_lt_l1 - StarR;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
	            Star_LT_l1_lb.setCenterCoordinates(Cx_lt_l1_lb, Cy_lt_l1_lb);
	            Star_LT_l1_lb.setScale(0f);
	            Star_LT_l1_lb.setAlpha(50f);	                
	                // 2 left-top
	            Cx_lt_l1_lt = Cx_lt_l1;   // 720 -> 360
	            Cy_lt_l1_lt = Cy_lt_l1 + StarR;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
	            Star_LT_l1_lt.setCenterCoordinates(Cx_lt_l1_lt, Cy_lt_l1_lt);
	            Star_LT_l1_lt.setScale(0f);
	            Star_LT_l1_lt.setAlpha(60f);
	                // 3 right-bottom
	            Cx_lt_l1_rb = Cx_lt_l1 - StarR;   // 720 -> 360
	            Cy_lt_l1_rb = Cy_lt_l1;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
	            Star_LT_l1_rb.setCenterCoordinates(Cx_lt_l1_rb, Cy_lt_l1_rb);
	            Star_LT_l1_rb.setScale(0f);
	            Star_LT_l1_rb.setAlpha(70f);
		            // 4 right-top
		        Cx_lt_l1_rt = Cx_lt_l1 + StarR;   // 720 -> 360
		        Cy_lt_l1_rt = Cy_lt_l1;  // 1280 -> 160(1/8); 320(1/4); 640(1/2); 
		        Star_LT_l1_rt.setCenterCoordinates(Cx_lt_l1_rt, Cy_lt_l1_rt);
		        Star_LT_l1_rt.setScale(0f);
		        Star_LT_l1_rt.setAlpha(80f);
                                  
		        	// -- calculate scale of gravitational sphere
	            S2C_dist = Math.round(Math.sqrt( Math.pow(BackGr_H/2 - Cy_rt_l1, 2)
	            							   + Math.pow(BackGr_W/2 - Cx_rt_l1,2) ) );
	            Scale_Gr_sphere_C = (2f/3f)*S2C_dist/(Gr_sphere_C.getImageWidth()/2);
	            R_Gr_sphere_C = 2f/3f*S2C_dist; 
	            R_Gr_sphere_S = Scale_Gr_sphere_C * (Gr_sphere_C.getImageWidth()/2);
                	// -- gravitational spheres
		        Gr_sphere_C.setCenterCoordinates(BackGr_W/2, BackGr_H/2);
		        Gr_sphere_S.setCenterCoordinates(BackGr_W/2, BackGr_H/2);
		        
		        if (ScreenFlag == 2){
		        	/*Star_LT_l1.setImage(mContext.getResources().getDrawable(R.drawable.tz1));
		        	Star_LB_l1.setImage(mContext.getResources().getDrawable(R.drawable.ir2));
		        	Star_RT_l1.setImage(mContext.getResources().getDrawable(R.drawable.ir3));
		        	Star_RB_l1.setImage(mContext.getResources().getDrawable(R.drawable.ar1));*/
		        }
		        
		        if (ScreenFlag == 3){
		        	/*Star_LT_l1.setImage(mContext.getResources().getDrawable(R.drawable.aiworker_logo));
		        	Star_LB_l1.setImage(mContext.getResources().getDrawable(R.drawable.aiworker_logo));
		        	Star_RT_l1.setImage(mContext.getResources().getDrawable(R.drawable.aiworker_logo));
		        	Star_RB_l1.setImage(mContext.getResources().getDrawable(R.drawable.aiworker_logo));*/
		        }
		        
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
                PonterHeading = savedState.getDouble(KEY_HEADING);
                PointerWidth = savedState.getInt(KEY_LANDER_WIDTH);
                PointerHeight = savedState.getInt(KEY_LANDER_HEIGHT);
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
                    map.putDouble(KEY_HEADING, Double.valueOf(PonterHeading));
                    map.putInt(KEY_LANDER_WIDTH, Integer.valueOf(PointerWidth));
                    map.putInt(KEY_LANDER_HEIGHT, Integer.valueOf(PointerHeight));
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
                	+ "ZZ = "+String.valueOf(Math.round(ZZ)) + "\n";
                	/*+ "pX = "+String.valueOf(Math.round(pX)) + "   --  "
                	+ "pY = "+String.valueOf(Math.round(pY)) + "\n"
                	+ "Z_re = "+String.valueOf(Z_re) + "\n"
                	+ "Z_im = "+String.valueOf(Z_im) + "\n"
                	+ "Z_re_sq = " + String.valueOf(Z_re_sq) + "\n";*/
                	/*+ "ScreenFlag "+String.valueOf(ScreenFlag)+ "\n "*/
                	//+ "StarR "+String.valueOf(Math.round(StarR))+ "\n "
                	//+"pX = "+ String.valueOf(Math.round(pX)) + "\n"
                	/*+"Graviton  = " + String.valueOf(Graviton) + "\n"
                	+"P2C_dist  = " + String.valueOf(P2C_dist) + "\n";*/
                			/*+ "Word:  " + DesGenSequence.charAt(0)+ "-"
                			+ DesGenSequence.charAt(1)+ "-"
                			+ DesGenSequence.charAt(2)+ "-"
                			+ Character.toString(DesGenSequence.charAt(0)) + "\n";*/
                			/*+ "C2C:  " + String.valueOf(Math.round(C2C - StarR-PointerR)) + "\n"
                			+ "C4C:  " + String.valueOf(Math.round(C4C - StarR-PointerR)) + "\n"
                			+ "P_S1l1_rr:  " + String.valueOf(Math.round(P_S1l1_rr - StarR-PointerR)) + "\n"
                			+ "P_S2l1_rr:  " + String.valueOf(Math.round(P_S2l1_rr - StarR-PointerR)) + "\n"
                			+ "pY:  " + String.valueOf(Math.round(pY)) + "\n"
                			+ "Cy_lt_l1:  " + String.valueOf(Math.round(Cy_lr_l1)) + "\n";*/
 
                			//TGStatus;//ir
                	
                } else {
                    Resources res = mContext.getResources();      str = "";
                    
                    /*if (mMode == STATE_READY)
                        str = res.getText(R.string.mode_ready);
                    else if (mMode == STATE_PAUSE)
                        str = res.getText(R.string.mode_pause);
                    else if (mMode == STATE_LOSE)
                        str = res.getText(R.string.mode_lose);
                    else if (mMode == STATE_WIN)
                        str = res.getString(R.string.mode_win_prefix)
                                //+ mWinsInARow + " "
                                + res.getString(R.string.mode_win_suffix);*/

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
            //canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            int yTop = BackGr_H - ((int) pY + (int)PointerR);
            int xLeft = (int) pX - (int)PointerR;
               
            if (GameMode.equalsIgnoreCase("6s"))
        	{
            //moving one image on background
            canvas.save();
            		//canvas.rotate(0, 0, 0);
            		//canvas.rotate(alpha, (float)BackGr_W/2, (float)BackGr_H/2);
            		//canvas.scale(BackGr_ImageScale,BackGr_ImageScale, (float)BackGr_W/2 , (float)BackGr_H/2); // scale
            	//BackGr_Image.setBounds(0, (int)pY - BackGr_H, BackGr_W, (int)pY + BackGr_H);
            //BackGr_Image.setBounds(0, 0, BackGr_W, BackGr_H);
            //BackGr_Image.setBounds(0, (int)(BackGr_H/2 - BackGr_W/2 - StarR), BackGr_W, (int)(BackGr_H/2 + BackGr_W/2 + StarR))
            // -- add left/right/top/down movements adjust bounds !!!!!
            BackGr_Image.setBounds(- BackGr_W/2, (int)(BackGr_H/2 - BackGr_W/2 - StarR), BackGr_W + BackGr_W/2, (int)(BackGr_H/2 + BackGr_W/2 + StarR));
            BackGr_Image.draw(canvas);        
            canvas.restore();
            
            // draw levelel1 stars
            Star_RT_l1.setScale(S2P_scale_rt);
            Star_LT_l1.setScale(S2P_scale_lt);
            Star_RB_l1.setScale(S2P_scale_rb);
            Star_LB_l1.setScale(S2P_scale_lb);
                        
            Gr_sphere_rt.setScale(Scale_Gr_sphere_C);            Gr_sphere_rt.drawTo(canvas);
            Gr_sphere_lt.setScale(Scale_Gr_sphere_C);   		 Gr_sphere_lt.drawTo(canvas);
            Gr_sphere_rb.setScale(Scale_Gr_sphere_C);            Gr_sphere_rb.drawTo(canvas);
            Gr_sphere_lb.setScale(Scale_Gr_sphere_C);            Gr_sphere_lb.drawTo(canvas);
            
            Star_LB_l1.drawTo(canvas);     
            Star_RT_l1.drawTo(canvas); 
            	
            Star_LT_l1.drawTo(canvas);  
            Star_RB_l1.drawTo(canvas);
            
            	// draw starts of level2 of the left-top star (level1)
            Star_LT_l1_lb.drawTo(canvas);    
            Star_LT_l1_lt.drawTo(canvas);
            Star_LT_l1_rb.drawTo(canvas);    
            Star_LT_l1_rt.drawTo(canvas);
            
            	// -- draw Gravity spheres
            Gr_sphere_S.drawTo(canvas);
            
            Gr_sphere_C.setScale(Scale_Gr_sphere_C);
            Gr_sphere_C.drawTo(canvas);

                        
            // draw pointer 
            canvas.save();
            canvas.rotate((float) PonterHeading, (float) pX, BackGr_H - (float) pY);
            canvas.scale(PointerScale, PointerScale, (float) pX, BackGr_H - (float) pY); // scale
            //canvas.scale(PointerScale, PointerScale, (float) pX - PointerR, BackGr_H - (float) pY + PointerR); // scale
            if (P2C_dist < R_Gr_sphere_C){
            	PointerImage2.setBounds(xLeft, yTop, xLeft + 2*(int)PointerR, yTop + 2*(int)PointerR);
            	PointerImage2.draw(canvas);
            } else {
            	PointerImage.setBounds(xLeft, yTop, xLeft + 2*(int)PointerR, yTop + 2*(int)PointerR);
            	PointerImage.draw(canvas);
            }
            canvas.restore();
        	}
            
            if (GameMode.equalsIgnoreCase("4s"))
        	{
                //int y1Top = BackGr_H - ((int) pY + (int)Pointer_grey_R);
                //int x1Left = (int) pX - (int)Pointer_grey_R;
            	//int y1Top =  BackGr_H/2 + 200 - (2*(At+Med) + (int)Pointer_grey_R);
            	//int x1Left = BackGr_W/2 + 2*(At-Med) - (int)Pointer_grey_R;
            	int y1Top =  BackGr_H/2 + 200 - (4*(At) + (int)Pointer_grey_R);
            	int x1Left = -200 + BackGr_W/2 + 4*(Med) - (int)Pointer_grey_R;
            	RR = (float) Math.sqrt(At*At + Med*Med) ;
            	ZZ = 1000*(float) (Math.sin(RR)/RR);
                // -- background
                canvas.save();
                BackGr_Image.setBounds(- BackGr_W/2, (int)(BackGr_H/2 - BackGr_W/2 - StarR), BackGr_W + BackGr_W/2, (int)(BackGr_H/2 + BackGr_W/2 + StarR));
                //BackGr_Image.draw(canvas);        
                canvas.restore();	
                
                // draw pointer grey
                canvas.save();
                //canvas.rotate((float) PonterHeading, (float) pX, BackGr_H - (float) pY);
                //canvas.scale(PointerScale, PointerScale, (float) pX, BackGr_H - (float) pY); // scale
                Pointer_grey.setBounds(x1Left, y1Top, x1Left + 2*(int)Pointer_grey_R, y1Top + 2*(int)Pointer_grey_R);
                Pointer_grey.draw(canvas);
                canvas.restore();
                
                canvas.save();
                if (Math.abs(ZZ) <= 2) {
	                // draw pointer red
	                Pointer_red.setBounds(x1Left, y1Top, x1Left + 2*(int)Pointer_grey_R, y1Top + 2*(int)Pointer_grey_R);
	                Pointer_red.draw(canvas);          
                }
                if (ZZ > 2) {
	                // draw pointer red
	                Pointer_amber.setBounds(x1Left, y1Top, x1Left + 2*(int)Pointer_grey_R, y1Top + 2*(int)Pointer_grey_R);
	                Pointer_amber.draw(canvas);
                }
                canvas.restore();   
                
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

            // -- parameters of the Sigmoid transfer function 
            //float[] ar_sigm_a = new float[] {110, 90, 70, 270, 360 }; //maximum rotation angle
            //float[] ar_sigm_k = new float[] {0.05f, 0.10f, 0.15f, 0.20f }; //smoothing
            //float[] ar_sigm_c = new float[] {50, 25, 75}; //point bend
            // new coordinates of shuttle based on angle and current velocity
            //V = At / V_coeff;
            
            /* -- =========== -- */
            // -- Vertical Movement
            float accel_V = 1.5f;
            //V = (At+Med) / V_coeff;
            ApM = At+Med;           
            if (ApM >= 80 && ApM<=130) { pY = pY + 0; }
        	else { if (ApM <60){ pY = pY - accel_V; } 
        		else { if (ApM > 110){ pY = pY + accel_V;}
        		}                    
        	}  
            
            
            // -- Horizontal Movement defined by alpha based on Att-Med
            float accel_alpha = 1.5f;
            AmM = At-Med;
           /* if (Math.abs(At-Med) <= 20) { alpha = alpha + 0; }
            else {if (At-Med > 20 && alpha > -80){ alpha = alpha - accel_alpha; } 
                else {if (At-Med < -20 && alpha < 80){ alpha = alpha + accel_alpha; } }  }  */
            
            if (Math.abs(At-Med) <= 20) { pX = pX + 0; }
            else {
                if (At-Med > 20 && alpha > -80){ pX = pX - accel_alpha; } 
                else {
                	if (At-Med < -20 && alpha < 80){ pX = pX + accel_alpha; }
                	//if (At-Med < -2 && alpha < 80){ pX = pX + accel_alpha; }
                }                    
            }       
            
 //           S2C_dist = Math.round(Math.sqrt( Math.pow(BackGr_H/2 - Cy_rt_l1, 2) + Math.pow(BackGr_W/2 - Cx_rt_l1,2) ) );
            //R_Gr_sphere_C = 2/3*S2C_dist;
   //         Scale_Gr_sphere_C = (2f/3f)*S2C_dist/R_Gr_sphere_C;
            //R_Gr_sphere_S = R_Gr_sphere_C;
            // -- pointer2center distance
	        P2C_dist = Math.round(Math.sqrt( Math.pow(BackGr_H/2 - (BackGr_H - pY), 2) + Math.pow(BackGr_W/2 - pX,2) ) );
        	// -- calculate Gravity based on the pointer position and TAN transfer function
	        // add coeficient to the TANH an scale it to [-1;0]
	        //norm=((data_of each column)-min(data_of each column))/(max(data_of
	        	//	each column)-min(data_of each column))
	        //Graviton  = Math.tanh(Math.toRadians(P2C_dist));
	        //Graviton  = 1f - (P2C_dist - 0)/(R_Gr_sphere_C - 0);
	        
	        // -- apply central sphere gravity according to the pointer position
            if (P2C_dist < R_Gr_sphere_C && pY>BackGr_H/2 && P2C_dist != 0f) 
            	{ Graviton  = 1f - (P2C_dist - 0)/(R_Gr_sphere_C - 0); pY = pY - Graviton/Grav_scale; }
            if (P2C_dist < R_Gr_sphere_C && pY<BackGr_H/2 && P2C_dist != 0f)
            	{ Graviton  = 1f - (P2C_dist - 0)/(R_Gr_sphere_C - 0); pY = pY + Graviton/Grav_scale; }
            
            if (P2C_dist < R_Gr_sphere_C && pX>BackGr_W/2 && P2C_dist != 0f)
            	{ Graviton  = 1f - (P2C_dist - 0)/(R_Gr_sphere_C - 0); pX = pX - Graviton/Grav_scale; }
            if (P2C_dist < R_Gr_sphere_C && pX<BackGr_W/2 && P2C_dist != 0f) 
            	{ Graviton  = 1f - (P2C_dist - 0)/(R_Gr_sphere_C - 0); pX = pX + Graviton/Grav_scale; }           
	                        
            
           // pX = pX + V * (float) Math.sin(Math.toRadians(alpha))*elapsed;
            //pY = pY + V * (float) Math.cos(Math.toRadians(alpha))*elapsed;
            PonterHeading = alpha;
            
            /* -- =========== -- */
            // -- checking if pointer reach left/right border of the screen
            double delatYShuttle1Border = BackGr_H/2 - (pY);
            
            	// -- checking if pointer is reaching the right border
            if (pX >= BackGr_W && delatYShuttle1Border >= 0) { pX = BackGr_W/2; pY = BackGr_H/2; }
            else {
            	if (pX >= BackGr_W && delatYShuttle1Border < 0) {
            		pX = BackGr_W/2;  pY = BackGr_H/2; 
                } else 
                // checking if pointer is reaching the left border
		            if (pX<=0 && delatYShuttle1Border >= 0) {
		            	pX = BackGr_W/2; pY = BackGr_H/2;
		            } else {
		            	if (pX<=0 && delatYShuttle1Border < 0) {
		            		pX = BackGr_W/2; pY = BackGr_H/2;
		                } 
		            }
            }
                    
            /* -- =========== -- */
            // right-top star2pointer distance
            S2P_dist_rt = Math.round(Math.sqrt( Math.pow(Cy_rt_l1 - (BackGr_H - pY), 2) + Math.pow(Cx_rt_l1 - pX,2) ) );
            	// -- dynamic (based on star2pointer distance) scaling of right-top star
            S2P_scale_rt = (S2P_dist_rt - 0)/(BackGr_W/2 - 0);
	            if (S2P_dist_rt < StarR + PointerR){ 
	            	S2P_scale_rt = 0; pX = BackGr_W/2; pY = BackGr_H/2; ScreenFlag = 3; doStart(); } 
	            if (S2P_dist_rt > BackGr_W/2-StarR + PointerR){ S2P_scale_rt = StarScale; }
	        		// -- positive gravitation to star
	            if (S2P_dist_rt < StarPosGrR*StarR + PointerR)
	            	{ Graviton  = 2f - (S2P_dist_rt - 0)/(R_Gr_sphere_S - 0);
	            	  pX = pX + Graviton;  pY = pY + Graviton; } 
	        
	        // right-bottom star2pointer distance
	        S2P_dist_rb = Math.round(Math.sqrt( Math.pow(Cy_rb_l1 - (BackGr_H - pY), 2) + Math.pow(Cx_rb_l1 - pX,2) ) );
	          	// -- dynamic (based on star2pointer distance) scaling of right-top star
	        S2P_scale_rb = (S2P_dist_rb - 0)/(BackGr_W/2 - 0);
		        if (S2P_dist_rb < StarR + PointerR){ 
		        	S2P_scale_rb = 0; pX = BackGr_W/2; pY = BackGr_H/2; ScreenFlag = 3; doStart(); } 
		        if (S2P_dist_rb > BackGr_W/2-StarR + PointerR){ S2P_scale_rb = StarScale; }
		        	// -- positive gravitation to star
		        if (S2P_dist_rb < StarPosGrR*StarR + PointerR)
		        	{  Graviton  = 2f - (S2P_dist_rb - 0)/(R_Gr_sphere_S - 0);
		        	   pX = pX + Graviton;  pY = pY - Graviton; } 
		        		        
		    // left-bottom star2pointer distance
		    S2P_dist_lb = Math.round(Math.sqrt( Math.pow(Cy_lb_l1 - (BackGr_H - pY), 2) + Math.pow(Cx_lb_l1 - pX,2) ) );
		        // -- dynamic (based on star2pointer distance) scaling of right-top star
		    S2P_scale_lb = (S2P_dist_lb - 0)/(BackGr_W/2 - 0);
			        if (S2P_dist_lb < StarR + PointerR){ 
			        	S2P_scale_lb = 0; pX = BackGr_W/2; pY = BackGr_H/2; ScreenFlag = 3; doStart(); } 
			        if (S2P_dist_lb > BackGr_W/2-StarR + PointerR){ S2P_scale_lb = StarScale; }	 
		        	// -- positive gravitation to star
		        if (S2P_dist_lb < StarPosGrR*StarR + PointerR)
		        	{  Graviton  = 2f - (S2P_dist_lb - 0)/(R_Gr_sphere_S - 0);
		        	   pX = pX - Graviton;  pY = pY - Graviton; }
		        
            // left-top star2pointer distance
            S2P_dist_lt = Math.round(Math.sqrt( Math.pow(Cy_lt_l1 - (BackGr_H - pY), 2) + Math.pow(Cx_lt_l1 - pX,2) ) );           
            	// -- dynamic (based on star2pointer distance) scaling of left-top star
            S2P_scale_lt = (S2P_dist_lt - 0)/(BackGr_W/2 - 0);
	            if (S2P_dist_lt < StarR + PointerR){
	            	S2P_scale_lt = 0; pX = BackGr_W/2; pY = BackGr_H/2; ScreenFlag = 2; doStart(); } 
	            if (S2P_dist_lt > BackGr_W/2-StarR + PointerR){ S2P_scale_lt = StarScale;}
	        		// -- positive gravitation to star
	            if (S2P_dist_lt < StarPosGrR*StarR + PointerR)
	            	{  Graviton  = 2f - (S2P_dist_lt - 0)/(R_Gr_sphere_S - 0);
	            	   pX = pX - Graviton;  pY = pY + Graviton; } 
	         
	        // =================================
		        // -- NEW!!!! fractal drawing   
	            // C = x + i*y
	            // Z = Z*Z + C;
	            float Re_Max = 200f; float Re_Min = -200f; float Im_Max = 200f; float Im_Min = -200f;
	            pX = BackGr_W/2; pY = BackGr_H/2;
	            // -- convert to complex
	            //pX_C = (float) (pX/BackGr_W * (Re_Max - Re_Min) + Re_Min);
	            //pY_C = (float) (pY/BackGr_W * (Im_Min - Im_Max) + Im_Max);
	            //pX_C = (float) pX;       pY_C = (float) pY;
	            // (x+yi)(u+vi) = (xu-yv)+(xv+yu)i
	            
	            // --sqr(Z = Z_re + Z_im)	            
	            Z_re_sq = Z_re*Z_re - Z_im*Z_im;
	            Z_im_sq = Z_re*Z_im + Z_im*Z_re;
	            
	            // -- Z = (Z_re_sq+i Z_im_sq) + (px_C + i pY_C);
	            // -- Z = sqr(Z)+ C
	            
	           // Z_re_f = (float)(Z_re_sq + (float)(pX)); 
	            Z_re_f = 5f + (float)(pX);
	            Z_im_f = Z_im_sq + (float)(pY);
	            
	            Z_re = Z_re_f; Z_im = Z_im_f;
	            
	            // -- convert back to real
	            //pX = BackGr_W *(Z_re_f - Re_Min)/(Re_Max - Re_Min);
	            //pY = BackGr_W *(Im_Max - Z_im_f)/(Im_Max - Im_Min);    
	            pX=Z_re_f; pY=Z_im_f;
	       // =================================
	            
            		// -- dynamic scaling of level2 of left-top star
            S2P_scale_lt_l2 = 0.5f*(StarScale - S2P_scale_lt);
	        	Star_LT_l1_lb.setScale(S2P_scale_lt_l2);    
	            Star_LT_l1_lt.setScale(S2P_scale_lt_l2);
	            Star_LT_l1_rb.setScale(S2P_scale_lt_l2);    
	            Star_LT_l1_rt.setScale(S2P_scale_lt_l2);
            
            // -- SkyBody: rotate stars
            float[] alpha_rot = new float[] {0.5f, 0.6f, 0.8f, 0.8f, 1f, 0.9f };
            Random rs1 =new Random();
            Star_LB_l1.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            Star_RT_l1.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            Star_LT_l1.updatePhysics(alpha_rot[rs1.nextInt(6)]);            
            Star_RB_l1.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            Star_LT_l1_lb.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            Star_LT_l1_lt.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            Star_LT_l1_rb.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            Star_LT_l1_rt.updatePhysics(alpha_rot[rs1.nextInt(6)]);
            /* -- =========== -- */
            
            //DesGenSequence.charAt(0);
            //check collision
            //Level1
            //P_S1l1_rr = Math.sqrt(Math.pow((BackGr_H-pY-Cy_lb_l1),2) + Math.pow((pX-Cx_lb_l1),2) );
            //P_S2l1_rr = Math.sqrt(Math.pow((BackGr_H-pY-Cy_rt_l1),2) + Math.pow((pX-Cx_rt_l1),2) );            		
            
            
        	// -- scaling of the background
            /*if (pY >0 && pY < 2*BackGr_H/3) 
            	BackGr_ImageScale = (float)(BackGr_ImageScaleMax*Math.cos(pY*(Math.PI/BackGr_ImageScalePi)/640));
            else if (pY >= 2*BackGr_H/3)	BackGr_ImageScale=BackGr_ImageScale;*/
            
            
            // -- checking if pointer reaching upper border of the working part of the screen
            //if (pY>BackGr_H) {
            if (pY>BackGr_H - BackGr_W/2 + StarR) { ScreenFlag = 3; doStart();   }
            if (pY<BackGr_W/2 - StarR) { ScreenFlag = 3; doStart();   } 
            /* -- =========== -- */
                     
            setState(STATE_RUNNING);  //set game status and update and print message
            
            // -- stop game when reaching Earth
            /*if (ScreenFlag == 9){	doStart(); V = 0; //prevent Shuttle1 from moving   ScreenFlag +=1; }*/
            
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

