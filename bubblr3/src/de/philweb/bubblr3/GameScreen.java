package de.philweb.bubblr3;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;


public class GameScreen implements Screen {
	
	protected Game game;
	protected SpriteBatch batch;
	protected OrthographicCamera gameCamera;
	protected OrthographicCamera HUDCamera;
	protected Stage stage;
	protected Skin skin;
	
	private final static String MAP_LEVEL1 = "data/maps/solo/level1.tmx";
	private TmxMapLoader tmxMapLoader;
	public TiledMap map;
	public World box2dWorld;
	public ArrayList<DynamicGameObject> boxes = new ArrayList<DynamicGameObject>();
	public static final int BOXCOUNT = 60;
	
	//===== reduce FPS =======================
	public static final long RENDERER_SLEEP_MS = 0; // 34 -> 30 fps, 30 -> 34 fps, 22 gives ~46 FPS, 20 = 100, 10 = 50
    private long now2, diff, start;
    
	private Renderer renderer;
	private boolean fixed;
	private boolean interpolated;
	
	private final static int FPSupdateIntervall = 1;  //--- display FPS alle x sekunden
	private long lastRender;
	private long now;
	public int frameCount = 0;
	public int lastFPS = 0;
	
	private final static int logic_FPSupdateIntervall = 1;  //--- display FPS alle x sekunden
	private long logic_lastRender;
	private long logic_now;
	public int logic_frameCount = 0;
	public int logic_lastFPS = 0;
	
	float dt;
	float accumulator;
    
    private final static String showFixedTimeStepWithInterpolation = "fixed TimeStep & interpolation";
    private final static String showFixedTimeStepWithoutInterpolation = "fixed TimeStep (no interp.)";
    private final static String showVariableTimeStep = "variable TimeStep";
    
    private final static float BUTTONWIDTH = 250f;
    private final static float BUTTONHEIGHT = 60f;
     
	protected int i;
	protected int click_X;
	protected int click_Y;
	protected Vector3 touchPoint;
	private Rectangle viewportHUD;
	private Rectangle viewportGAME;
	
	
    
