public class FileObject {
	private StringProperty filename, description, hostname, speed;

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

	public FileObject(String filename, String hostname, String speed, String description) {
	}

	public String getHostname() {
		return hostname.get();
	}

	public String getSpeed() {
		return speed.get();
	}

}