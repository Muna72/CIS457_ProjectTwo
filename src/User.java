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

/*******************************************************************************************
 * Creates a connection with the CentralizedServer and starts a local server
 ******************************************************************************************/
	public void makeConnection(String userName, String serverHostName, String serverPort, String connectionSpeed,
		String localHost, String localPort) throws IOException {

        Thread localServer = new Thread(new Runnable() {
			public void run() {
            }
        });
	}
/*******************************************************************************************
 * Searches the CentralServer for available files the client can download 
 ******************************************************************************************/
	public boolean search(String keyword) {
    }

	public ArrayList<AvailableFile> getAvailableFiles() {
		return availableFiles;
	}
/*******************************************************************************************
 * If the file requested is an available file the client will form a socket with
 * the proper server and then download the file
 ******************************************************************************************/
	public boolean retrieve(String file) throws UnknownHostException, IOException {
	}

	public void quit() {
	}
}
/*******************************************************************************************
 * Stores information for available files that matched in the search window
 ******************************************************************************************/
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