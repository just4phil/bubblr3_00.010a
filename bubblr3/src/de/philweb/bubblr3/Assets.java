package de.philweb.bubblr3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;



public class Assets {

	private final static String FILE_IMAGE_ATLAS = "data/imageatlas/pages.atlas";
	public static TextureAtlas imageAtlasGame;
	private final static String FILE_UI_SKIN = "skin/uiskin.json";
	public static Skin skin;
	public static Texture items;
	public static TextureRegion dummyFire;
	public static BitmapFont font;


	//===================================================
	

	
    public static Skin getSkin(){
//        if( skin == null ) {			// erzeugt fehler, dass skin bei restart der app plÃ¶tzlich weg ist!
            FileHandle skinFile = Gdx.files.internal(FILE_UI_SKIN);
            skin = new Skin( skinFile );
//        }
        return skin;
    }
	

    
    
	public static void loadRessources (Game game, AssetManager m_assetManager) {
		
		//----- BACKGROUND -------------------------------
		TextureParameter param;
		param = new TextureParameter();
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;
		

		//----- ITEMS (ImageAtlas) -------------------------------
		ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), game.getResolutions());

		System.out.println("Asset path: " + resolver.resolve(FILE_IMAGE_ATLAS).path());
		
        m_assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
        m_assetManager.load(FILE_IMAGE_ATLAS, TextureAtlas.class);

		//------- FONTS ------------------------------------		
		m_assetManager.load("skin/default.fnt", BitmapFont.class);
		
	}	
		
	
	
	public static void assignRessources (AssetManager m_assetManager) {
		
		if (m_assetManager.isLoaded(FILE_IMAGE_ATLAS)) {
					
			imageAtlasGame = m_assetManager.get(FILE_IMAGE_ATLAS, TextureAtlas.class);

			dummyFire = imageAtlasGame.findRegion("dummyFire");
		}
		
		//---------- FONTS ----------------------------------------------			
	
		font = m_assetManager.get("skin/default.fnt", BitmapFont.class);
		font.setUseIntegerPositions(false);	
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

	}
	
}



