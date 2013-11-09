package de.philweb.bubblr3;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;



public abstract class Game implements ApplicationListener {
	
	private Screen screen;
	private SpriteBatch _batch;
	private AssetManager _assetManager;
	private GameObjectManager _gameObjectManager;
	
	private OrthographicCamera _HUDcamera;
    private int virtualWidthHUD;
    private int virtualHeightHUD;
    private float aspectRatioHUD;
	private Rectangle viewportHUD;
	
	private OrthographicCamera _GAMEcamera;
    private int virtualWidthGAME;
    private int virtualHeightGAME;
    private float aspectRatioGAME;
	private Rectangle viewportGAME;
    
	private Vector3 touchPointGAME;
	private Vector3 touchPointHUD;
	
	float pixelPerMeter;	
	
	private Resolution[] resolutions;
	
	
    
	public void setupHUDcam (int width, int height) {
		
		virtualWidthHUD = width;
		virtualHeightHUD = height;
		aspectRatioHUD = (float)virtualWidthHUD / (float)virtualHeightHUD;
	}
	
	public void setupGAMEcam (int width, int height) {
		
		virtualWidthGAME = width;
		virtualHeightGAME = height;
		aspectRatioGAME = (float)virtualWidthGAME / (float)virtualHeightGAME;
	}
	

	
	
	@Override
	public void create () {
		
		_batch = new SpriteBatch();
		_assetManager = new AssetManager(); 	// must be initialized before super.create (else: = null)
		_gameObjectManager = new GameObjectManager();
		
		_GAMEcamera = new OrthographicCamera(virtualWidthGAME, virtualHeightGAME);
		_GAMEcamera.setToOrtho(false, virtualWidthGAME, virtualHeightGAME);
		
		_HUDcamera = new OrthographicCamera(virtualWidthHUD, virtualHeightHUD);
		_HUDcamera.setToOrtho(false, virtualWidthHUD, virtualHeightHUD);
		
		pixelPerMeter = virtualWidthHUD / virtualWidthGAME;	// is fixed PPM right??
		
   	 	touchPointHUD = new Vector3();
   	 	touchPointGAME = new Vector3();
	}

	 
	
	
	
