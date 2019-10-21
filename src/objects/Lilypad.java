package objects;

import java.util.concurrent.ThreadLocalRandom;

import main.Constants;
import world.Room;

public class Lilypad extends ProtoEdible {

	public Lilypad(int id, int x, int y, int radius, int type, Room room) {
		super(id, x, y, radius, type, room, null);
	}
	
	@Override
	public void kill(Player killer) {
		if(killer.getAnimal() >= Constants.DEER && killer.getAnimal() != Constants.CHEETAH) {
			killer.setScore(killer.getScore() + 75);
			this.room.objects.remove(this);
			for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) {
				((Player) player).removalMap.put(this, killer);
				((Player) player).toRemove.add(this);
			}	
			respawn();
		}
	}
	
	@Override
	public void respawn() {
		/*ProtoEdible food = new Lilypad(this.room.object_id, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), this.getRadius(), this.getType(), this.room);
		
		this.room.objects.add(food);
		this.room.object_id++;
		
		for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) {
			((Player) player).toAdd.add(food);
		}*/
	}

}
