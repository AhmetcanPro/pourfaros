package main;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.drafts.Draft_6455;

import objects.GameClient;
import server.Server;
import tools.NetworkingTools;
import world.Room;

public class Main {
	
	public ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();
	
	public Main() {
		for(int i = 1; i < Constants.ROOM_AMOUNT + 1; i++) {
			Room room = new Room(i, this);
			
			rooms.put(i, room);
			new Thread(room).start();
		}
		
		Server test = null;
		try {
			test = new Server(7020, new Draft_6455(), this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		test.setConnectionLostTimeout(0);
		test.start();
	}

	public static void main(String[] args) {
		new Main();
	}
	
	public void handleNewClient(GameClient client, Room room) {
		client.getSocket().send(NetworkingTools.initClient(room).getData());
	}

}