	@Override
	public void render () {
		
        // clear previous frame
//		Gdx.gl.glClearColor(1, 1, 1, 1);		// for testing: white background
		Gdx.gl.glClearColor(0, 0, 0, 0);		// black background
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		//---------------------------------------------------
		
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

	

	
	@Override
	public void resize (int width, int height) {
			
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop;
        float w;
        float h;
        
    	// calculate new viewport GAMEcam
        crop = new Vector2(0f, 0f);
        
        if(aspectRatio > aspectRatioGAME)
        {
            scale = (float)height/(float)virtualHeightGAME;
            crop.x = (width - virtualWidthGAME * scale) / 2f;
        }
        else if(aspectRatio < aspectRatioGAME)
        {
            scale = (float)width/(float)virtualWidthGAME;
            crop.y = (height - virtualHeightGAME * scale) / 2f;
        }
        else
        {
            scale = (float)width/(float)virtualWidthGAME;
        }

        w = (float)virtualWidthGAME * scale;
        h = (float)virtualHeightGAME * scale;
        viewportGAME = new Rectangle(crop.x, crop.y, w, h);
        
//        pixelPerMeter = w / virtualWidthGAME;	// this seems to be false!!! must be a fixed value
        Gdx.app.log("height: " + h, "pixelPerMeter: " + pixelPerMeter);
        
        //---------------------------------------------------
        
    	// calculate new viewport HUDcam
        crop = new Vector2(0f, 0f);
        
        if(aspectRatio > aspectRatioHUD)
        {
            scale = (float)height/(float)virtualHeightHUD;
            crop.x = (width - virtualWidthHUD * scale) / 2f;
        }
        else if(aspectRatio < aspectRatioHUD)
        {
            scale = (float)width/(float)virtualWidthHUD;
            crop.y = (height - virtualHeightHUD * scale) / 2f;
        }
        else
        {
            scale = (float)width/(float)virtualWidthHUD;
        }

        w = (float)virtualWidthHUD * scale;
        h = (float)virtualHeightHUD * scale;
        viewportHUD = new Rectangle(crop.x, crop.y, w, h);
        
        //---------------------------------------------------
        
        if (screen != null) screen.resize(width, height);
	}

	
	
	//--------------------------------

	
	/** Sets the current screen. {@link Screen#hide()} is called on any old screen, and {@link Screen#show()} is called on the new
	 * screen, if any.
	 * @param screen may be {@code null}
	 */
	public void setScreen (Screen screen) {
		if (this.screen != null) {
			this.screen.hide();
		}
		
		this.screen = screen;
		if (this.screen != null) {
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}
	//------------------------------------------------------
	
	
	public void useGAMECam() {
		
		_GAMEcamera.update();
 
        // set viewport
		if (viewportGAME != null) Gdx.gl.glViewport((int) viewportGAME.x, (int) viewportGAME.y, (int) viewportGAME.width, (int) viewportGAME.height);
 
		_batch.setProjectionMatrix(_GAMEcamera.combined);
	}
	
	
	public void useHUDCam() {
		
		_HUDcamera.update();
 
        // set viewport
        if (viewportHUD != null) Gdx.gl.glViewport((int) viewportHUD.x, (int) viewportHUD.y, (int) viewportHUD.width, (int) viewportHUD.height);
 
		_batch.setProjectionMatrix(_HUDcamera.combined);
	}
	//------------------------------------------------------
	
	public Vector3 unprojectGAME(int click_X, int click_Y) {
   	 
    	_GAMEcamera.unproject(touchPointGAME.set(click_X, click_Y, 0), viewportGAME.x, viewportGAME.y, viewportGAME.width, viewportGAME.height);
   	 return touchPointGAME;
   }
      
	public Vector3 unprojectHUD(int click_X, int click_Y) {
   	 
    	_HUDcamera.unproject(touchPointHUD.set(click_X, click_Y, 0), viewportHUD.x, viewportHUD.y, viewportHUD.width, viewportHUD.height);   
   	 return touchPointHUD;
   } 

	//------------------------------------------------------
	
	
	/** @return the currently active {@link Screen}. */
	public Screen getScreen () {
		return screen;
	}

	
	public AssetManager getAssetManager() {
		return _assetManager;
	}
	
	public GameObjectManager getGameObjectManager() {
		return _gameObjectManager;
	}
	
	public SpriteBatch getSpritebatch() {
		return _batch;
	}
	
	public OrthographicCamera getGAMECamera() {
		return _GAMEcamera;
	}
	
	public OrthographicCamera getHUDCamera() {
		return _HUDcamera;
	}
	
	public Rectangle getViewportGAME() {
		
		return viewportGAME;
	}
	public Rectangle getViewportHUD() {
		
		return viewportHUD;
	}
	public int getVirtualWidthHUD() {
		
		return virtualWidthHUD;
	}
	public int getVirtualHeightHUD() {
		
		return virtualHeightHUD;
	}
	public int getVirtualWidthGAME() {
		
		return virtualWidthGAME;
	}
	public int getVirtualHeightGAME() {
		
		return virtualHeightGAME;
	}
	
	public float getPPM() {
		return pixelPerMeter;
	}
	
	public float meterToPixels(float meter) {	// TODO: Besser als int ausgeben?
		return (float)meter * pixelPerMeter;
	}
	
	public float pixelsToMeter(float pixels) {
		return (float)pixels / pixelPerMeter;		
	}
	
	
	
	public void setResolutions (Resolution[] resolutions) {
		
		this.resolutions = resolutions;
	}
	
	public Resolution[] getResolutions () {
		
		return resolutions;
	}
	
	public void setCameraZoom(float amount) {
		
		_HUDcamera.zoom = _HUDcamera.zoom + amount;
		_GAMEcamera.zoom = _GAMEcamera.zoom + amount;
	}
	
	//------------------------------------------------
	
	
	
	@Override
	public void pause () {
		
		if (screen != null) screen.pause();
	}

	
	@Override
	public void resume () {
		
		if (screen != null) screen.resume();
	}
	
	
	@Override
	public void dispose () {
		
		if (screen != null) screen.hide();
//		_batch.dispose();
	}
	
	
	
}
