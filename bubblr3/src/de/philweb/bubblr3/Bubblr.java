package de.philweb.bubblr3;

import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;


public class Bubblr extends Game {

    public static final int VIRTUAL_WIDTH_HUD 		= 800;
    public static final int VIRTUAL_HEIGHT_HUD 		= 480;
    public static final int VIRTUAL_WIDTH_GAME 		= 25;
    public static final int VIRTUAL_HEIGHT_GAME 	= 15;
    
    public static final float PLAYFIELDWIDTH 		= 18f;			// (32 tiles * 18 px / 32 ppm)
    public static final float PLAYFIELDHEIGHT 		= 14.625f;		// (26 tiles * 18 px / 32 ppm)
    
	public static final float MAPBORDERLEFT 		= 4.5f;
	public static final float MAPBORDERRIGHT 		= 20.3f;
	public static final float MAPBORDERTOP 			= 15f;
	public static final float MAPBORDERBOTTOM 		= 0f;
	
    public static final int TILESHORIZONTALLY 		= 32;
    public static final int TILESVERICALLY 			= 26;
    public static final int TILESVIRTUALRESOLUTION 	= 18; //px
    
    
    //--- hier die spielfeld groesse noch hinterlegen + grenzen rechts links oben unten etzc....

	private Resolution[] resolutions = {
			new Resolution(480, 800, "800x480")
			};
	
	
	@Override
	public void create() {		
		
		super.setupHUDcam(VIRTUAL_WIDTH_HUD, VIRTUAL_HEIGHT_HUD);
		super.setupGAMEcam(VIRTUAL_WIDTH_GAME, VIRTUAL_HEIGHT_GAME);
		
		super.setResolutions(resolutions);
		
		super.create();

		
		setScreen(new SplashScreen(this));
	}
	
	
}
