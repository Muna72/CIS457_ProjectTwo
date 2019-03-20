import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FileTableController implements Initializable {
/*******************************************************************************************
 * Received data from the ConnectionController class
 ******************************************************************************************/
	public void initData(User user, String serverHN, String serverPort, String userName, String userHN, String userPort,
			String userSpeed) {
		this.serverHN = serverHN;
		this.serverPort = serverPort;
		this.userName = userName;
		this.userHN = userHN;
		this.userPort = userPort;
		this.userSpeed = userSpeed;
	}

	public void initialize(URL location, ResourceBundle resources) {
	}
/*******************************************************************************************
 * Returns a list of all FileObjects in users files.
 ******************************************************************************************/
	private ObservableList<FileObject> getMyFiles() {

	}
/*******************************************************************************************
 * Returns a list of all FileObjects in servers files
 ******************************************************************************************/
	private ObservableList<FileObject> getServerFiles() {

	}
/*******************************************************************************************
 * Allowing editing of FileObjects description
 ******************************************************************************************/
	private void editDescription(FileObject item) {

	}
/*******************************************************************************************
 * Facilitates the transfer of file between two users
 ******************************************************************************************/
	private void transferFile(FileObject item) {

	}

	private void sessionAttributes() {

	}
/*******************************************************************************************
 * When user clicks GetFile button the text is grabbed from search bar.
 * Looks for filenames that match.
 ******************************************************************************************/
	public void getFile() {

	}

	public void disconnectBtnAction() {
	}

	public void searchServer() {
	}

	public void closeBtnAction() {
	}
}