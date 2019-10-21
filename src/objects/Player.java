package objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import main.Constants;
import tools.NetworkingTools;
import tools.Tools;

public class Player extends GameObject {
	
	private int score = 0, cooldown = 0, rank = 0, zoom = Constants.DEFAULT_ZOOM, angle = 0, mouseX = 0, mouseY = 0, animal = Constants.MOUSE, water = 100, waterTimer = Constants.COOLDOWN;
	private GameClient client;
	private boolean spectator = true, mouseDown = false;
	private String playerName;
	
	private int airLevel = 100;
	private int airTimer = Constants.COOLDOWN;
	
	private int flag = Constants.FLAG_INVINCIBLE;
	
	private boolean holdingW = false;
	
	public int getWaterTimer() {
		return waterTimer;
	}

	public void setWaterTimer(int waterTimer) {
		this.waterTimer = waterTimer;
	}

	public boolean isHoldingW() {
		return holdingW;
	}

	public void setHoldingW(boolean holdingW) {
		this.holdingW = holdingW;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getWater_pending_removal() {
		return water_pending_removal;
	}

	public void setWater_pending_removal(int water_pending_removal) {
		this.water_pending_removal = water_pending_removal;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<GameObject> getToAdd() {
		return toAdd;
	}

	public void setToAdd(List<GameObject> toAdd) {
		this.toAdd = toAdd;
	}

	public List<GameObject> getToRemove() {
		return toRemove;
	}

	public void setToRemove(List<GameObject> toRemove) {
		this.toRemove = toRemove;
	}

	public Map<GameObject, GameObject> getRemovalMap() {
		return removalMap;
	}

	public void setRemovalMap(Map<GameObject, GameObject> removalMap) {
		this.removalMap = removalMap;
	}

	public boolean isSpawned() {
		return spawned;
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	public void setLastAni(int lastAni) {
		this.lastAni = lastAni;
	}

	private int lastAni = 0;
	private boolean inWater = false;
	
	private int first = 1;
	
	private int water_pending_removal = 0;
	
	private boolean inHole = false;
	private int hole = 0;
	
	private long createdAt = System.currentTimeMillis();
	private long lastUpdate = System.currentTimeMillis();
	
	public List<GameObject> toAdd = new CopyOnWriteArrayList<GameObject>();
	public List<GameObject> toRemove = new CopyOnWriteArrayList<GameObject>();
	public Map<GameObject, GameObject> removalMap = new HashMap<GameObject, GameObject>();
	
	public boolean spawned = true;
	
	public Player(int id, int x, int y, String playerName, GameClient client) {
		super(id, x, y, Constants.MOUSE_MIN, Constants.ANIMAL);
		
		this.client = client;
		this.playerName = playerName;
	}

	public void reset() {
		this.zoom = Constants.DEFAULT_ZOOM;
		this.angle = 0;
		this.flag = Constants.FLAG_INVINCIBLE;
		this.rank = 0;
		this.first = 1;
		this.spawned = true;
		this.cooldown = 0;
		this.setCreatedAt(System.currentTimeMillis());
		this.mouseDown = false;
		this.mouseX = 0;
		this.inHole = false;
		this.hole = 0;
		this.mouseY = 0;
		this.water = 100;
		this.animal = Constants.MOUSE;
	}
	
	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getWater() {
		return water;
	}

	public void setWater(int water) {
		this.water = Math.min(water, 100);
	}

	public int getAnimal() {
		return animal;
	}

	public void setAnimal(int animal) {
		this.animal = animal;
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX * 4;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY * 4;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public boolean isMouseDown() {
		return mouseDown;
	}

	public void setMouseDown(boolean mouseDown) {
		this.mouseDown = mouseDown;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	public int getScore() {
		return score;
	}

	public boolean isSpectator() {
		return spectator;
	}

	public void setSpectator(boolean spectator) {
		this.spectator = spectator;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public void update() {
		long time = (System.currentTimeMillis() - lastUpdate) / 40;
		
		if(spectator) {
			if(getX() >= Constants.GAME_WIDTH * 4) {
				setVelocityX((int) (-Constants.DEFAULT_SPEED * time));
			}
			
			if(getY() >= Constants.GAME_HEIGHT * 4) {
				setVelocityY((int) (-Constants.DEFAULT_SPEED * time));
			}
			
			if(this.getX() < this.getRadius()) {
				this.setX(this.getRadius() - 1);
				setVelocityX((int) (Constants.SPEED * time));
			}
			if(this.getY() < this.getRadius()) {
				this.setY(this.getRadius() - 1);
				setVelocityY((int) (Constants.SPEED * time));
			}
			setCreatedAt(System.currentTimeMillis());
		} else {
			int speed = Constants.SPEED;
			
			if(this.getX() <= 450 * 4) this.inWater = true;
			else if(this.getX() >= Constants.GAME_WIDTH * 4 - 450 * 4) this.inWater = true;
			else this.inWater = false;
			
			long amount = this.spawned ? 5000 : 3000;
			if(this.flag == Constants.FLAG_INVINCIBLE && (System.currentTimeMillis() - getCreatedAt()) >= amount) {
				this.flag = 0;
				this.spawned = false;
			}
			
			if(this.flag != Constants.FLAG_INVINCIBLE && this.holdingW && this.inWater) this.flag = Constants.FLAG_UNDERWATER;
			if(this.flag == Constants.FLAG_UNDERWATER && (!this.inWater || !this.holdingW)) {
				this.flag = 0;
				this.airLevel = 100;
			}
			
			if(this.animal < Constants.DINO && this.score >= Tools.getNextAnimalGrowth(this)) {
				this.lastAni = Tools.getNextAnimalGrowth(this);
				this.animal = Tools.getNextLogicalAnimal(this);
				this.water = 100;
				this.createdAt = System.currentTimeMillis();
				this.flag = Constants.FLAG_INVINCIBLE;
				try {
					NetworkingTools.sendMessage(this.getClient().getSocket(), NetworkingTools.upgrade(this));
				} catch(Exception e) {}
			}
			
			this.zoom = Constants.ZOOM;
			double percentage = (double) (this.score - this.lastAni) / (double) (Tools.getNextAnimalGrowth(this) - this.lastAni);
			percentage *= 100d;
			if(this.animal >= Constants.DINO) percentage = 100;
			int radius = (int) ((int) (Tools.getMinSize(this)) + Math.round(percentage) / 3);
			setRadius(radius);
			this.zoom -= radius * 10;
			
			if(this.animal >= Constants.DINO) {
				this.zoom = Constants.DEFAULT_ZOOM;
				setRadius(150);
				this.zoom -= getRadius() * 10;
				speed = Constants.SPEED - 5;
			}
			if(this.airTimer < 0) {
				this.airTimer = Constants.COOLDOWN;
				this.airLevel-=1;
			}
			if(this.airLevel <= 0) {
				this.airLevel = 100;
				this.flag = 0;
			}
			if(this.waterTimer < 0) {
				this.waterTimer = Constants.COOLDOWN;
				if(this.inWater) this.water = Math.min(this.water + 2, 100);
				else this.water_pending_removal += 2;
			}
			if(this.animal == Constants.BEAR) speed = Constants.SPEED - 5;
			if(this.mouseDown && this.cooldown < 0 && this.water >= 30) {
       	       speed = Constants.BOOST_SPEED;
       	       if(this.animal == Constants.CHEETAH) speed += 50;
       		   this.water_pending_removal += 2;
       	       this.cooldown = Constants.COOLDOWN;
       		}
			
			boolean inMud = false;
			
			if(this.water < 25) {
				this.setFlag(Constants.FLAG_LOW_WATER_NO_HP);
			} else {
				if(this.flag == Constants.FLAG_LOW_WATER_NO_HP) this.setFlag(0);
			}
			this.cooldown -= (1 * time);
			this.waterTimer -= (1 * time);
			this.airTimer -= (10 * time);
			
			if(this.water <= 0) getClient().getRoom().killPlayer(this, Constants.DEATH_WATER, this);
			
			if(this.flag != Constants.FLAG_UNDERWATER) {
			for(GameObject object : getClient().getRoom().objects) {
				if(object.getType() == Constants.MUD) {
					if(this.client.getRoom().collisionTest(this, object)) {
						inMud = true;
						if(!Tools.fastInMud(this)) speed = speed / 3;
					}
				} else if(object.getType() != Constants.MUD && object.getId() != getId()) {
					if(animal != Constants.DINO) {
						if(object.getType() == Constants.MUSHROOM) {
							if(this.animal < Constants.PIG || this.animal == Constants.CHEETAH) getClient().getRoom().collision(this, object);
						} else {
							if(object.getType() == Constants.OBSTACLE && this.animal != Constants.BEAR) getClient().getRoom().collision(object, this);
							else if(object.getType() != Constants.GREEN_BUSH && object.getType() != Constants.FOOD && object.getType() != Constants.WATER) {
								if(this.animal < Constants.FOX && object.getType() == Constants.BUSH) getClient().getRoom().collision(object, this);
								if(this.animal == Constants.CROC) {
									if(object.getType() == Constants.BUSH) getClient().getRoom().collision(object, this);
								} else {
									if(object.getType() == Constants.WATER_SPOT && this.animal != Constants.HIPPO) getClient().getRoom().collision(object, this);
								}
							}
						}
					}
					
					if(object.getType() == Constants.ANIMAL && ((Player) object).getAnimal() == this.animal) getClient().getRoom().collision(object, this);
					else if(object.getType() == Constants.ROCK) getClient().getRoom().collision(object, this);
					else if(object.getType() == Constants.LILYPAD && (this.animal < Constants.DEER || this.animal == Constants.CHEETAH)) getClient().getRoom().collision(this, object);
				}
			}
			}
			
			if(this.flag != Constants.FLAG_UNDERWATER && this.inWater && !Tools.fastInMud(this)) speed = speed / 3;
			if(this.flag == Constants.FLAG_UNDERWATER) speed = speed / 3;
			
			this.angle = Tools.getAngle(this, 0);
			this.setVelocityX((int) (Math.cos(Math.toRadians(this.angle)) * (int) (speed)));
			this.setVelocityY((int) (Math.sin(Math.toRadians(this.angle)) * (int) (speed)));
			
			if(!inMud && this.flag != Constants.FLAG_INVINCIBLE) this.water -= this.water_pending_removal;
			this.water_pending_removal = 0;
		}
		
		setX(getX() + getVelocityX());
		setY(getY() + getVelocityY());
		
		if(!spectator) {
			if(this.getX() < this.getRadius()) {
				this.setX(this.getRadius() - 1);
			}
			if(this.getY() < this.getRadius()) {
				this.setY(this.getRadius() - 1);
			}
			
			if(this.getX() >= (Constants.GAME_WIDTH * 4) - this.getRadius()) {
				setX((Constants.GAME_WIDTH * 4) - this.getRadius() - 1);
			}
			
			if(this.getY() >= (Constants.GAME_HEIGHT * 4) - this.getRadius()) {
				setY((Constants.GAME_HEIGHT * 4) - this.getRadius() - 1);
			}
			
			for(GameObject object : this.client.getRoom().objects) {
				if(object.getType() == Constants.ANIMAL && object.getId() != this.getId() && !((Player) object).isSpectator() && this.animal > ((Player) object).animal && this.client.getRoom().collisionTest(this, object)) {
					double percentage = (double) ((Player) object).score / (double) Tools.getNextAnimalGrowth(((Player) object));
					percentage *= 100d;
					if(((Player) object).animal >= Constants.DINO) percentage = 100;
					if(percentage <= 25 || ((Player) object).animal == Constants.DINO) {
						this.client.getRoom().killPlayer((Player) object, Constants.DEATH_EATEN, this);
						
						for(int i = 0; i < 3; i++) {
							int x = ThreadLocalRandom.current().nextInt(((Player) object).getX() - ((Player) object).getRadius(), ((Player) object).getX() + ((Player) object).getRadius());
							int y = ThreadLocalRandom.current().nextInt(((Player) object).getY() - ((Player) object).getRadius(), ((Player) object).getY() + ((Player) object).getRadius());
							int radius = Constants.SMALL_FOOD;
							
							GameObject water = new Water(this.client.getRoom().object_id, x, y, radius, Constants.WATER, this.client.getRoom(), null);
							
							this.client.getRoom().object_id++;
							this.client.getRoom().objects.add(water);
							for(GameObject player : this.client.getRoom().objects) if(player.getType() == Constants.ANIMAL) ((Player) player).toAdd.add(water);
						}
					}
					else {
						((Player) object).setX((int) (Math.cos(Math.toRadians(this.angle)) * 270));
						((Player) object).setY((int) (Math.sin(Math.toRadians(this.angle)) * 270));
						
						this.score += Math.round(((Player) object).score / 2) > 0 ? Math.round(((Player) object).score / 2) : 1;
						((Player) object).score = Math.round(((Player) object).score / 2);
					}
				}
			}
		}
		
		
		if(this.inHole && !this.getClient().getRoom().collisionTest(this, (GameObject) this.getClient().getRoom().objects.get(hole))) {
			for(GameObject player : this.getClient().getRoom().objects) {
				if(player.getType() == Constants.ANIMAL) {
					((Player) player).toAdd.add(this);
				}
			}
			
			this.inHole = false;
			this.hole = 0;
		}
		
		this.lastUpdate = System.currentTimeMillis();;
		NetworkingTools.sendMessage(getClient().getSocket(), NetworkingTools.sendUpdatePacket(getClient().getRoom(), this, this.first));
		first = 0;
	}

	public GameClient getClient() {
		return client;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public double getLastAni() {
		return (double) lastAni;
	}

	public boolean isInHole() {
		return inHole;
	}

	public void setInHole(boolean inHole) {
		this.inHole = inHole;
	}

	public int getHole() {
		return hole;
	}

	public void setHole(int hole) {
		this.hole = hole;
	}

	public int getAirLevel() {
		return this.airLevel;
	}

}
