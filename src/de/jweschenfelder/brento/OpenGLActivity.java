package de.jweschenfelder.brento;

import java.io.InputStream;
import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class OpenGLActivity extends Activity {

	// Used to handle pause and resume...
	private static OpenGLActivity master = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(50, 50, 100);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;
	
	private Object3D brentRightFoot = null;
	private Object3D brentLeftFoot = null;
	private Object3D brentLeftHand = null;
	private Object3D brentRightHand = null;
	private Object3D brentFace = null;
	private Object3D brentMouth = null;
	private Object3D brentHair = null;
	private Object3D brentRightEye = null;
	private Object3D brentLeftEye = null;
	private Object3D brentNose = null;
	private Object3D brentBody = null;
	private Object3D brentParts[] = null;
	private Object3D brentHead = null;
	private float scale = 0;
	private int fps = 0;
	
	private boolean firstStart = true;
	private final static int NOTHING = 0;
	private final static int WALKING = 1;
	private final static int JUMPING = 2;
	private final static int WAVING = 3;
	private final static int SHAKING = 4;
	private final static int NODDING = 5;
	private final static int STOPPING = 6;
	private int curAction = NOTHING;
	private int newAction = NOTHING;
	
	private Light sun = null;
	private Object3D lamp = null;
	private Camera cam = null;

	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLContextClientVersion(2);
		
		/*
		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});*/

		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
		
		TableLayout tbl = new TableLayout(this);
		tbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		TableRow tr1 = new TableRow(this);
		TableRow tr2 = new TableRow(this);
		TableRow tr3 = new TableRow(this);
		TableRow tr4 = new TableRow(this);
		tr1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		Button b1 = new Button(this);
		b1.setText("Walk");
		b1.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	firstStart = true;
		    	newAction = WALKING;
		    	if (curAction == NOTHING) {
		    		curAction = newAction;
		    	}
		    }
		});
		Button b2 = new Button(this);
		b2.setText("Jump");
		b2.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	firstStart = true;
		    	newAction = JUMPING;
		    	if (curAction == NOTHING) {
		    		curAction = newAction;
		    	}
		    }
		});
		Button b3 = new Button(this);
		b3.setText("Wave");
		b3.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	firstStart = true;
		    	newAction = WAVING;
		    	if (curAction == NOTHING) {
		    		curAction = newAction;
		    	}
		    }
		});
		Button b4 = new Button(this);
		b4.setText("Shake");
		b4.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	firstStart = true;
		    	newAction = SHAKING;
		    	if (curAction == NOTHING) {
		    		curAction = newAction;
		    	}
		    }
		});
		Button b5 = new Button(this);
		b5.setText("Nod");
		b5.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	firstStart = true;
		    	newAction = NODDING;
		    	if (curAction == NOTHING) {
		    		curAction = newAction;
		    	}
		    }
		});
		Button b6 = new Button(this);
		b6.setText("Stop");
		b6.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	firstStart = true;
		    	newAction = STOPPING;
		    	if (curAction == NOTHING) {
		    		curAction = newAction;
		    	}
		    }
		});
		Button b7 = new Button(this);
		b7.setText("Quit");
		b7.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	//Quit - ToDo
		    	finish();
		    }
		});
		tr1.addView(b1);
		tr1.addView(b2);
		tr2.addView(b3);
		tr2.addView(b4);
		tr3.addView(b5);
		tr3.addView(b6);
		tr4.addView(b7);
		tr4.setPadding(0, 15, 0, 0);
		tbl.addView(tr1);
		tbl.addView(tr2);
		tbl.addView(tr3);
		tbl.addView(tr4);
		tbl.setGravity(Gravity.TOP | Gravity.LEFT);
		addContentView(tbl, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		firstStart = true;
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean onTouchEvent(MotionEvent me) {

		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			touchTurn = xd / -100f;
			touchTurnUp = yd / -100f;
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

		return super.onTouchEvent(me);
	}

	protected boolean isFullscreenOpaque() {
		return true;
	}

	class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();

		private final int ffMax = 120;
		private int ffInd = 0;

		public MyRenderer() {
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}
			fb = new FrameBuffer(w, h);

			if (master == null) {

				world = new World();
				world.setAmbientLight(20, 20, 20);
 
				sun = new Light(world);
				sun.setIntensity(255, 255, 255);

				InputStream ser = getResources().openRawResource(R.raw.sbrent);
				scale = 5.0f;
				
				brentParts = Loader.loadSerializedObjectArray(ser);
				brentRightFoot = brentParts[0];
				brentLeftFoot = brentParts[1];
				brentLeftHand = brentParts[2];
				brentRightHand = brentParts[3];
				brentFace = brentParts[4];
				brentMouth = brentParts[5];
				brentHair = brentParts[6];
				brentRightEye = brentParts[7];
				brentLeftEye = brentParts[8];
				brentNose = brentParts[9];
				brentBody = brentParts[10];
				brentHead = Object3D.mergeObjects(brentLeftEye, brentRightEye);
				brentHead = Object3D.mergeObjects(brentHead, brentNose);
				brentHead = Object3D.mergeObjects(brentHead, brentHair);
				brentHead = Object3D.mergeObjects(brentHead, brentMouth);
				brentHead = Object3D.mergeObjects(brentHead, brentFace);
				brentHead.setTransparency(-1);
				brentHead.setCulling(false);
				brentHead.strip();
				brentHead.build();
				brentBody.setTransparency(-1);
				brentBody.setCulling(false);
				brentBody.strip();
				brentBody.build();
				brentLeftHand.setTransparency(-1);
				brentLeftHand.setCulling(false);
				brentLeftHand.strip();
				brentLeftHand.build();
				brentRightHand.setTransparency(-1);
				brentRightHand.setCulling(false);
				brentRightHand.strip();
				brentRightHand.build();
				brentLeftFoot.setTransparency(-1);
				brentLeftFoot.setCulling(false);
				brentLeftFoot.strip();
				brentLeftFoot.build();
				brentRightFoot.setTransparency(-1);
				brentRightFoot.setCulling(false);
				brentRightFoot.strip();
				brentRightFoot.build();
				world.addObject(brentHead);
				world.addObject(brentBody);
				world.addObject(brentLeftHand);
				world.addObject(brentRightHand);
				world.addObject(brentLeftFoot);
				world.addObject(brentRightFoot);
				
				lamp = Primitives.getSphere(1f);
				lamp.setAdditionalColor(255, 255, 255);
				lamp.strip();
				lamp.build();
				world.addObject(lamp);
				
				init();
				init();
				
				cam = world.getCamera();
				cam.lookAt(brentBody.getTransformedCenter());
				cam.rotateCameraX((float) Math.toRadians(270));
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);

				SimpleVector sv = new SimpleVector();
				sv.set(brentHead.getTransformedCenter());
				sv.x = 0;
				sv.y += 50;
				sv.z -= 20;
				sun.setPosition(sv);
				lamp.translate(sv);
				
				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = OpenGLActivity.this;
				}
			}
		}

		public void init() {
			brentBody.clearTranslation();
			brentHead.clearTranslation();
			brentLeftHand.clearTranslation();
			brentRightHand.clearTranslation();
			brentLeftFoot.clearTranslation();
			brentRightFoot.clearTranslation();
			brentBody.clearRotation();
			brentHead.clearRotation();
			brentLeftHand.clearRotation();
			brentRightHand.clearRotation();
			brentLeftFoot.clearRotation();
			brentRightFoot.clearRotation();
			brentBody.setScale(scale);
			brentHead.setScale(scale);
			brentLeftHand.setScale(scale);
			brentRightHand.setScale(scale);
			brentLeftFoot.setScale(scale);
			brentRightFoot.setScale(scale);
			
			SimpleVector ce1 = new SimpleVector(0f, 0f, (2.7f * -scale));
			SimpleVector pi1 = new SimpleVector(0f, 0f, 0f);
			SimpleVector ce2 = new SimpleVector(0f, 0f, 0f);
			SimpleVector pi2 = new SimpleVector(0f, 0f, (-scale/2 + 0.5f));
			brentBody.setOrigin(ce1);
			brentBody.setRotationPivot(pi1);
			brentHead.setOrigin(ce1);
			brentHead.setRotationPivot(pi1); 
			brentLeftHand.setOrigin(ce1);
			brentLeftHand.setRotationPivot(pi1);
			brentRightHand.setOrigin(ce1);
			brentRightHand.setRotationPivot(pi1);
			brentLeftFoot.setOrigin(ce2);
			brentLeftFoot.setRotationPivot(pi2);
			brentRightFoot.setOrigin(ce2);
			brentRightFoot.setRotationPivot(pi2);
			
			brentLeftFoot.rotateX((float) Math.toRadians(180));
			brentRightFoot.rotateX((float) Math.toRadians(180));
		}
		
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			switch (curAction) {
				case NOTHING: { 
					break;
				}
				case WALKING: { 
					walking();
					break;
				}
				case JUMPING: { 
					jumping();
					break;
				}
				case WAVING: { 
					waving();
					break;
				}
				case SHAKING: { 
					shaking();
					break;
				}
				case NODDING: { 
					nodding();
					break;
				}
				case STOPPING: { 
					stopping();
					break;
				}
				default: { 
					break;
				}
			}
			
			if (touchTurn != 0) {
				cam.moveCamera(Camera.CAMERA_MOVEIN, 50);
				cam.rotateY(touchTurn);
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				cam.moveCamera(Camera.CAMERA_MOVEIN, 50);
				cam.rotateX(touchTurnUp);
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
				touchTurnUp = 0;
			}

			fb.clear(back);
			world.renderScene(fb);
			world.draw(fb);
			fb.display();

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}

		private synchronized void walking() {
			if (firstStart) {
				init();
				brentLeftHand.rotateZ((float) Math.toRadians(30));
				brentRightHand.rotateZ((float) Math.toRadians(30));
				brentLeftFoot.rotateX((float) Math.toRadians(30));
				brentRightFoot.rotateX((float) Math.toRadians(-30));
				curAction = WALKING;
				firstStart = false;
			}
			if (ffInd < ffMax/2) {
				try {
					Thread.sleep((long) 30);
					brentLeftHand.rotateZ((float) Math.toRadians(-5));
					brentRightHand.rotateZ((float) Math.toRadians(-5));
					brentLeftFoot.rotateX((float) Math.toRadians(-5));
					brentRightFoot.rotateX((float) Math.toRadians(5));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= ffMax/2) && (ffInd < ffMax)) {
				try {
					Thread.sleep((long) 30);
					brentLeftHand.rotateZ((float) Math.toRadians(5));
					brentRightHand.rotateZ((float) Math.toRadians(5));
					brentLeftFoot.rotateX((float) Math.toRadians(5));
					brentRightFoot.rotateX((float) Math.toRadians(-5));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else {
				ffInd = 0;
				if (curAction != newAction) {
					brentLeftHand.rotateZ((float) Math.toRadians(-30));
					brentRightHand.rotateZ((float) Math.toRadians(-30));
					brentLeftFoot.rotateX((float) Math.toRadians(-30));
					brentRightFoot.rotateX((float) Math.toRadians(30));
					init();
					curAction = newAction;
				}
			} 
		}

		private synchronized void jumping() {
			if (firstStart) {
				init();
				SimpleVector ce = new SimpleVector(0f, 0f, (2.7f * -scale + 1.5f));
				SimpleVector pi = new SimpleVector(0f, 0f, 0f);
				brentLeftFoot.setOrigin(ce);
				brentLeftFoot.setRotationPivot(pi);
				brentRightFoot.setOrigin(ce);
				brentRightFoot.setRotationPivot(pi);
				curAction = JUMPING;
				firstStart = false;
			}
			if (ffInd < 11) {
				try {
					Thread.sleep((long) 15);
					brentLeftFoot.rotateX((float) Math.toRadians(5));
					brentRightFoot.rotateX((float) Math.toRadians(5));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= 11) && (ffInd < ffMax/2)) {
				try {
					Thread.sleep((long) 15);
					float x = 0f; 
					float y = 0f; 
					float z = 0.5f; 
					brentBody.translate(x, y, z);
					brentHead.translate(x, y, z);
					brentLeftHand.translate(x, y, z);
					brentRightHand.translate(x, y, z);
					brentLeftFoot.translate(x, y, z);
					brentRightFoot.translate(x, y, z);
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= ffMax/2) && (ffInd < (ffMax - 15))) {
				try {
					Thread.sleep((long) 15);
					float x = 0f; 
					float y = 0f; 
					float z = -0.5f; 
					brentBody.translate(x, y, z);
					brentHead.translate(x, y, z);
					brentLeftHand.translate(x, y, z);
					brentRightHand.translate(x, y, z);
					brentLeftFoot.translate(x, y, z);
					brentRightFoot.translate(x, y, z);
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= (ffMax - 15)) && (ffInd < ffMax)) {
				try {
					Thread.sleep((long) 15);
					brentLeftFoot.rotateX((float) Math.toRadians(-5));
					brentRightFoot.rotateX((float) Math.toRadians(-5));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else {
				try {
					Thread.sleep((long) 500);
					ffInd = 0;
					if (curAction != newAction) {
						SimpleVector ce = new SimpleVector(0f, 0f, 0f);
						SimpleVector pi = new SimpleVector(0f, 0f, (-scale/2 + 0.5f));
						brentLeftFoot.setOrigin(ce);
						brentLeftFoot.setRotationPivot(pi);
						brentRightFoot.setOrigin(ce);
						brentRightFoot.setRotationPivot(pi);
						init();
						curAction = newAction;
					}
				} catch (InterruptedException e) {
				}
			} 
		}

		private synchronized void waving() {
			if (firstStart) {
				init();
				SimpleVector ce = new SimpleVector(0f, 0f, -1f);
				SimpleVector pi = new SimpleVector(0f, 0f, (-scale/3 + 2.7f));
				brentLeftHand.setOrigin(ce);
				brentLeftHand.setRotationPivot(pi);
				brentLeftHand.rotateY((float) Math.toRadians(-90));
				curAction = WAVING;
				firstStart = false;
			}
			if (ffInd < ffMax/2) {
				try {
					Thread.sleep((long) 30);
					brentLeftHand.rotateY((float) Math.toRadians(5));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= ffMax/2) && (ffInd < ffMax)) {
				try {
					Thread.sleep((long) 30);
					brentLeftHand.rotateY((float) Math.toRadians(-5));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else {
				try {
					Thread.sleep((long) 30);
					ffInd = 0;
					if (curAction != newAction) {
						SimpleVector ce = new SimpleVector(0f, 0f, (2.7f * -scale));
						SimpleVector pi = new SimpleVector(0f, 0f, 0f);
						brentLeftHand.setOrigin(ce);
						brentLeftHand.setRotationPivot(pi);
						brentLeftHand.rotateY((float) Math.toRadians(90));
						init();
						curAction = newAction;
					}
				} catch (InterruptedException e) {
				}
			} 
		}

		private synchronized void shaking() {
			if (firstStart) {
				init();
				brentHead.rotateZ((float) Math.toRadians(-12));
				curAction = SHAKING;
				firstStart = false;
			}
			if (ffInd < ffMax/2) {
				try {
					Thread.sleep((long) 30);
					brentHead.rotateZ((float) Math.toRadians(2));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= ffMax/2) && (ffInd < ffMax)) {
				try {
					Thread.sleep((long) 30);
					brentHead.rotateZ((float) Math.toRadians(-2));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else {
				try {
					Thread.sleep((long) 30);
					ffInd = 0;
					if (curAction != newAction) {
						brentHead.rotateZ((float) Math.toRadians(12));
						init();
						curAction = newAction;
					}
				} catch (InterruptedException e) {
				}
			} 
		}

		private synchronized void nodding() {
			if (firstStart) {
				init();
				SimpleVector ce = new SimpleVector(0f, 0f, 0f);
				SimpleVector pi = new SimpleVector(0f, 0f, (2.7f + 1.4f/2));
				brentHead.setOrigin(ce);
				brentHead.setRotationPivot(pi);
				brentHead.rotateX((float) Math.toRadians(-12));
				curAction = NODDING;
				firstStart = false;
			}
			if (ffInd < ffMax/2) {
				try {
					Thread.sleep((long) 30);
					brentHead.rotateX((float) Math.toRadians(2));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else if ((ffInd >= ffMax/2) && (ffInd < ffMax)) {
				try {
					Thread.sleep((long) 30);
					brentHead.rotateX((float) Math.toRadians(-2));
					ffInd = ffInd + 5;
				} catch (InterruptedException e) {
				}
			}
			else {
				try {
					Thread.sleep((long) 30);
					ffInd = 0;
					if (curAction != newAction) {
						SimpleVector ce = new SimpleVector(0f, 0f, (2.7f * -scale));
						SimpleVector pi = new SimpleVector(0f, 0f, 0f);
						brentHead.setOrigin(ce);
						brentHead.setRotationPivot(pi);
						brentHead.rotateX((float) Math.toRadians(12));
						init();
						curAction = newAction;
					}
				} catch (InterruptedException e) {
				}
			} 
		}

		private synchronized void stopping() {
			if (firstStart) {
				init();
				curAction = NOTHING;
				firstStart = false;
			}
		}
	
	}
		
}
