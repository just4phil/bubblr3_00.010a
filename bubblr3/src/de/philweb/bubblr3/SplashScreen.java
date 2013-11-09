package de.philweb.bubblr3;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Screen;

public class SplashScreen implements Screen {
	
	protected Game game;
    boolean areImagesReady = false;
	
	
    
	//====================================================================================
	public SplashScreen(Game game) {

		this.game = game; 		
		Assets.loadRessources(game, game.getAssetManager());
	}

	
	
	
	//====================================================================================
    @Override
    public void render(float delta) {
    	 	
		//------ update assetmanager ------------
    	if(game.getAssetManager().update() == true) { 	//------------- we are done loading -> assign ressources
    		
    		if (areImagesReady == false) {
    			
    			Assets.assignRessources(game.getAssetManager());
    			areImagesReady = true;
    		}
    		
    		//------------------- we are done loading and have assigned ressources... game can start
    		else game.setScreen(new GameScreen(game, false, false));
    	}	
    }
    
    
   	
  	
    
    
    /** @see ApplicationListener#resize(int, int) */
    @Override
    public void resize(int width, int height) {
   	//--- nothing more to do here... evrything is done in Game.java
    }
    
    
    
    /** Called when this screen is no longer the current screen for a {@link Game}. */
    @Override
     public void hide() {
          // called when current screen changes from this to a different screen
     }
    
    
   @Override
    public void dispose() {

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
