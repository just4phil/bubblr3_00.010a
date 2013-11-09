package de.philweb.bubblr3;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


public class DynamicGameObject {

	private Game game;
	private World box2dworld;
	public Body body;
	public int objectID;
	
	public float width;			// in metern
	public float height;		// in metern
	
	public Vector2 position;	// in metern	// for dynamic objects this will be the interpolated position!
	public Vector2 position_previous;
	
	public float angle;
	public float angle_previous;

	
	
	public DynamicGameObject(Game game, int objectID, float x, float y, float w, float h) {
		
		this.game = game;
		this.objectID	= objectID;
		
		box2dworld = null;
		body = null;
		
		position 		= new Vector2(x, y);
		position_previous = new Vector2(x, y);
		width 			= w;
		height 			= h;

		//---- add the new gameobject to the global list of gameobjects within the gameobjectmanager
		game.getGameObjectManager().addDynamicGameObject(this);	
	}
	
	
	
	
	public void update (float deltaTime) {


		if (body != null) {
			
			//--- update position & angle for ver. TS
			position.x = body.getPosition().x;	// not neccessary due to interpolation ... only testing difference to fixed timestep 
			position.y = body.getPosition().y;	// not neccessary due to interpolation ... only testing difference to fixed timestep 
			angle = body.getAngle();	// not neccessary due to interpolation ... only testing difference to fixed timestep 

			
			//-- check bottom mapborder ---
			if(body.getPosition().y + height < Bubblr.MAPBORDERBOTTOM ) {	
				body.setTransform(body.getPosition().x, Bubblr.MAPBORDERTOP, 0);
				
				//--- position des objekt inkl. interpolationsposition ebenfalls angleichen, da sonst artefakte der interpolation auftreten
				position_previous.x = body.getPosition().x;
				position_previous.y = body.getPosition().y;
				position.x = position_previous.x;
				position.y = position_previous.y;
			}
			
			//-- check top mapborder ---
			if(body.getPosition().y > Bubblr.MAPBORDERTOP) {
				body.setTransform(body.getPosition().x, Bubblr.MAPBORDERBOTTOM, 0);
				
				//--- position des objekt inkl. interpolationsposition ebenfalls angleichen, da sonst artefakte der interpolation auftreten
				position_previous.x = body.getPosition().x;
				position_previous.y = body.getPosition().y;
				position.x = position_previous.x;
				position.y = position_previous.y;
			}
			
			
			//--- check if object is out of playfield and deactivate
			if(body.getPosition().x < -2f || body.getPosition().x > 26f) {
				body.setActive(false);
			}
			
			if(body.getPosition().y < -3f) {
				body.setActive(false);
			}
		
        // -------------------------------------------------------------------------------------
		}
	}
	
	
	
	public void setBox2DWorld(World box2dworld) {
		
		this.box2dworld = box2dworld;
	}
	
}
