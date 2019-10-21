package objects;

import main.Constants;
import world.Room;

public class Water extends ProtoEdible {

	public Water(int id, int x, int y, int radius, int type, Room room, ProtoGenerator gen) {
		super(id, x, y, radius, type, room, gen);
	}
	
	@Override
	public void kill(Player killer) {
		if(killer.getWater() < 100) {
			killer.setWater(killer.getWater() + 3);
			this.room.objects.remove(this);
			for(GameObject player : this.room.objects) if(player.getType() == Constants.ANIMAL) {
				((Player) player).removalMap.put(this, killer);
				((Player) player).toRemove.add(this);
			}
			if(origin != null) origin.amount--;
		}
	}

}
