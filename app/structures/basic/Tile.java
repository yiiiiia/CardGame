package structures.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A basic representation of a tile on the game board. Tiles have both a pixel
 * position and a grid position. Tiles also have a width and height in pixels
 * and a series of urls that point to the different renderable textures that a
 * tile might have.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Tile {
	public static final int TILE_NORMAL_MODE = 0;
	public static final int TILE_WHITE_MODE = 1;
	public static final int TILE_RED_MODE = 2;

	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
																// objects from a file

	private List<String> tileTextures;
	private int xpos;
	private int ypos;
	private int width;
	private int height;
	private int tilex;
	private int tiley;
	private Unit unit;

	public Tile() {
	}

	public Tile(String tileTexture, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		tileTextures = new ArrayList<String>(1);
		tileTextures.add(tileTexture);
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
	}

	public Tile(List<String> tileTextures, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		this.tileTextures = tileTextures;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
	}

	public List<String> getTileTextures() {
		return tileTextures;
	}

	public void setTileTextures(List<String> tileTextures) {
		this.tileTextures = tileTextures;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getTilex() {
		return tilex;
	}

	public void setTilex(int tilex) {
		this.tilex = tilex;
	}

	public int getTiley() {
		return tiley;
	}

	public void setTiley(int tiley) {
		this.tiley = tiley;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public void clearUnit() {
		this.unit = null;
	}

	public boolean isOccupied() {
		return unit != null;
	}

	/**
	 * Loads a tile from a configuration file parameters.
	 * 
	 * @param configFile
	 * @return
	 */
	public static Tile constructTile(String configFile) {
		try {
			Tile tile = mapper.readValue(new File(configFile), Tile.class);
			return tile;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tilex;
		result = prime * result + tiley;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (tilex != other.tilex)
			return false;
		if (tiley != other.tiley)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tile [xpos=" + xpos + ", ypos=" + ypos + "]";
	}
}
