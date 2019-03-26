import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.lang.Math;
import java.util.List;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/****
 * Manages the connection to the central server.
 * Creates the local FTP server.
 * Manages the local FTP client.
 * Used to instantiate a user.
****/
public class ftpClientAndServer extends JPanel {

    private Socket s;
    private DataOutputStream out;
    private DataInputStream in;
    //not used atm might remove.
    private BufferedReader buf;
    private ArrayList<AvailableFile> availableFiles;
    private String userName;
    private String serverHost;
    private String serverPort;
    private String speed;
    //currently start centralserver too. Not sure if a 3rd host/port is needed.
    private String localHost;
    //Needs to be generated not input.
    private String localPort;
    private Boolean loggedIn;
    
    
    //ehhh just gonna hardcode a local port
    // public int getRandomLocalPort(double min, double max) {
    // min = Math.ceil(min);
    // max = Math.floor(max);
    // return Math.floor((Math.random() * (max - min)) + min); //The maximum is exclusive and the minimum is inclusive
    // }


//need to figure out about multiple central servers adding to the filelist or just those on localhost running central server.


/****
 * Connects to the central server using data from JPanels.
 * Starts the local FTP server using data from JPanels.
****/
    public void connectCentralServerStartLocalUser(String userName, String serverHost, String serverPort, String speed,
        String localHost) throws IOException {

        //Creates a random local port between 50000 and 50010.
        //Might just hardcode a single localport for client/server to transfer to/from.;
        localPort = Integer.toString(50000);
        
        //IP address obtained from JPanel
        InetAddress ip = InetAddress.getByName(serverHost);
        //Connection to the central Server.
        //I has questions about using serverHost and serverPort.
        //CentralServer open on port 3158. 
        s = new Socket(ip, Integer.parseInt(serverPort));
        
        //Information of the client you would like to connect to.
        this.localHost = localHost;
        this.userName = userName;
        this.speed = speed;

        //Input and output streams to send and receive information.
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());

        //The connection stream that stores information about the client to be passed.
        //This will be parsed by each space with String Tokenizer.
        out.writeUTF(userName + "" + localHost + "" + speed + "" + localPort);

