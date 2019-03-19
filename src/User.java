public class User {
	private Socket s;
	private DataOutputStream dos;
	private DataInputStream is;
	private BufferedReader dis;
	private ArrayList<AvailableFile> availableFiles;
	private String userName;
	private String localHost;
	private String serverPort;
	private String connectionSpeed;
	private boolean loggedOn;

	public void makeConnection(String userName, String serverHostName, String serverPort, String connectionSpeed,
		String localHost, String localPort) throws IOException {

        Thread localServer = new Thread(new Runnable() {
			public void run() {
            }
        });
	}

	public boolean search(String keyword) {
    }
    
	public ArrayList<AvailableFile> getAvailableFiles() {
		return availableFiles;
	}

	public boolean retrieve(String file) throws UnknownHostException, IOException {
	}

	public void quit() {
	}
}

class AvailableFile {

	public String hostUserName;
	public String hostName;
	public String fileName;
	public String speed;
	public int port;

	public AvailableFile(String hostUserName, String hn, int p, String fn, String speed) {
		this.hostUserName = hostUserName;
		this.hostName = hn;
		this.port = p;
		this.fileName = fn;
		this.speed = speed;
	}

}