/*******************************************************************************************
 * FileObject class can easily update data of file tables
 ******************************************************************************************/
public class FileObject {
	private StringProperty filename, description, hostname, speed;
/*******************************************************************************************
 * Used for users files
 ******************************************************************************************/
	public FileObject(String filename, String description) {
	}

	public String getFilename() {
		return filename.get();
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
/*******************************************************************************************
 * Used in server files
 ******************************************************************************************/
	public FileObject(String filename, String hostname, String speed, String description) {
	}

	public String getHostname() {
		return hostname.get();
	}

	public String getSpeed() {
		return speed.get();
	}

}