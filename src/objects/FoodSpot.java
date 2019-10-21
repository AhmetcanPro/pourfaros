package objects;

import java.util.concurrent.ThreadLocalRandom;

import main.Constants;
import world.Room;

public class FoodSpot extends ProtoGenerator {
	
	public int cooldown = 0;
	public Room room;

	public FoodSpot(int id, int x, int y, int radius, int type, Room room) {
		super(id, x, y, radius, type);
		this.room = room;
	}
	
	public void update() {
		cooldown++;
		if(amount < Constants.FOOD_AMOUNT && cooldown >= Constants.FOOD_PROD) {
			cooldown = 0;
			amount++;
			
			int x = ThreadLocalRandom.current().nextInt(this.getX() - this.getRadius() / 2, this.getX() + this.getRadius() / 2);
			int y = ThreadLocalRandom.current().nextInt(this.getY() - this.getRadius() / 2, this.getY() + this.getRadius() / 2);
			int radius = ThreadLocalRandom.current().nextInt(0, 100) > 25 ? Constants.SMALL_FOOD : Constants.BIG_FOOD;
			
			GameObject object = new ProtoEdible(this.room.object_id, x, y, radius, Constants.FOOD, room, this);
			
			this.room.object_id++;
			this.room.objects.add(object);
			for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) ((Player) player).toAdd.add(object);
		}
	}
	
}
