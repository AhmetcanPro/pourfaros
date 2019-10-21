package world;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import main.Constants;
import main.Main;
import objects.FoodSpot;
import objects.GameObject;
import objects.Lilypad;
import objects.Mushroom;
import objects.Player;
import objects.ProtoEdible;
import objects.WaterSpot;
import server.MsgWriter;
import tools.GameList;
import tools.NetworkingTools;

public class Room extends Thread {
	
	public GameList objects = new GameList();
	public int id;
	public short playerCount;
	public int object_id = 2;
	private Main main;
	
	public CopyOnWriteArrayList<GameObject> leaderboard = new CopyOnWriteArrayList<GameObject>();
	
	public Room(int id, Main main) {
		this.id = id;
		this.playerCount = 0;
		this.main = main;
	}
	
	private void generate() {
		Random random = new Random();
		for(int i = 0; i < Constants.OBSTACLE_AMOUNT;i++) {
			int size = random.nextBoolean() ? Constants.OBSTACLE_MIN : Constants.OBSTACLE_MAX;
			
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt((0 + size) * 4, ((Constants.GAME_WIDTH - size) * 4) + 1), ThreadLocalRandom.current().nextInt(0, ((Constants.GAME_HEIGHT - size)  * 4) + 1), size, Constants.OBSTACLE));
			object_id++;
		}
		
		for(int i = 0; i < Constants.LILYPAD_AMOUNT;i++) {
			objects.add(new Lilypad(object_id, ThreadLocalRandom.current().nextInt(0, (450 * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.LILYPAD_SIZE, Constants.LILYPAD, this));
			object_id++;
			objects.add(new Lilypad(object_id, ThreadLocalRandom.current().nextInt((Constants.GAME_WIDTH - 450) * 4, Constants.GAME_WIDTH * 4 + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.LILYPAD_SIZE, Constants.LILYPAD, this));
			object_id++;
		}
		
		for(int i = 0; i < Constants.MUD_AMOUNT;i++) {
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(450 * 4 + Constants.MUD_SIZE, (Constants.GAME_WIDTH * 4 - Constants.MUD_SIZE) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0 + Constants.MUD_SIZE, (Constants.GAME_HEIGHT * 4 - Constants.MUD_SIZE) + 1), Constants.MUD_SIZE, Constants.MUD));
			object_id++;
		}
		
		for(int i = 0; i < Constants.POND_AMOUNT;i++) {
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(450 * 4 + Constants.POND_SIZE, (Constants.GAME_WIDTH * 4 - Constants.POND_SIZE) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0 + Constants.POND_SIZE, (Constants.GAME_HEIGHT * 4 - Constants.POND_SIZE) + 1), Constants.POND_SIZE, Constants.POND));
			GameObject obj = (GameObject) objects.get(object_id);
			
			object_id++;
			for(i = 0; i < Constants.LILYPAD_AMOUNT;i++) {
				objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(obj.getX() - Constants.POND_SIZE + Constants.LILYPAD_SIZE, (obj.getX() + Constants.POND_SIZE - Constants.LILYPAD_SIZE) + 1), ThreadLocalRandom.current().nextInt(obj.getY() - Constants.POND_SIZE + Constants.LILYPAD_SIZE, (obj.getY() + Constants.POND_SIZE - Constants.LILYPAD_SIZE) + 1), Constants.LILYPAD_SIZE, Constants.LILYPAD));
				object_id++;
			}
		}
		
		for(int i = 0; i < Constants.ROCK_AMOUNT;i++) {
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.ROCK_SIZE, Constants.ROCK));
			object_id++;
		}
		
		for(int i = 0; i < Constants.BUSH_AMOUNT;i++) {
			objects.add(new FoodSpot(object_id, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.BUSH_SIZE, Constants.BUSH, this));
			object_id++;
		}
		
		for(int i = 0; i < Constants.WATER_SPOT_AMOUNT;i++) {
			objects.add(new WaterSpot(object_id, ThreadLocalRandom.current().nextInt(450 * 4, (Constants.GAME_WIDTH * 4) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.WATER_SPOT_SIZE, Constants.WATER_SPOT, this));
			object_id++;
		}
		
		for(int i = 0; i < Constants.GREEN_BUSH_AMOUNT;i++) {
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.GREEN_BUSH_SIZE, Constants.GREEN_BUSH));
			object_id++;
		}
		
		for(int i = 0; i < Constants.HOLE_AMOUNT;i++) {
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(450 * 4, (Constants.GAME_WIDTH * 4) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.HOLE_SIZE, Constants.HOLE));
			object_id++;
		}
		
		for(int i = 0; i < Constants.SMALL_HOLE_AMOUNT;i++) {
			objects.add(new GameObject(object_id, ThreadLocalRandom.current().nextInt(450 * 4, (Constants.GAME_WIDTH * 4) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.SMALL_HOLE_SIZE, Constants.SMALL_HOLE));
			object_id++;
		}
		
		for(GameObject object1 : objects) {
			for(GameObject object2 : objects) {
				if(object1.getId() != object2.getId() && object1.getType() != Constants.MUD && object2.getType() != Constants.MUD) {
					collision(object1, object2);
				}
				
				if(object1.getType() == Constants.MUD && object2.getType() == Constants.POND) collision(object1, object2);
			}
		}
		
		for(int i = 0; i < Constants.FOOD_AMOUNT / Math.round(2);i++) {
			objects.add(new ProtoEdible(object_id, ThreadLocalRandom.current().nextInt(450 * 4, (Constants.GAME_WIDTH * 4) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), random.nextBoolean() == true ? Constants.BIG_FOOD : Constants.SMALL_FOOD, Constants.FOOD, this, null));
			object_id++;
		}
		
		for(int i = 0; i < Constants.MUSHROOM_AMOUNT;i++) {
			objects.add(new Mushroom(object_id, ThreadLocalRandom.current().nextInt(450 * 4, (Constants.GAME_WIDTH * 4) + 1) - 450 * 4, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), Constants.MUSHROOM_SIZE, Constants.MUSHROOM, this));
			object_id++;
		}
	}
	
	public void collision(GameObject entity1, GameObject entity2) {
		double dx = entity2.getX() - entity1.getX();
	    double dy = entity2.getY() - entity1.getY();

	    double dist = Math.sqrt(dx * dx + dy * dy);

	    if (((entity1.getRadius() + entity2.getRadius())) >= dist) {
	    	if(dist <= 0) dist = 1;

	    	double nx = dx / dist;
	        double ny = dy / dist;

	        entity2.setX((int) (entity1.getX() + nx * (entity1.getRadius() + entity2.getRadius())));
	        entity2.setY((int) (entity1.getY() + ny * (entity1.getRadius() + entity2.getRadius())));
	    }
	}
	
	public void collisionDynamic(GameObject entity1, GameObject entity2) {
        double dx = entity2.getX() - entity1.getX();
        double dy = entity2.getY() - entity1.getY();

        double dist = Math.sqrt(dx * dx + dy * dy);

        if (((entity1.getRadius() + entity2.getRadius())) >= dist) {
	    	if(dist <= 0) dist = 1;

	    	double nx = dx / dist;
	        double ny = dy / dist;

            double distB1 = (dist * (entity1.getRadius() / (entity1.getRadius() + entity2.getRadius())));
            double cx = entity1.getX() + nx * distB1;
            double cy = entity1.getY() + ny * distB1;

            entity1.setX((int) (cx - nx * entity1.getRadius()));
            entity1.setY((int) (cy - ny * entity1.getRadius()));

            entity2.setX((int) (cx - nx * entity2.getRadius()));
            entity2.setY((int) (cy - ny * entity2.getRadius()));
        }
    }
	
	public boolean collisionTest(GameObject object1, GameObject object) {
		int dx = object1.getX() - object.getX();
		int dy = object1.getY() - object.getY();

		int dist = (int) Math.sqrt(dx * dx + dy * dy);

		if (object.getRadius() + object1.getRadius() >= dist) {
		    return true;
		}
		return false;
	}
	
	public void updateLeaderboard() {
		leaderboard.clear();
		for(GameObject object : objects) {
			leaderboard.add(object);
		}
		
		List<GameObject> toSend = new ArrayList<GameObject>();
		for(GameObject object : leaderboard) {
			if(object.getType() != Constants.ANIMAL || (object.getType() == Constants.ANIMAL && ((Player) object).isSpectator())) leaderboard.remove(object);
		}
		
		Collections.sort(leaderboard);
		int a = 1;
		for(GameObject object : leaderboard) {
			((Player) object).setRank(a);
			a++;
			
			for(GameObject object1 : leaderboard) {
				if(!object1.equals(object) && object1.getId() == object.getId()) leaderboard.remove(object1);
			}
		}
		
		for(int i = 0; i < Math.min(10, leaderboard.size()); i++) toSend.add(leaderboard.get(i));
		for(GameObject player : leaderboard) {
			MsgWriter toWrite = NetworkingTools.sendLeaderboard(toSend, (Player) player);
			NetworkingTools.sendMessage(((Player) player).getClient().getSocket(), toWrite);
		}
	}
	
	public void run() {
		generate();
		
		while(true) {
			update();
			
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void updatePlayerCount() {
		int players = 0;
		Iterator it = main.rooms.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry) it.next();
	        players += ((Room) pair.getValue()).leaderboard.size();
	    }
	    playerCount = (short) players;
		
		for(GameObject object : objects) {
			if(object.getType() == Constants.ANIMAL) {
				Player player = (Player) object;
				
				NetworkingTools.sendMessage(player.getClient().getSocket(), NetworkingTools.updatePlayer(this));
			}
		}
	}
	
	public void initPlayer(Player player) {
		player.reset();
		NetworkingTools.sendMessage(player.getClient().getSocket(), NetworkingTools.sendJoinPacket(this, player, 1));
		NetworkingTools.sendMessage(player.getClient().getSocket(), NetworkingTools.upgrade(player));
		
		for(GameObject object : objects) {
			if(object.getType() == Constants.ANIMAL) {
				((Player) object).toAdd.add(player);
			}
		}
		player.toAdd.clear();
		for(GameObject o : objects) {
			player.toAdd.add(o);
		}
	}
	
	public void addSpectator(Player player) {
		objects.add(player);
			
		player.setVelocityX(Constants.DEFAULT_SPEED);
		player.setVelocityY(Constants.DEFAULT_SPEED);
			
		player.reset();
		NetworkingTools.sendMessage(player.getClient().getSocket(), NetworkingTools.sendJoinPacket(this, player, 2));
		player.toAdd.clear();
		for(GameObject o : objects) {
			player.toAdd.add(o);
		}
	}
	
	public void removePlayer(Player player) {
		objects.remove(player);
		
		if(!player.isSpectator()) {
			for(GameObject object : objects) {
				if(object.getType() == Constants.ANIMAL) {
					((Player) object).toRemove.add(player);
					((Player) object).removalMap.put(player, player);
				}
			}
		}
	}
	
	public void killPlayer(Player player, int reason, Player kill) {
		NetworkingTools.sendMessage(player.getClient().getSocket(), NetworkingTools.sendDeath(player, reason));
		
		for(GameObject object : objects) {
			if(object.getType() == Constants.ANIMAL) {
				((Player) object).toRemove.add(player);
				((Player) object).removalMap.put(player, kill);
			}
		}
		
		player.reset();
		player.setSpectator(true);
	}
	
	public void update() {
		for(GameObject object : objects) {
			if(object.getType() == Constants.HOLE) {
				for(GameObject player : objects) {
					if(player.getType() == Constants.ANIMAL && !((Player) player).isSpectator() && ((Player) player).isInHole() == false && collisionTest(player, object)) {
						((Player) player).setHole(object.getId());
						((Player) player).setInHole(true);
						
						for(GameObject toSend : objects) {
							if(toSend.getType() == Constants.ANIMAL) {
								((Player) toSend).toRemove.add(player);
								((Player) toSend).removalMap.put(player, object);
							}
						}
					}
				}
			}
			
			object.update();
		}
		
	    updatePlayerCount();
	    updateLeaderboard();
	}

}

