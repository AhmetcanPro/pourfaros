package tools;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import objects.GameObject;

public class GameList implements Iterable<GameObject> {
	
	public ConcurrentHashMap<Integer, GameObject> gameMap = new ConcurrentHashMap<Integer, GameObject>();
	
	public GameList(GameList copy) {
		gameMap = new ConcurrentHashMap<Integer, GameObject>(copy.gameMap);
	}
	
	public GameList() {
		
	}
	
	public Object get(int id) {
		return gameMap.get(id);
	}
	
	public void add(GameObject object) {
		gameMap.put(object.getId(), object);
	}
	
	public void add(int id, GameObject object) {
		gameMap.put(object.getId(), object);
	}
	
	public void remove(GameObject object) {
		gameMap.remove(object.getId(), object);
	}

	@Override
	public Iterator<GameObject> iterator() {
		return gameMap.values().iterator();
	}

	public int size() {
		return gameMap.size();
	}

}
