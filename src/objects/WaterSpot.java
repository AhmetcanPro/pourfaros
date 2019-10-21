package objects;

import java.util.concurrent.ThreadLocalRandom;

import main.Constants;
import world.Room;

public class WaterSpot extends ProtoGenerator {
	
	public int cooldown = 0;
	public Room room;

	public WaterSpot(int id, int x, int y, int radius, int type, Room room) {
		super(id, x, y, radius, type);
		this.room = room;
	}
	
	@Override
	public void update() {
		cooldown++;
		if(amount < Constants.WATER_AMOUNT && cooldown >= Constants.WATER_PROD) {
			cooldown = 0;
			amount++;
			
			int x = ThreadLocalRandom.current().nextInt(this.getX() - this.getRadius() / 2, this.getX() + this.getRadius() / 2);
			int y = ThreadLocalRandom.current().nextInt(this.getY() - this.getRadius() / 2, this.getY() + this.getRadius() / 2);
			
			Water object = new Water(this.room.object_id, x, y, Constants.SMALL_FOOD, Constants.WATER, this.room, this);
			
		    this.room.object_id++;
			this.room.objects.add(object);
			for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) ((Player) player).toAdd.add(object);
		}
	}
	
}

