package objects;

import java.util.concurrent.ThreadLocalRandom;

import main.Constants;
import world.Room;

public class ProtoEdible extends GameObject {
	
	protected Room room;
	protected ProtoGenerator origin;

	public ProtoEdible(int id, int x, int y, int radius, int type, Room room, ProtoGenerator origin) {
		super(id, x, y, radius, type);
		this.room = room;
		this.origin = origin;
	}
	
	@Override
	public void update() {
		for(GameObject object : this.room.objects) {
			if(object.getType() == Constants.ANIMAL && !((Player) object).isSpectator() && ((Player) object).getFlag() != Constants.FLAG_UNDERWATER && room.collisionTest(this, object)) {
				kill((Player) object);
			} else if(object.getType() != Constants.ANIMAL && object.getType() != Constants.GREEN_BUSH && object.getId() != this.getId() && object.getType() != Constants.FOOD && object.getType() != Constants.MUD) {
				try {
					((ProtoEdible) object).getType();
				} catch(Exception e) { 
					this.room.collision(object, this);
				}
			}
		}
		
		if(this.getX() >= (Constants.GAME_WIDTH * 4) - this.getRadius()) {
			this.setX((Constants.GAME_WIDTH * 4) - this.getRadius() - 1);
		}
		
		if(this.getY() >= (Constants.GAME_HEIGHT * 4) - this.getRadius()) {
			this.setY((Constants.GAME_HEIGHT * 4) - this.getRadius() - 1);
		}
	}
	
	public void kill(Player killer) {
		if(killer.getAnimal() != Constants.DEER) {
		killer.setScore(killer.getScore() + 1);
		this.room.objects.remove(this);
		for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) {
			((Player) player).removalMap.put(this, killer);
			((Player) player).toRemove.add(this);
		}
		respawn();
		}
	}
	
	public void respawn() {
		if(origin != null) origin.amount--;
		else {
			ProtoEdible food = new ProtoEdible(this.room.object_id, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), this.getRadius(), this.getType(), this.room, this.origin);
			
			this.room.objects.add(food);
			this.room.object_id++;
			
			for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) {
				((Player) player).toAdd.add(food);
			}
		}
	}

}