	//====================================================================================
	public GameScreen(Game game, boolean fixed, boolean interpolated) {

		this.game = game;
		this.fixed = fixed;
		this.interpolated = interpolated;
		
		batch 			= game.getSpritebatch();
   	 	gameCamera 		= game.getGAMECamera();
   	 	HUDCamera 		= game.getHUDCamera();

		stage = new Stage(0, 0, false, batch);
   	 	stage.setCamera(HUDCamera);
   	 	Gdx.input.setInputProcessor(stage);
   	 
   	 
		//---- initialize level  ---------------------
		box2dWorld = new World(new Vector2(0.0f, -10f), true);
		
		game.getGameObjectManager().clearList();
		for (int i = 0; i < BOXCOUNT; i++) {
			
			DynamicGameObject object = new DynamicGameObject(game, 1, Bubblr.MAPBORDERLEFT + i/4f, 13f, 0.6f, 0.6f);
			PhysicsBodyFactory.addDynamicBody(game, object, box2dWorld);
			PhysicsBodyFactory.addRectangleFixture(1, object, object.width, object.height, 0, 0);
			object.body.setFixedRotation(false);
			object.body.getFixtureList().get(0).setRestitution(0.8f);
			boxes.add(object);
		}
		
	    //------------ load the tiledMap ---------
		
	    ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), game.getResolutions());
	    tmxMapLoader = new TmxMapLoader(resolver);
	    map = tmxMapLoader.load(MAP_LEVEL1);
	    
	    PhysicsBodyFactory.convertMap(box2dWorld, map, game.getPPM(), 
	    		(Bubblr.VIRTUAL_WIDTH_GAME - Bubblr.PLAYFIELDWIDTH) / 2f, 
	    		(Bubblr.VIRTUAL_HEIGHT_GAME - Bubblr.PLAYFIELDHEIGHT) / 2f,
	    		2);
	    
	    
	    //---- initialize renderer ---------------------
		renderer = new Renderer(game, this);

		//---------------------------------------------------------		

		dt = 0.0133f;	// logic updates approx. @ 75 hz
		
		setupButtons();
	}

	
	
	//====================================================================================
    @Override
    public void render(float delta) {
    	 	
		//---------- FPS check ----------------------------
    	frameCount ++;
		now = System.nanoTime();	// zeit loggen
		
		if ((now - lastRender) >= FPSupdateIntervall * 1000000000)  {

			lastFPS = frameCount / FPSupdateIntervall;
					
			frameCount = 0;
			lastRender = System.nanoTime();
		}
		//--------------------------------------------------------------
    	

		if (fixed) {
			
			renderFIXEDTIMESTEP(delta);
		}
		else renderVARIABLETIMESTEP(delta);
    }
    
    
    
    
	//====================================================================================
    //	http://gafferongames.com/game-physics/fix-your-timestep/
    public void renderFIXEDTIMESTEP(float delta) {   	

        if ( delta > 0.25f ) delta = 0.25f;	  // note: max frame time to avoid spiral of death
        
        accumulator += delta;
        
        while (accumulator >= dt) {
        	
        	if (interpolated == true) game.getGameObjectManager().copyCurrentPosition();
        	
        	updating(dt);
        	accumulator -= dt;
            
        	if (interpolated == true) game.getGameObjectManager().interpolateCurrentPosition(accumulator / dt);
    		
        	//---------- FPS check -----------------------------
    		
        	logic_frameCount ++;
        	logic_now = System.nanoTime();	// zeit loggen
    		
    		if ((logic_now - logic_lastRender) >= logic_FPSupdateIntervall * 1000000000)  {

    			logic_lastFPS = logic_frameCount / logic_FPSupdateIntervall;		
    			logic_frameCount = 0;
    			logic_lastRender = System.nanoTime();
    		}
    		//--------------------------------------------------------------
        }

        rendering(delta);
    }
    
    	

    
	//====================================================================================
    public void renderVARIABLETIMESTEP(float delta) {   	

    	updating(delta);
        
		//---------- FPS check -----------------------------
		
    	logic_frameCount ++;
    	logic_now = System.nanoTime();	// zeit loggen
		
		if ((logic_now - logic_lastRender) >= logic_FPSupdateIntervall * 1000000000)  {

			logic_lastFPS = logic_frameCount / logic_FPSupdateIntervall;	
			logic_frameCount = 0;
			logic_lastRender = System.nanoTime();
		}       
    	//------------------------------------------------------

        rendering(delta);
    }
    

    
    
	//====================================================================================
    public void updating(float delta) {
         	
		for (i = 0; i < BOXCOUNT; i++) boxes.get(i).update(delta);
		box2dWorld.step(delta, 10, 8);
    }
    
    
    
   
	//====================================================================================
    public void rendering(float delta) {

    	//------- render Tiledmap ---------------------		
		renderer.renderTiledMap();
			
		//------- render game-stuff ---------------------
		renderer.renderGamePlay();

		//------- render HUD-stuff ---------------------		
		renderer.renderHUD();
		
		//------- render stage-stuff ---------------------  
		game.useHUDCam(); 	

   	 	stage.act();
   	 	stage.draw();
		
		
		
        //------------- to limit fps ------------------------
        if (RENDERER_SLEEP_MS > 0) {
        
        	now2 = System.currentTimeMillis();
        	diff = now2 - start;
		
        	if (diff < RENDERER_SLEEP_MS) {
        		try {
        			Thread.sleep(RENDERER_SLEEP_MS - diff);
        		} catch (InterruptedException e) {
        		}
        	}

        	start = System.currentTimeMillis();
        }
        //-----------------------------------------------------
    }
	
    
    
	//====================================================================================
    public void setupButtons() {

		skin = Assets.getSkin(); 
		
		Table menuTable = new Table();
	    stage.addActor(menuTable);
	    menuTable.setPosition(400, 460);
	    menuTable.debug().defaults().space(6);
	    
	    TextButton btnPlayMenu = new TextButton(showVariableTimeStep, skin);
	    btnPlayMenu.addListener(new ActorGestureListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				
				game.setScreen(new GameScreen(game, false, false));			
			}});
	    menuTable.add(btnPlayMenu).width(BUTTONWIDTH).height(BUTTONHEIGHT);
	    
	    TextButton btnButton1 = new TextButton(showFixedTimeStepWithInterpolation, skin);
	    btnButton1.addListener(new ActorGestureListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				
				game.setScreen(new GameScreen(game, true, true));			
			}});
	    menuTable.add(btnButton1).width(BUTTONWIDTH).height(BUTTONHEIGHT);
	    
	    TextButton btnButton2 = new TextButton(showFixedTimeStepWithoutInterpolation, skin);
	    btnButton2.addListener(new ActorGestureListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				
				game.setScreen(new GameScreen(game, true, false));			
			}});
	    menuTable.add(btnButton2).width(BUTTONWIDTH).height(BUTTONHEIGHT);
    }
    
    
    
  //====================================================================================
	
    
    /** @see ApplicationListener#resize(int, int) */
    @Override
    public void resize(int width, int height) {
   	//--- nothing more to do here... evrything is done in Game.java
   	 
    	viewportGAME = game.getViewportGAME();
    	viewportHUD = game.getViewportHUD();
    	
    	if (viewportHUD != null) {
    		     		
    		stage.setViewport(game.getVirtualWidthHUD(), game.getVirtualHeightHUD(), true, viewportHUD.x, viewportHUD.y, viewportHUD.width, viewportHUD.height);
    	}
    }
    
    
    
    /** Called when this screen is no longer the current screen for a {@link Game}. */
    @Override
     public void hide() {
          // called when current screen changes from this to a different screen
    	
    	dispose();
     }
    
    
   @Override
    public void dispose() {
            					// called from hide()
   		stage.clear();
   		stage.dispose();
    }



@Override
public void show() {
	// TODO Auto-generated method stub
	
}



@Override
public void pause() {
	// TODO Auto-generated method stub
	
}



@Override
public void resume() {
	// TODO Auto-generated method stub
	
}
	
	
}