        //Creates a filelist in the form of a text document.
        //Will need to develop a method of attaching filename and description in text file.
        //Professor recommends xml, found this link
        //https://www.tutorialspoint.com/java_xml/java_dom4j_parse_document.htm
        File fileList = new File("./filelist.xml");
        if(fileList.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document document = reader.read(fileList);
                System.out.println("Root element :" + document.getRootElement().getName());
                Element classElement = document.getRootElement();

                //Creates a list of all the files in the filelist.xml starting at the tag "file".
                List<Node> nodes = document.selectNodes("/filelist/file");
                //This sends the dataOutputStream the "200" success status code and the size of the nodes.
                out.writeUTF("200" + "" + nodes.size());

                for(Node node : nodes) {
                    String fileName = node.selectSingleNode("name").getText();
                    String fileDescription = node.selectSingleNode("description").getText();
                    //This will be parsed with a String tokenizer, delimiting by the "$" symbol.
                    out.writeUTF(fileName + "$" + fileDescription);
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            //If not file is available output 404 and a console message.
            out.writeUTF("404");
            System.out.println(".txt Filelist is required.");
        }
        //indicates the user is logged in.
        loggedIn = true;

        //Manages any other FTP clients seeking a file from our local FTPServer.
        //Creates a new thread to allow this to happen simultaneously.
        Thread localServer = new Thread(new Runnable() {
            public void run() {
                while(loggedIn) {   
                    try {
                        FileInputStream fis = null;
                        BufferedInputStream bis = null;
                        OutputStream os = null;
                        //Found a good example for transfering any file format.
                        //https://www.rgagnon.com/javadetails/java-0542.html
                        //Creates a SeverSocket that allows connection from another host's FtpClient.
                        ServerSocket localFtpServer = new ServerSocket(Integer.parseInt(localPort));
                        //Socket from another host's client.
                        Socket client = localFtpServer.accept();
                        System.out.println("Accepted connection : " + client);
                        //dos and dis are used to read info about the file needed to be downloaded and then send the actual file.
                        //Creates a new DataOutputStream using the client socket from another host's FtpClient.
                        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                        //Creates a new DataInputStream using the client socket from another host's FtpClient.
                        DataInputStream dis = new DataInputStream(client.getInputStream());

                        //dis reads in the command including filename to be downloaded to another host.
                        String command = dis.readUTF();
                        StringTokenizer tokens = new StringTokenizer(command);
                        //Parses the first token which is the command.
                        String targetFile = tokens.nextToken();
                        //targetFile now only contains the filename.
                        targetFile = tokens.nextToken();
                        
                        //Stores the targetFile into a file object
                        //file to be checked if exists in src folder.
                        File file = new File("./" + targetFile);
                        if(file.exists()) {
                            //Creates a double containing the size of the file
                            double fileSize = file.length();
                            //output a "200" status code followed by a $ for string tokenizer followed by filesize
                            //indicating file exists and file size to be passed.
                            //removed fileSize.toString()); will remove if toString uneeded
                            out.writeUTF("200$" + fileSize);
                            //read in regardless of file formats.
                            //Send file
                            //Creates an array of bytes the size of the file to be transferred.
                            byte [] myByteArray = new byte [(int)file.length()];
                            //Input streams to read in the file regardless of file format.
                            fis = new FileInputStream(file);
                            bis = new BufferedInputStream(fis);
                            //Reading the file into a bufferedInputStream.
                            bis.read(myByteArray, 0, myByteArray.length);
                            os = client.getOutputStream();
                            System.out.println("Sending " + file + "(" + myByteArray.length + " bytes)");
                            //Since the file is in the inputstream we can now send write it to the output stream for client.
                            os.write(myByteArray, 0, myByteArray.length);
                            os.flush();
                            System.out.println("Done");
                            //Close down streams when done.
                            fis.close();
                            bis.close();
                            os.close();
                        } else {
                            //If the client could not find the file return "404".
                            dos.writeUTF("404");
                        }
                        //Close down sockets when done.
                        client.close();
                        localFtpServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }); 
        //Starts up the localServer. Instaniates the runnable thread.
        localServer.start();
    }
    //Handles searching for the localFtpClient searched the Central Server for available files.
    //When keyword is typed into GUI search it run with keyword first.
    //Then getAvailableFiles() can be run and parsed to display files in swing panel.
    public boolean search(String keyword) {
        //tokens takes in a single available file from an array of available files/
        //It then parses and splits each element of the available file object based on 
        StringTokenizer tokens;

        try {

            //send the keyword to the output stream of the central server.
            out.writeUTF(keyword);
            String str = "";
            //Reads in file information from the central server
            //if nothing matches the search str will equal EOF
            str = in.readUTF();

            availableFiles = new ArrayList<AvailableFile>();

            //If more files exist read them.
            while(!str.equals("EOF")) {
                tokens = new StringTokenizer(str);
                String hostSpeed = tokens.nextToken();
                String hostName = tokens.nextToken();
                int hostPort = Integer.parseInt(tokens.nextToken());
                String hostFileName = tokens.nextToken();
                String hostUserName = tokens.nextToken();
                //Creates an availableFile object to be added to the array of AvailableObjects.
                AvailableFile file = new AvailableFile(hostUserName, hostName, hostPort, hostFileName, hostSpeed); 
                availableFiles.add(file);
                //setup next file to be read.
                str = in.readUTF();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (availableFiles.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    //returns list of available files. This is the command we will use to show available files in GUI.
    public ArrayList<AvailableFile> getAvailableFiles() {
        return availableFiles;
    }
    //Checks to see if the file request is an available file on the server.
    //If so it will form a socket with the correct server and download the file.
    public boolean retrieve(String fileCommand) throws UnknownHostException, IOException {
        StringTokenizer tokens;
        StringTokenizer commandResponse = new StringTokenizer(fileCommand);
        String commandType = commandResponse.nextToken();
        String file = commandResponse.nextToken();
        boolean downloaded = false;
        if(commandType.equals("retr")) {
            for(int i = 0; i < availableFiles.size(); i++) {
                //looks through the entire availableFile list searching for a matching file to the one we want.
                //Requires correct fileName and username.
                if(availableFiles.get(i).fileName.equals(file) && !availableFiles.get(i).hostUserName.equals(userName)) {
                    AvailableFile targetFile = availableFiles.get(i);
                    InetAddress ip = InetAddress.getByName(availableFiles.get(i).hostName);
                    //Socket connection to server containing file.
                    Socket retr = new Socket(ip, targetFile.port);

                    DataOutputStream dos = new DataOutputStream(retr.getOutputStream());
                    DataInputStream dis = new DataInputStream(retr.getInputStream());

                    String command = "retr: " + file;
                    dos.writeUTF(command);

                    String fullResponseInput = dis.readUTF();
                    tokens = new StringTokenizer(fullResponseInput);
                    //Parses in status code indicating if server has file.
                    String response = tokens.nextToken();
                    //Parses in fileSize from String token.
                    int fileSize = Integer.parseInt(tokens.nextToken());
                    //The internet recommended keeping the fileSize above the actual fileSize as a precaution.
                    //1 kb bigger then actual fileSize.
                    fileSize = fileSize + 1000;

                    if(!response.equals("404")) {
                        File newFile = new File("./" + file);
                        int bytesRead;
                        int current = 0;
                        //might need to change initialization on these two.
                        // FileOutputStream fos;
                        // BufferedOutputStream bos;
                        try {
                            byte [] myByteArray = new byte [fileSize];
                            InputStream is = retr.getInputStream();
                            //this part left me with questions because the online example used poor conventions.
                            //https://www.rgagnon.com/javadetails/java-0542.html
                            FileOutputStream fos = new FileOutputStream(newFile);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bytesRead = is.read(myByteArray,0,myByteArray.length);
                            current = bytesRead;

                            do { 
                                bytesRead =
                                is.read(myByteArray, current, (myByteArray.length-current));
                                if(bytesRead >= 0) current += bytesRead;
                            } while(current < fileSize);
                            fos.close();
                            bos.close();
                        //Might use finally to close streams and sockets.
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // fos.close();
                        // bos.close();
                    } else {
                        System.out.println("File Not Found!");
                    }
                    retr.close();
                }
            }
        } 
        if(commandResponse.equals("quit") || commandResponse.equals("QUIT")) {
            quit();
            return false;
        }
        return downloaded;
    }
    //Sends a message to the central serval closing the socket connection
    //I believe swing interface would also close upon hitting a disconnect button but picture on profs doc looks like a central server quit via command.
    //could also just run if input from command textbox equals quit.
    public void quit() {
		try {
			out.writeUTF("-1");
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Ends the thread.
        loggedIn = false;
        
	}

    /**
     * Method to paint the file information
     * @param g for graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawString(String str, int x, int y)

    }
}
//Stores information for available files that matches in the search.
class AvailableFile {
    public String hostUserName;
    public String hostName;
    public String fileName;
    public String speed;
    public int port;

    public AvailableFile(String hostUserName, String hostName, int port, String fileName, String speed) {
        this.hostUserName = hostUserName;
        this.hostName = hostName;
        this.port = port;
        this.fileName = fileName;
        this.speed = speed;
    }
}
