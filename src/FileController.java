import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FileTableController implements Initializable {

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

	private ObservableList<FileObject> getMyFiles() {

	}

	private ObservableList<FileObject> getServerFiles() {

	}

	private void editDescription(FileObject item) {

	}

	private void transferFile(FileObject item) {

	}

	private void sessionAttributes() {

	}

	public void getFile() {

	}

	public void disconnectBtnAction() {
	}

	public void searchServer() {
	}

	public void closeBtnAction() {
	}
}