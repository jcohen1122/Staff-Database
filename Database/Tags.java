package Database;
/**
 * Possible room tags
 * @author Joshua Cohen
 *
 */
public enum Tags {
	HUDDLE,
	CONFERENCE,
	COFFEE,
	BATHROOM,
	JANITOR,
	PRINTER,
	COPY,
	MATERIALS_LIBRARY,
	MAIL,
	PHONE,
	CAFE,
	ELECTRICAL,
	STORAGE,
	STAIRS,
	ELEVATOR,
	LACTATION,
	MISC;
	
	public static Tags getTag(String tag) {
		for (Tags t : Tags.values()) {
			if (tag.equals(t.toString())) {
				return t;
			}
		}
		return null;
	}
}
