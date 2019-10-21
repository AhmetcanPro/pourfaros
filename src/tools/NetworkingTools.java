package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.java_websocket.WebSocket;

import main.Constants;
import objects.GameObject;
import objects.Player;
import server.MsgWriter;
import world.Room;

public class NetworkingTools {
	
	public static void sendMessage(WebSocket socket, MsgWriter writer) {
		try {
			socket.send(writer.getData());
		} catch(Exception e) {}
	}
	
	public static MsgWriter initClient(Room room) {
		MsgWriter writer = new MsgWriter();
		writer.writeUInt8(1); 
		writer.writeUInt16(room.playerCount);
		writer.writeUInt16((short) Constants.GAME_VER);
		
		return writer;
	}
	
	public static MsgWriter sendUpdatePacket(Room room, Player player, int first) {
    	List<GameObject> objects = new ArrayList<>();
    	List<GameObject> objectsToUpdate = new ArrayList<>();
    	for(GameObject object : room.objects) {
    		if(object.getType() == Constants.ANIMAL && !((Player) object).isSpectator() && !((Player) object).isInHole()) {
    			objectsToUpdate.add(object);
    		} else if(object.getType() != Constants.ANIMAL) {
    			objectsToUpdate.add(object);
    		}
    	}
    	for(GameObject object : player.toAdd) {
    		if(object.getType() == Constants.ANIMAL && !((Player) object).isSpectator()) {
    			objects.add(object);
    		} else if(object.getType() != Constants.ANIMAL) {
    			objects.add(object);
    		}
    	}
    	
		MsgWriter writer = new MsgWriter();
		writer.writeUInt8(4); 
		writer.writeUInt16((short) player.getX()); 
		writer.writeUInt16((short) player.getY()); 
		writer.writeUInt16((short) player.getZoom());
		
		writer.writeUInt8(player.isSpectator() ? 2 : 1);
		
		if(!player.isSpectator()) { writer.writeUInt8(player.getFlag() == Constants.FLAG_UNDERWATER ? player.getAirLevel() : player.getWater());
		writer.writeUInt32(player.getScore());
		double percentage = (double) (player.getScore() - player.getLastAni()) / (double) (Tools.getNextAnimalGrowth(player) - player.getLastAni());
		percentage *= 100d;
		writer.writeUInt8(Math.min((int) percentage, 100));
		}
		
		writer.writeUInt16((short) (first == 1 ? (objects.size() + 2) : objects.size()));
		for(GameObject object : objects) {
			if(object.getType() != Constants.ANIMAL) {
				writer.writeUInt8(object.getType());	
				writer.writeUInt32(object.getId());
				writer.writeUInt16((short) object.getRadius());
				writer.writeUInt16((short) object.getX());
				writer.writeUInt16((short) object.getY());
				writer.writeUInt8(0);
			} else if(object.getType() == Constants.ANIMAL) {
				writer.writeUInt8(object.getType());	
				writer.writeUInt32(object.getId());
				writer.writeUInt16((short) object.getRadius());
				writer.writeUInt16((short) object.getX());
				writer.writeUInt16((short) object.getY());
				writer.writeUInt8(0);
				writer.writeUInt8(((Player) object).getAnimal());
				writer.writeString(((Player) object).getPlayerName());
			}
		}
		
		if(first == 1) {
		writer.writeUInt8(19);	
		writer.writeUInt32(0);
		writer.writeUInt16((short) 0);
		writer.writeUInt16((short) (225 * 4));
		writer.writeUInt16((short) (Constants.GAME_HEIGHT * 2));
		writer.writeUInt8(0);
		writer.writeUInt16((short) 450);
		writer.writeUInt16((short) Constants.GAME_HEIGHT);
		
		writer.writeUInt8(19);	
		writer.writeUInt32(1);
		writer.writeUInt16((short) 0);
		writer.writeUInt16((short) ((Constants.GAME_WIDTH - 225) * 4));
		writer.writeUInt16((short) (Constants.GAME_HEIGHT * 2));
		writer.writeUInt8(0);
		writer.writeUInt16((short) 450);
		writer.writeUInt16((short) Constants.GAME_HEIGHT);
		}
		
		writer.writeUInt16((short) objectsToUpdate.size());
		for(GameObject object : objectsToUpdate) {
			writer.writeUInt32(object.getId());
			writer.writeUInt16((short) object.getX());
			writer.writeUInt16((short) object.getY());
			writer.writeUInt16((short) ((object.getRadius() / 4) * 10));
			
			if(object.getType() == Constants.ANIMAL) {
				Player playerObject = (Player) object;
				writer.writeUInt8(playerObject.getAnimal());
				writer.writeUInt16((short) Tools.getAngle(playerObject, 0));
				writer.writeUInt8(player.getFlag());
	            if(((player.getFlag() >> 0) % 2) != 0) {
	            	writer.writeUInt8(player.getFlag() > 2000 ? 1 : 2);
	            }
	            if(((player.getFlag() >> 5) % 2) != 0) writer.writeUInt8(100);
			}
		}
		writer.writeUInt16((short) player.toRemove.size());
		for(GameObject object : player.toRemove) {
			writer.writeUInt32(object.getId());
			GameObject objectToMoveTo = object;
			if(player.removalMap.get(object) != null) objectToMoveTo = player.removalMap.get(object);
			writer.writeUInt8(1);
			writer.writeUInt32(objectToMoveTo.getId());
		}
		
		player.toAdd = new CopyOnWriteArrayList<GameObject>();
		player.toRemove = new CopyOnWriteArrayList<GameObject>();
		player.removalMap = new HashMap<GameObject, GameObject>();
		return writer;
    }
	
