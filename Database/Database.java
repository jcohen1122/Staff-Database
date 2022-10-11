package Database;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A map of FM employees and rooms
 * @author Joshua Cohen
 *
 */
public class Database {

	/* Database instance */
	Collection<Object> database;
	//Chrome instance
	WebDriver driver;

	/* Constructor */
	public Database() throws IOException {
		database = new HashSet<>();
		load();
	}

	/* Open Chrome on start of program */
	public void startChrome() {
		//Open google
		System.setProperty("webdriver.chrome.driver", "/Users/jryan/Desktop/Java Libraries/chromedriver");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200",
				"--ignore-certificate-errors","--disable-extensions","--no-sandbox",
				"--disable-dev-shm-usage");
		driver = new ChromeDriver(options);
	}

	/* Quit chrome instance */
	public void quitChrome() {
		driver.quit();
	}

	/* Search UMD directory */
	public String directorySearch(String query) {	
		//Open directory
		driver.get("https://identity.umd.edu/search");
		WebElement input = driver.findElement(By.id("basicSearchInput"));
		//Add query to search
		input.sendKeys(query);

		//search
		WebElement search = driver.findElement(By.name("basicSearch"));		
		WebElement click = new WebDriverWait(driver, Duration.ofMillis(50)).until(ExpectedConditions.elementToBeClickable(search));
		click.click();

		//get results
		List<String> results = new ArrayList<String>();
		List<WebElement> pagination = driver.findElements(By.className("DisplaySearchItem"));

		results.add("\""+ query + "\"\nResults: " + pagination.size()+"\n");

		//parse thru results
		for (WebElement element : pagination) {
			StringBuffer result = new StringBuffer();
			WebElement name = element.findElement(By.className("name"));
			WebElement email = element.findElement(By.className("email"));
			WebElement inst = element.findElement(By.className("institution"));
			WebElement title = element.findElement(By.className("displayTitle"));
			WebElement dept = element.findElement(By.className("deptName"));
			WebElement address = element.findElement(By.className("address"));
			WebElement phone = element.findElement(By.className("phoneNumbers"));

			result.append(name.getText() + "\n" + email.getText() + "\n\n"
					+ inst.getText() + "\n" + title.getText() + "\n" + dept.getText() + "\n\n"
					+address.getText() + "\n" + phone.getText());

			results.add(result.toString());
		}

		String displayResults = compileDirSearch(results);
		return displayResults;
	}

	/* Make directory results easier to read */
	private String compileDirSearch(List<String> results) {
		StringBuffer toReturn = new StringBuffer();

		for (String str : results) {
			toReturn.append(str);
			toReturn.append("\n--------------------------------------------------------------");
			toReturn.append("-------------------------------------------------------");
			toReturn.append("\n");
		}

		return toReturn.toString();
	}

	/* Search methods */
	public Collection<Object> search(String query) {
		if (query.isEmpty()) {
			return null;
		}

		Collection<Object> results = new TreeSet<>();

		if (query.toLowerCase().equals("employees")) {
			for (Object o : database) {
				if (o instanceof Employee) {
					results.add((Employee)o);
				}
			}
		} else if (query.toLowerCase().equals("rooms")) {
			for (Object o : database) {
				if (o instanceof Room) {
					results.add((Room)o);
				}
			}
		} else {

			for (Object o : database) {
				if (o instanceof Employee) {
					Employee e = (Employee) o;

					//for button action on gui
					if (query.equals(e.getGridX() + "-" + e.getGridY()+"-"+e.getRoom_number().substring(0,1))) {
						results.add(o);
						break;
					}

					//check partial first and last
					try {
						if (query.toLowerCase().equals(e.getFirstname().substring(0,query.length()).toLowerCase())) {
							results.add(o);
						}
						if (query.toLowerCase().equals(e.getLastname().substring(0,query.length()).toLowerCase())) {
							results.add(o);
						}
					} catch (Exception ex) {

					}
					//check first and last names
					if (e.getFirstname().toLowerCase().equals(query.toLowerCase()) || e.getLastname().toLowerCase().equals(query.toLowerCase())) {
						results.add(o);
					}
					if ((e.getFirstname().toLowerCase() + " " + e.getLastname().toLowerCase()).equals(query.toLowerCase())) {
						results.add(o);
					}
					//check partial position
					for (int i = 0; i < e.getPosition().length()-query.length(); i++) {
						if (query.toLowerCase().equals(e.getPosition().substring(i,i+query.length()).toLowerCase())) {
							results.add(o);
						}
					}
					//check position and department
					if (e.getPosition().toLowerCase().equals(query.toLowerCase()) || e.getDepartment().toLowerCase().equals(query.toLowerCase())) {
						results.add(o);
					}
					//partial room number
					for (int i = 0; i < e.getRoom_number().length()-query.length(); i++) {
						if (query.toLowerCase().equals(e.getRoom_number().substring(i,i+query.length()).toLowerCase())) {
							results.add(o);
						}
					}
					//check room number
					if (e.getRoom_number().toLowerCase().equals(query.toLowerCase())) {
						results.add(o);
					}
					//check cell and extension
					if (e.getCell().equals(query) || e.getExtension().equals(query)) {
						results.add(o);
					}
				} else if (o instanceof Room) {
					Room r = (Room) o;
					//for button action on gui
					if (query.equals(r.getGridX() + "-" + r.getGridY()+"-"+r.getRoom_number().substring(0,1)) || 
							query.equals(r.getGridX() + "-" + r.getGridY()+"-"+r.getRoom_number().substring(1,2))){
						results.add(o);
						break;
					}

					//check tags
					for (Tags tag : r.getTags()) {
						if (tag.toString().toLowerCase().equals(query.toLowerCase())) {
							if (!results.contains(r)) {
								results.add(o);
							}
						}
					}
					//partial room number
					for (int i = 0; i < r.getRoom_number().length()-query.length(); i++) {
						if (query.toLowerCase().equals(r.getRoom_number().substring(i,i+query.length()).toLowerCase())) {
							results.add(o);
						}
					}
					//check room number
					if (r.getRoom_number().toLowerCase().equals(query.toLowerCase())) {
						results.add(o);
					}
				}
			}
		}
		return results;
	}

	/* Makes results easier to read */
	public String compileResults(Collection<Object> results) {
		StringBuffer toReturn = new StringBuffer();
		try {
			toReturn.append("------------------------------------------------------------------------------------------------------------\n");
			for (Object o : results) {
				if (o instanceof Employee) {
					Employee e = (Employee) o;
					toReturn.append(e.getFirstname() + " " + e.getLastname() + "\n");
					toReturn.append(e.getPosition() + " in " + e.getDepartment() + "\n");
					toReturn.append("Room: " + e.getRoom_number() + "\n");
					toReturn.append("Cell: " + e.getCell() + "\nExtension: " + e.getExtension()+"\n");
				} else if (o instanceof Room) {
					Room r = (Room) o;
					toReturn.append("Room " + r.getRoom_number() + "\n");
					for (Tags tag : r.getTags()) {
						toReturn.append(" | " + tag + " | ");
					}
					toReturn.append("\n");
				} else {
					toReturn.append(o);
				}
				toReturn.append("------------------------------------------------------------------------------------------------------------\n");
			}

		} catch (Exception ex) {

		}

		return toReturn.toString();
	}

	public String compileResults(Collection<Object> results, String query) {
		StringBuffer toReturn = new StringBuffer();
		try {
			toReturn.append("\""+query+"\"\n");
			toReturn.append("Results: " + results.size()+"\n\n");
			toReturn.append("------------------------------------------------------------------------------------------------------------\n");
			for (Object o : results) {
				if (o instanceof Employee) {
					Employee e = (Employee) o;
					toReturn.append(e.getFirstname() + " " + e.getLastname() + "\n");
					toReturn.append(e.getPosition() + " in " + e.getDepartment() + "			");
					toReturn.append("Room: " + e.getRoom_number() + "\n");
					toReturn.append("Cell: " + e.getCell() + "			Extension: " + e.getExtension()+"\n");
				} else if (o instanceof Room) {
					Room r = (Room) o;
					toReturn.append("Room " + r.getRoom_number() + "\n");
					for (Tags tag : r.getTags()) {
						toReturn.append(" | " + tag + " | ");
					}
					toReturn.append("\n");
				} else {
					toReturn.append(o);
				}
				toReturn.append("------------------------------------------------------------------------------------------------------------\n");
			}

		} catch (Exception ex) {

		}

		return toReturn.toString();
	}

	/* Get object at grid space */
	public Object get(int x, int y) {
		for (Object o : database) {
			if (o instanceof Employee) {
				Employee e  = (Employee) o;
				if (e.getGridX() == x && e.getGridY() == y) {
					return e;
				}
			} else if (o instanceof Room) {
				Room r = (Room) o;
				if (r.getGridX() == x && r.getGridY() == y) {
					return r;
				}
			}
		}
		return null;
	}


	/* Save database to file */
	public void readToFile(String path) throws IOException {
		//create new file
		File file = new File(path);
		file.createNewFile();
		//read save state and write it to file
		FileWriter fileWriter = new FileWriter(file);

		//read in employees
		fileWriter.write("/*************** EMPLOYEES ***************/\n");
		for (Object o : database) {
			if (o instanceof Employee) {
				fileWriter.write(o+"\n");
			}
		}
		fileWriter.write("\n");

		//read in rooms
		fileWriter.write("/*************** ROOMS ***************/\n");
		for (Object o : database) {
			if (o instanceof Room) {
				fileWriter.write(o+"\n");
			}
		}

		fileWriter.close();
	}

	/* Load database from file */
	public void loadFromFile() throws IOException {
		File file = new File("C:\\Users\\jryan\\OneDrive\\Desktop\\My Programs\\FM Database\\src\\Database\\DatabaseFile");
		Scanner in = new Scanner(file);

		while (in.hasNext()) {
			//Employee or room
			String type = in.next();

			if (type.equals("Employee")) {
				//employee parameters
				String firstname=null,lastname=null,position=null,department=null,room_number=null,cell=null, extension = null;
				int gridX=0,gridY=0;

				//First name
				String next = in.next();
				firstname = next.substring(11, next.length()-1);

				//Last name
				next = in.next();
				lastname = next.substring(9, next.length()-1);

				//Position
				next = in.next();
				position = next.substring(9, next.length()-1);

				//Department
				next = in.next();
				department = next.substring(11, next.length()-1);

				//Room Number
				next = in.next();
				room_number = next.substring(12, next.length()-1);

				//Cell
				next = in.next();
				cell = next.substring(5, next.length()-1);

				//Extension
				next = in.next();
				extension = next.substring(10, next.length()-1);

				//Grid X
				next = in.next();
				gridX = Integer.valueOf(next.substring(6, next.length()-1));

				//Grid Y
				next = in.next();
				gridY = Integer.valueOf(next.substring(6, next.length()-1));

				//Wrap into employee object
				Employee e = new Employee(firstname,lastname,position,department,room_number,
						cell,extension,gridX,gridY);

				//add to database
				database.add(e);
			} else if (type.equals("Room")) {
				//room parameters
				Collection<Tags> tags;
				String room_number;
				int gridX,gridY;

				//Tags
				String next = in.next();
				tags = new HashSet<>();

				//consider ']' if there is only one tag
				Tags tag;
				if (next.substring(next.length()-2).equals("],")) {
					tag = Tags.getTag(next.substring(7, next.length()-2));
				} else {
					tag = Tags.getTag(next.substring(7, next.length()-1));
				}

				while (tag != null) {
					next = in.next();
					tags.add(tag);

					//consider ']' if there is only one tag
					if (next.substring(next.length()-2).equals("],")) {
						tag = Tags.getTag(next.substring(0,next.length()-2));
					} else {
						tag = Tags.getTag(next.substring(0,next.length()-1));
					}
				}

				//Room Number
				room_number = next.substring(12,next.length()-1);

				//GridX
				next = in.next();
				gridX = Integer.valueOf(next.substring(6,next.length()-1));

				//GridY
				next = in.next();
				gridY = Integer.valueOf(next.substring(6,next.length()-1));

				//Wrap into room object
				Room r = new Room(tags,room_number,gridX,gridY);

				//add to database
				database.add(r);
			}
		}
		in.close();
	}

	/* Alternative load method */
	public void load() throws IOException {
		InputStream stream = this.getClass().getResourceAsStream("DatabaseFile");

		//skip first line
		stream.skip(30);
		//skip line between employees and rooms
		boolean skipped = false;

		//read until there is nothing else to read
		while (stream.available() != 0) {
			//get type (room or employee)
			switch((char)stream.read()) {		
			case 'E':
				//employee parameters
				String firstname=null,lastname=null,position=null,department=null,room_number=null,cell=null, extension = null;
				int gridX=0,gridY=0;

				//first name
				firstname = findString(stream, 19, ',');
				//last name
				lastname = findString(stream, 10, ',');
				//position
				position = findString(stream, 10, ',');
				//department
				department = findString(stream, 12, ',');
				//room number
				room_number = findString(stream, 13, ',');
				//cell
				cell = findString(stream, 6, ',');
				//extension
				extension = findString(stream, 11, ',');
				//gridX
				gridX = Integer.valueOf(findString(stream, 7, ','));
				//gridY
				gridY = Integer.valueOf(findString(stream, 7, ']'));

				Employee e = new Employee(firstname, lastname, position, department, room_number, cell, extension, gridX, gridY);
				database.add(e);
								
				break;
			case 'R':
				//skip line
				if (!skipped) {
					stream.skip(23);
					skipped = true;
				}
				
				//tags
				Collection<Tags> tags = new HashSet<Tags>();
				String tag = findString(stream, 11, ',', ']');
				tags.add(Tags.getTag(tag));
	
				while ((char)stream.read() != ',') {
					tag = findString(stream, 0, ',', ']');
					if (Tags.getTag(tag) != null) {
						tags.add(Tags.getTag(tag));
					}
				}
								
				//room number
				room_number = findString(stream, 13, ',');
				//specific case for materials_library
				if (room_number.charAt(0) == '=') {
					room_number = room_number.substring(1);
				}
				
				//gridX
				gridX = Integer.valueOf(findString(stream, 7, ','));
				//gridY
				gridY = Integer.valueOf(findString(stream, 7, ']'));
				
				
				Room r = new Room(tags, room_number, gridX, gridY);
				
				database.add(r);
				
				break;
			default:
				break;
			}
		}

		stream.close();
	}

	/* Finds a string segment within an input stream based on a delimeter and skip length */
	private String findString(InputStream stream, int skipLength, char delimeter) throws IOException {
		stream.skip(skipLength);
		StringBuffer strbuf = new StringBuffer();
		char character = (char)stream.read();
		while (character != delimeter) {
			strbuf.append(character);
			character = (char)stream.read();
		}
		return strbuf.toString();
	}
	
	private String findString(InputStream stream, int skipLength, char delimeter, char delimeter2) throws IOException {
		stream.skip(skipLength);
		StringBuffer strbuf = new StringBuffer();
		char character = (char)stream.read();
		
		while (character != delimeter && character != delimeter2) {
			strbuf.append(character);
			character = (char)stream.read();
		}
		return strbuf.toString();
	}

	/* Database as String */
	@Override
	public String toString() {
		StringBuffer toReturn = new StringBuffer();
		toReturn.append("Database:\n");

		for (Object o : database) {
			toReturn.append(o+"\n");
		}

		return toReturn.toString();
	}

}
