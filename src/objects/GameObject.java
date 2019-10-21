package objects;

import main.Constants;

public class GameObject implements Comparable<GameObject> {
	
	private int type;
	private int x;
	private int y;
	private int radius;
	private int id;
	private int velocityX;
	private int velocityY;
	
	public GameObject(int id, int x, int y, int radius, int type) {
		this.type = type;
		this.id = id;
		this.x = Math.max(x, 0);
		this.y = Math.max(y, 0);
		this.radius = radius;
		
		this.setVelocityX(0);
		this.setVelocityY(0);
	}

	@Override
	public int compareTo(GameObject o) {
		if(o.type != Constants.ANIMAL) return 0;
		
		int score = 0;
		score = ((Player) o).getScore() - ((Player) this).getScore();
		if(score == 0) score = ((Player) o).getId() - ((Player) this).getId();
		
		return score;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = Math.max(x, 0);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = Math.max(y, 0);
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void update() {
		setX(getX() + getVelocityX());
		setY(getY() + getVelocityY());
		
		if(this.x >= (Constants.GAME_WIDTH * 4) - this.radius) {
			this.x = (Constants.GAME_WIDTH * 4) - this.radius - 1;
		}
		
		if(this.y >= (Constants.GAME_HEIGHT * 4) - this.radius) {
			this.y = (Constants.GAME_HEIGHT * 4) - this.radius - 1;
		}
	}

	public int getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(int velocityX) {
		this.velocityX = velocityX;
	}

	public int getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(int velocityY) {
		this.velocityY = velocityY;
	}

}
