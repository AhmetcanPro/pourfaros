package server;

import java.net.InetSocketAddress;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import main.Constants;
import main.Main;
import objects.GameClient;

public class Server extends WebSocketServer {
	
    private Main main;
	public ConcurrentHashMap<WebSocket, GameClient> sockets = new ConcurrentHashMap<WebSocket, GameClient>();
	
	public Server(int port, Draft d, Main main) throws UnknownHostException {
		super(new InetSocketAddress(port), Collections.singletonList(d));
		this.main = main;
	}

	public Server(InetSocketAddress address, Draft d, Main main) {
		super(address, Collections.singletonList(d));
		this.main = main;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		if(sockets.get(conn) == null) {	
			int id = Math.max(ThreadLocalRandom.current().nextInt(Constants.ROOM_AMOUNT), 1);
		    GameClient newClient = new GameClient(conn, main.rooms.get(id));
		    
			sockets.put(conn, newClient);
			newClient.onopen();
			main.handleNewClient(newClient, main.rooms.get(id));
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		try {
			sockets.get(conn).onclose();
			sockets.remove(conn);
		} catch(Exception e) {}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer blob) {
		try {
			sockets.get(conn).onmessage(new MsgReader(blob.array()));
		} catch(Exception e) {}
	}

	@Override
	public void onMessage(WebSocket arg0, String arg1) {}

}