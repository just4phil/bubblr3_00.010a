package de.philweb.bubblr3;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class PhysicsBodyFactory {

	
	public static void addDynamicBody(Game game, DynamicGameObject gameObject, World box2dWorld) {
		
		gameObject.setBox2DWorld(box2dWorld);
		
		BodyDef characterBodyDef = new BodyDef();
		characterBodyDef.type = BodyDef.BodyType.DynamicBody;

		Body body = box2dWorld.createBody(characterBodyDef);
		body.setUserData(gameObject);
		body.setTransform(gameObject.position, 0f);

		gameObject.body = body;
	}
		
	
	public static void addRectangleFixture(int ID, DynamicGameObject gameObject, float w, float h, float offsetX, float offsetY) {
		
		Vector2 offset = new Vector2();
		offset.set(w/2f, h/2f); 		//---offset for fixture
		
		FixtureDef characterFixtureDef = new FixtureDef();
		PolygonShape characterShape = new PolygonShape();
		characterShape.setAsBox(w / 2.0f, h / 2.0f, offset, 0.0f);

		characterFixtureDef.shape = characterShape;
		characterFixtureDef.density = 1.0f;
		 
		gameObject.body.createFixture(characterFixtureDef);
		characterShape.dispose(); 
//		body.setMassData(masse);
		Fixture fix = gameObject.body.getFixtureList().get(0);
		fix.setUserData(ID);
	}
	
	

	
	public static void addStaticTileBodyAndFixture(int ID, World box2dWorld, float x, float y, float w, float h, float offsetX, float offsetY) {
		
		BodyDef characterBodyDef = new BodyDef();
		characterBodyDef.type = BodyDef.BodyType.StaticBody;

		Body body = box2dWorld.createBody(characterBodyDef);
//		body.setUserData(gameObject);
		body.setTransform(x - w/2f, y - h/2f, 0f);

		
		Vector2 offset = new Vector2();
		offset.set(0f, 0f); 		//---offset for fixture
		
		FixtureDef characterFixtureDef = new FixtureDef();
		PolygonShape characterShape = new PolygonShape();
		characterShape.setAsBox(w / 2.0f, h / 2.0f, offset, 0.0f);
		
		characterFixtureDef.shape = characterShape;
		 
		body.createFixture(characterFixtureDef);
		characterShape.dispose(); 
		Fixture fix = body.getFixtureList().get(0);
		fix.setUserData(ID);
	}
	
	
	public static void convertMap(World box2dWorld, TiledMap map, float PPM, float playfieldOffsetX, float playfieldOffsetY, int defaultID) {
		
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		
	    for(int y = 0; y <= layer.getHeight() -1; y++) {
	    	for(int x = 0; x <= layer.getWidth() -1; x++) {
	    		Cell cell = layer.getCell(x, y);
	    		if(cell != null) {

	    			PhysicsBodyFactory.addStaticTileBodyAndFixture(defaultID, box2dWorld,  
	    				playfieldOffsetX + ((x + 1) * layer.getTileWidth() / PPM),
	    				playfieldOffsetY + ((y + 1) * layer.getTileHeight() / PPM), 
	    				layer.getTileWidth() / PPM, 
	    				layer.getTileHeight() / PPM,
	    				0, 0);
	    		} 
	      	}
	    }
	}
}
