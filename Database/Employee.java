package Database;
import java.util.Objects;

/**
 * Represents an employee at FM
 * @author Joshua Cohen
 *
 */
public class Employee implements Comparable<Employee>{

	/* Instance Variables */
	private String firstname, lastname, position, department, room_number, cell, extension;
	private int gridX, gridY;
	
	/* Constructor */
	
	public Employee(String firstname, String lastname, String position, String department, String room_number, String cell, String extension,
			int gridX, int gridY) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.position = position;
		this.department = department;
		this.room_number = room_number;
		this.cell = cell;
		this.extension = extension;
		this.gridX = gridX;
		this.gridY = gridY;
	}
	
	/* Get and Set Methods */
	/* All fields immutable */
	
	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getPosition() {
		return position;
	}

	public String getDepartment() {
		return department;
	}

	public String getRoom_number() {
		return room_number;
	}

	public String getCell() {
		return cell;
	}

	public String getExtension() {
		return extension;
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridY() {
		return gridY;
	}

	@Override
	public String toString() {
		return "Employee [firstname=" + firstname + ", lastname=" + lastname + ", position=" + position
				+ ", department=" + department + ", room_number=" + room_number + ", cell=" + cell + ", extension="
				+ extension + ", gridX=" + gridX + ", gridY=" + gridY + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cell, department, extension, firstname, gridX, gridY, lastname, position, room_number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Employee))
			return false;
		Employee other = (Employee) obj;
		return cell.equals(other.cell) && Objects.equals(department, other.department) && Objects.equals(extension, other.extension)
				&& Objects.equals(firstname, other.firstname) 
				&& Objects.equals(lastname, other.lastname) && Objects.equals(position, other.position)
				&& Objects.equals(room_number, other.room_number);
	}

	@Override
	public int compareTo(Employee o) {
		return (firstname.compareTo(o.firstname) == 0 ? lastname.compareTo(o.lastname) : firstname.compareTo(o.firstname));
	}
	
}
