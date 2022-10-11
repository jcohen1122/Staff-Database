package Database;
import java.util.Collection;
import java.util.Objects;

/**
 * A room in the FM building (Huddle, Conference, Etc)
 * @author Joshua Cohen
 *
 */
public class Room implements Comparable<Room>{
	
	/* Instance Variables */
	private Collection<Tags> tags;
	private String room_number;
	private int gridX, gridY;
	
	/* Constructor */
	
	public Room(Collection<Tags> tags, String room_number, int gridX, int gridY) {
		this.tags = tags;
		this.room_number = room_number;
		this.gridX = gridX;
		this.gridY = gridY;
	}

	/* Get/Set Methods */
	/* All fields immutable */
	
	public Collection<Tags> getTags() {
		return tags;
	}

	public String getRoom_number() {
		return room_number;
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridY() {
		return gridY;
	}

	/* Room as a string */
	@Override
	public String toString() {
		return "Room [tags=" + tags + ", room_number=" + room_number + ", gridX=" + gridX + ", gridY=" + gridY + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(gridX, gridY, room_number, tags);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Room))
			return false;
		Room other = (Room) obj;
		return  Objects.equals(room_number, other.room_number)
				&& Objects.equals(tags, other.tags);
	}

	@Override
	public int compareTo(Room o) {
		return room_number.compareTo(o.room_number);
	}
	
}
