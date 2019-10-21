package objects;

import java.util.concurrent.ThreadLocalRandom;

import org.java_websocket.WebSocket;

import main.Constants;
import server.MsgReader;
import server.MsgWriter;
import tools.NetworkingTools;
import world.Room;

public class GameClient {
	
	private WebSocket socket;
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setSocket(WebSocket socket) {
		this.socket = socket;
	}

	private Player player = null;
	private Room room;
	
	public GameClient(WebSocket socket, Room room) {
		this.socket = socket;
		this.room = room;
	}

	public void onclose() {
		try {
			this.room.removePlayer(this.player);
		} catch(Exception ex) {}
	}

	public void onopen() {
		
	}

	public void onmessage(MsgReader reader) {
		int type = 0;
		try {
			type = reader.readUInt8();
		} catch(Exception e) {
			return;
		}
		
		switch(type) {
		case 2:
			try {
					String playerName = reader.readString();
					boolean isSpectator = reader.readUInt8() == 2 ? true : false;
					
					if(isSpectator) {
						Player player = new Player(this.room.object_id, ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1), ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1), playerName.trim(), this);
						room.object_id++;
						this.player = player;
						
						room.addSpectator(this.player);
					} else {
						this.player.setPlayerName(playerName.trim());
						this.player.setSpectator(isSpectator);
						
						this.player.setX(ThreadLocalRandom.current().nextInt(0, (Constants.GAME_WIDTH * 4) + 1));
						this.player.setY(ThreadLocalRandom.current().nextInt(0, (Constants.GAME_HEIGHT * 4) + 1));
						this.player.setZoom(Constants.ZOOM);
						
						this.room.initPlayer(this.player);
					}
		    } catch(Exception e) {
		    	socket.close();
		    }
			break;
		case 5:
			try {
				player.setMouseX(reader.readInt16());
				player.setMouseY(reader.readInt16());
			} catch(Exception e) {
				socket.close();
			}
			break;
		case 21:
			try {
			player.setMouseDown(reader.readUInt8() == 0 ? false : true);
			} catch(Exception e) {
				socket.close();
			}
			break;
		case 255:
			MsgWriter writerForPing = new MsgWriter();
			writerForPing.writeUInt8(255);
			
			NetworkingTools.sendMessage(this.getSocket(), writerForPing);
			break;
		case 20:
			try {
				this.player.setHoldingW(reader.readUInt8() == 1 ? true : false);
			} catch(Exception e) {
				
			}
		case 19:
			try {
				if(!player.isSpectator()) {
					String message = reader.readString().trim();
					if(!message.equals("")) {
						if(message.startsWith(".setflagxp")) {
							player.setScore(Integer.parseInt(message.split(" ")[1]));
						} else if(message.startsWith(".setflagani")) {
							player.setAnimal(Integer.parseInt(message.split(" ")[1]));
							player.setWater(100);
							player.setCreatedAt(System.currentTimeMillis());
							player.setFlag(Constants.FLAG_INVINCIBLE);
							NetworkingTools.sendMessage(socket, NetworkingTools.upgrade(player));
						} else { 
							MsgWriter writer = new MsgWriter();
							writer.writeUInt8(19);
							writer.writeUInt32(player.isInHole() ? player.getHole() : player.getId());
							writer.writeString(message);
							
							for(GameObject object : this.room.objects) {
								if(object.getType() == Constants.ANIMAL) NetworkingTools.sendMessage(((Player) object).getClient().getSocket(), writer);
							}
					    }
				}
				}
			} catch(Exception e) {
					socket.close();
			}
			break;
		}
	}

	public WebSocket getSocket() {
		return socket;
	}

}
