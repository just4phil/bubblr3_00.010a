package de.philweb.bubblr3;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.BodyDef;



public class GameObjectManager {

	
	private ArrayList<DynamicGameObject> dynamicGameObjectList;
	int i;
	int size;
	DynamicGameObject dynamicGameObject;
	
	
	
	public GameObjectManager() {
	
		dynamicGameObjectList = new ArrayList<DynamicGameObject>();
	}
	
		
	
	public void copyCurrentPosition() {
		
		size = dynamicGameObjectList.size();
		
		for (i = 0; i < size; i++) {
		
			dynamicGameObject = dynamicGameObjectList.get(i);
			
			if (dynamicGameObject.body != null) {
				
				if (dynamicGameObject.body.getType() == BodyDef.BodyType.DynamicBody && dynamicGameObject.body.isActive() == true) {
					
					dynamicGameObject.position_previous.x = dynamicGameObject.body.getPosition().x;
					dynamicGameObject.position_previous.y = dynamicGameObject.body.getPosition().y;
					
					dynamicGameObject.angle_previous = dynamicGameObject.body.getAngle();
				}
			}
		}
	}
	
	
	
	public void interpolateCurrentPosition(float alpha) {
		
		size = dynamicGameObjectList.size();
		
		for (i = 0; i < size; i++) {
		
			dynamicGameObject = dynamicGameObjectList.get(i);
			
			if (dynamicGameObject.body != null) {
				
				if (dynamicGameObject.body.getType() == BodyDef.BodyType.DynamicBody && dynamicGameObject.body.isActive() == true) {
										
					//---- interpolate: currentState*alpha + previousState * ( 1.0 - alpha ); ------------------
					dynamicGameObject.position.x = dynamicGameObject.body.getPosition().x * alpha + dynamicGameObject.position_previous.x * (1.0f - alpha);
					dynamicGameObject.position.y = dynamicGameObject.body.getPosition().y * alpha + dynamicGameObject.position_previous.y * (1.0f - alpha);
					
					dynamicGameObject.angle = dynamicGameObject.body.getAngle() * alpha + dynamicGameObject.angle_previous * (1.0f - alpha);
				}
			}
		}
	}
	
	
	
	public ArrayList<DynamicGameObject> getDynamicGameObjectList() {
		
		return dynamicGameObjectList;
	}
	
	public void addDynamicGameObject(DynamicGameObject dynamicGameObject) {
		
		dynamicGameObjectList.add(dynamicGameObject);
	}
	
	public void removeDynamicGameObject(DynamicGameObject dynamicGameObject) {
		
		dynamicGameObjectList.remove(dynamicGameObject);
	}
	
	public void clearList() {
		
		dynamicGameObjectList.clear();
	}
}