	public static MsgWriter sendLeaderboard(List<GameObject> toSend, Player playerA) {
        MsgWriter writer = new MsgWriter();
        writer.writeUInt8(8);
        writer.writeUInt8(playerA.getRank());
        if (toSend.contains(playerA)) {
            writer.writeUInt8(toSend.size());
        } else {
            writer.writeUInt8(toSend.size() + 1);
        }
        for (GameObject player : toSend) {
            Player player1 = (Player)player;
            writer.writeUInt8(player1.getRank());
            writer.writeString(player1.getPlayerName());
            writer.writeUInt32(player1.getScore());
        }
        if (!toSend.contains(playerA)) {
            writer.writeUInt8(playerA.getRank());
            writer.writeString(playerA.getPlayerName());
            writer.writeUInt32(playerA.getScore());
        }
        return writer;
    }
	
	public static MsgWriter sendJoinPacket(Room room, Player player, int i) {
		MsgWriter writer = new MsgWriter();
		writer.writeUInt8(2); 
		if(room.playerCount >= 10) writer.writeUInt8(0); 
		else {
		writer.writeUInt8(1); 
		writer.writeUInt8(i); 
		writer.writeUInt32(player.getId());
		
		writer.writeUInt16((short) room.getId());
		writer.writeUInt16((short) Constants.GAME_WIDTH);
		writer.writeUInt16((short) Constants.GAME_HEIGHT);
		
		writer.writeUInt16((short) player.getX());
		writer.writeUInt16((short) player.getY());
		
		writer.writeUInt16((short) player.getZoom());
		
		/* ArrayList<GameObject> obstacles = new ArrayList<GameObject>();
		for(GameObject o : room.objects) {
			if(o.getType() == Constants.OBSTACLE) obstacles.add(o);
		}
		
		ArrayList<GameObject> spots = new ArrayList<GameObject>();
		for(GameObject o : room.objects) {
			if(o.getType() == Constants.WATER_SPOT) spots.add(o);
		}
		
		ArrayList<GameObject> foodspots = new ArrayList<GameObject>();
		for(GameObject o : room.objects) {
			if(o.getType() == Constants.BUSH) foodspots.add(o);
		}
		
		writer.writeUInt16((short) 0);
		writer.writeUInt16((short) spots.size());
		for(GameObject o : spots) {
			writer.writeUInt8(o.getX() / (Constants.GAME_WIDTH / 200));
			writer.writeUInt8(o.getY() / (Constants.GAME_WIDTH / 200));
			writer.writeUInt8(o.getRadius() / 20);
		}
		writer.writeUInt16((short) 0);
		writer.writeUInt16((short) 0);
		writer.writeUInt16((short) obstacles.size());
		for(GameObject o : obstacles) {
			writer.writeUInt8(o.getX() / (Constants.GAME_WIDTH / 200));
			writer.writeUInt8(o.getY() / (Constants.GAME_WIDTH / 200));
			writer.writeUInt8(o.getRadius() / 20);
		}
		writer.writeUInt16((short) foodspots.size());
		for(GameObject o : foodspots) {
			writer.writeUInt8(o.getX() / (Constants.GAME_WIDTH / 200));
			writer.writeUInt8(o.getY() / (Constants.GAME_WIDTH / 200));
			writer.writeUInt8(o.getRadius() / 20);
		} */
		}
		
		return writer;
	}
	
	public static MsgWriter sendDeath(Player player, int reason) {
		MsgWriter writer = new MsgWriter();
		writer.writeUInt8(14); 
		writer.writeUInt8(reason); 
		player.setScore((int) Math.round(player.getScore() / 2.5));
		writer.writeUInt32(player.getScore()); 
		
		return writer;
	}
	
	public static MsgWriter upgrade(Player player) {
		MsgWriter writer = new MsgWriter();
		writer.writeUInt8(18);
		writer.writeUInt8(player.getAnimal());
		writer.writeUInt32(Tools.getNextAnimalGrowth(player));
		
		writer.writeUInt8(14 - player.getAnimal());
		for(int i = 0; i < 14 - (player.getAnimal()); i++) writer.writeUInt8((14 - i));
		writer.writeUInt8(player.getAnimal()-1);
		for(int i = 1; i < player.getAnimal(); i++) writer.writeUInt8(i);
		
		writer.writeUInt8(0);
		int edibleAmount = 0;
		if(player.getAnimal() >= Constants.MOUSE && player.getAnimal() != Constants.DEER) edibleAmount++;
		if(player.getAnimal() >= Constants.PIG && player.getAnimal() != Constants.CHEETAH) edibleAmount++;
		if(player.getAnimal() >= Constants.DEER && player.getAnimal() != Constants.CHEETAH) edibleAmount++;
		
		writer.writeUInt8(edibleAmount);
		if(player.getAnimal() != Constants.DEER) writer.writeUInt8(6);
		if(player.getAnimal() >= Constants.DEER && player.getAnimal() != Constants.CHEETAH) writer.writeUInt8(Constants.LILYPAD);
		if(player.getAnimal() >= Constants.PIG && player.getAnimal() != Constants.CHEETAH) writer.writeUInt8(8);
		
		return writer;
	}
	
	public static MsgWriter updatePlayer(Room room) {
		MsgWriter writer = new MsgWriter();
		writer.writeUInt8(10); 
		writer.writeUInt16(room.playerCount);
		
		return writer;
	}

}
