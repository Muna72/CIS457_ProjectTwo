import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.Color;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.border.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
    /**
     *
     * @author Muna Gigowski
     * @version 1.0 (March 2019)
     */
    public class FtpGUI extends JFrame implements ActionListener {


        //Declaring instance variables
        private int DELAY = 20;
        private boolean isRunning;
        private boolean firstTimeStartPressed;
        DecimalFormat df = new DecimalFormat("#.00");
        ArrayList<Object>availableFileInfo;

        private JPanel input;
        ftpClientAndServer connectionInfo;
        ftpClientAndServer cmdLine;
        private JPanel commandArea;
        private JPanel searchArea;

        //define buttons
        JButton connect;
        JButton search;
        JButton go;

        //define text fields
        JTextField serverHostName;
        JTextField portNum;
        JTextField userName;
        JTextField hostName;
        JTextField keyword;
        JTextField command;

        //define JComboBoxes
        JComboBox<String> speedSelection;

        //define JTable and tableModel
        JTable fileInfo;
        DefaultTableModel tableModel;

        //define JLabels
        private JLabel serverHostnameLabel;
        private JLabel portNumLabel;
        private JLabel userNameLabel;
        private JLabel hostNameLabel;
        private JLabel speedLabel;
        private JLabel keywordLabel;
        private JLabel commandLabel;
        private JLabel inputLabel;
        private JLabel fileSearch;
        private JLabel ftpConsole;


        //define menu items
        private JMenuBar menu;
        JMenu file;
        JMenuItem reset;
        JMenuItem quit;

        /**
         * Main method
         * @param args
         */
        public static void main(String[] args) {
            try {
                FtpGUI gui = new FtpGUI();
                gui.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                gui.setTitle("FTP Client");
                gui.setPreferredSize(new Dimension(1400, 1000));
                gui.pack();
                gui.setVisible(true);
                gui.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {

                        int dialogResult = JOptionPane.showConfirmDialog(gui,
                                "Closing window while client is running" +
                                        " will cause you to lose all data. Proceed in closing?", "Close Window?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Class constructor initializes instance variables
         */
        public FtpGUI() {

            firstTimeStartPressed = true;
            isRunning = false;
            availableFileInfo = new ArrayList<Object>();
            connectionInfo = new ftpClientAndServer();

            setLayout(new GridBagLayout());
            GridBagConstraints position = new GridBagConstraints();
            Font font = new Font("SansSerif Bold", Font.BOLD, 14);
            TitledBorder border;

            //Adding all panels to JFrame
            input = new JPanel(new GridBagLayout());
            input.setBackground(Color.RED);
            input.setPreferredSize(new Dimension(1000, 200));
            //input.setBorder(new EmptyBorder(15, 0, 30, 20));
            border = new TitledBorder("Connection Input Information");
            border.setTitleFont(new Font("Arial", Font.BOLD, 16));
            border.setTitleJustification(TitledBorder.CENTER);
            border.setTitlePosition(TitledBorder.TOP);
            input.setBorder(border);
            position = makeConstraints(10, 0, 1, 3, GridBagConstraints.LINE_END);
            position.insets =  new Insets(0, 0, 0, 150);
            add(input,position);

            searchArea = new JPanel(new GridBagLayout());
            searchArea.setBackground(Color.BLUE);
            searchArea.setPreferredSize(new Dimension(1000, 350));
            border = new TitledBorder("File Search");
            border.setTitleFont(new Font("Arial", Font.BOLD, 16));
            border.setTitleJustification(TitledBorder.CENTER);
            border.setTitlePosition(TitledBorder.TOP);
            searchArea.setBorder(border);
            position = makeConstraints(10, 2, 1, 3, GridBagConstraints.LINE_END);
            position.insets =  new Insets(0, 0, 0, 150);
            add(searchArea,position);

            commandArea = new JPanel(new GridBagLayout());
            commandArea.setBackground(Color.YELLOW);
            //commandArea.setPreferredSize(new Dimension(1000, 300));
            border = new TitledBorder("FTP Console");
            border.setTitleFont(new Font("Arial", Font.BOLD, 16));
            border.setTitleJustification(TitledBorder.CENTER);
            border.setTitlePosition(TitledBorder.TOP);
            commandArea.setBorder(border);
            position = makeConstraints(10, 4, 1, 3, GridBagConstraints.LINE_END);
            position.insets =  new Insets(0, 0, 0, 150);
            add(commandArea,position);

            cmdLine = new ftpClientAndServer();
            cmdLine.setPreferredSize(new Dimension(600,200));
            position = makeConstraints(0, 3, 1, 1, GridBagConstraints.FIRST_LINE_START);
            position.insets =  new Insets(10, 10, 10, 20);
            commandArea.add(cmdLine, position);

            font = new Font("SansSerif Bold", Font.BOLD, 13);

            String[] speedOptions = new String[] {"Ethernet", "Modem",
                    "T1", "T3"};

            speedSelection = new JComboBox<>(speedOptions);
            speedSelection.setMinimumSize(speedSelection.getPreferredSize());
            position = makeConstraints(10, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(15, -25, 0, 0);
            input.add(speedSelection, position);

            String[] columnNames = {"Speed", "Hostname", "Filename", "User"};

            tableModel = new DefaultTableModel(columnNames, 0);
            fileInfo = new JTable(tableModel);
            fileInfo.setRowHeight(40);
            position = makeConstraints(1, 3, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(10, 0, 0, 20);
            searchArea.add(fileInfo, position);

            //Adding stats to searchArea JPanel
            serverHostnameLabel = new JLabel("Server Hostname:");
            position = makeConstraints(1, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(30, 10, 0, 0);
            serverHostnameLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
            input.add(serverHostnameLabel, position);

            font = new Font("SansSerif Bold", Font.BOLD, 13);

            portNumLabel = new JLabel("Port:");
            portNumLabel.setFont(font);
            position = makeConstraints(3, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(7, 20, 0, 0);
            input.add(portNumLabel, position);

            userNameLabel = new JLabel("Username:");
            userNameLabel.setFont(font);
            position = makeConstraints(1, 2, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(10, 15, 20, 20);
            input.add(userNameLabel, position);

            hostNameLabel = new JLabel("Hostname:");
            hostNameLabel.setFont(font);
            position = makeConstraints(3, 2, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(15, 20, 0, 0);
            input.add(hostNameLabel, position);

            speedLabel = new JLabel("Speed:");
            speedLabel.setFont(font);
            position = makeConstraints(8, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(15, -80, 0, 20);
            input.add(speedLabel, position);

            keywordLabel = new JLabel("Keyword:");
            keywordLabel.setFont(font);
            position = makeConstraints(0, 0, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(-90, 10, 0, 20);
            searchArea.add(keywordLabel, position);

            commandLabel = new JLabel("Enter Command: ");
            commandLabel.setFont(font);
            commandLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            position = makeConstraints(0, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(25, 10, 0, 20);
            commandArea.add(commandLabel, position);


            //Place the textfields
            serverHostName = new JTextField("", 20);
            position = makeConstraints(2, 1, 1, 1, GridBagConstraints.LINE_START);
            //serverHostName.setMinimumSize(serverHostName.getPreferredSize());
            position.insets =  new Insets(15, 15, 0, 20);
            input.add(serverHostName, position);

            portNum = new JTextField("", 10);
            position = makeConstraints(3, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(12, 60, 0, 0);
            input.add(portNum, position);

            userName = new JTextField("", 15);
            position = makeConstraints(2, 2, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(0, -20, 0, 80);
            input.add(userName, position);

            hostName = new JTextField("", 20);
            position = makeConstraints(4, 2, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(15, -80, 0, 0);
            input.add(hostName, position);

            keyword = new JTextField("", 20);
            keyword.setForeground(Color.GREEN);
            position = makeConstraints(1, 0, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(-90, -10, 0, 20);
            searchArea.add(keyword, position);

            command = new JTextField("", 20);
            command.setForeground(Color.GREEN);
            position = makeConstraints(0, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(10, 120, 0, 20);
            commandArea.add(command, position);

            //place each button
            connect = new JButton( "Connect" );
            connect.setForeground(Color.GREEN);
            position = makeConstraints(10, 1, 1, 1, GridBagConstraints.LINE_START);
            position.insets =  new Insets(40, 100, 0, 20);
            input.add(connect, position);

            search = new JButton( "Search" );
            search.setForeground(Color.RED);
            position = makeConstraints(4,0,1,1,GridBagConstraints.LINE_START);
            position.insets =  new Insets(-90,0,0,20);
            searchArea.add(search, position);

            go = new JButton( "Go" );
            go.setForeground(Color.RED);
            position = makeConstraints(1,1,1,1,GridBagConstraints.LINE_START);
            position.insets =  new Insets(15,-150,0,20);
            commandArea.add(go, position);

            //create and add menu items
            menu = new JMenuBar();
            file = new JMenu("File");
            quit = new JMenuItem("Quit");
            reset = new JMenuItem("Clear");
            menu.add(file);
            file.add(quit);
            file.add(reset);
            setJMenuBar(menu);

            //add all action listeners
            speedSelection.addActionListener(this);
            connect.addActionListener(this);
            search.addActionListener(this);
            go.addActionListener(this);
            serverHostName.addActionListener(this);
            portNum.addActionListener(this);
            userName.addActionListener(this);
            hostName.addActionListener(this);
            keyword.addActionListener(this);
            command.addActionListener(this);
            file.addActionListener(this);
            quit.addActionListener(this);
            reset.addActionListener(this);

            //disable buttons by default
            search.setEnabled(false);
            go.setEnabled(false);
        }

        /**
         * Action performed method
         * @param e
         */
        public void actionPerformed(ActionEvent e) {


            //exit application if QUIT menu item
            if (e.getSource() == quit) {
                System.exit(1);
            }

            //set running variable to true if START button
            if (e.getSource() == go) {
                try {
                    connectionInfo.retrieve(command.getText());
                } catch (Exception ex) {
                    System.out.println("Error retrieving file");
                }
            }


            //set running variable to false if STOP button
            if (e.getSource() == connect) {
                if(!serverHostName.getText().equals("") && !portNum.getText().equals("") &&
                !userName.getText().equals("") && !hostName.getText().equals("")) {
                    try {
                        System.out.println(connectionInfo);
                        connectionInfo.connectCentralServerStartLocalUser(userName.getText(), serverHostName.getText(),
                                portNum.getText(), speedSelection.getSelectedItem().toString(), hostName.getText());
                        search.setEnabled(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Error setting up connection");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "All connection setup fields must have values");
                }
            }

            if (e.getSource() == search) {
                connectionInfo.search(keyword.getText());
                ArrayList<AvailableFile> files = connectionInfo.getAvailableFiles();
               Object[] headers = {"USERNAME", "HOSTNAME", "FILE NAME", "SPEED"};
               System.out.println(headers);
                tableModel.addRow(headers);
                for(int i = 0; i < files.size(); ++i) {
                    AvailableFile currentFile = files.get(i);
                    Object[] objs = {currentFile.getHostUserName(), currentFile.getHostName(), currentFile.getFileName(), currentFile.getSpeed()};
                    tableModel.addRow(objs);
                }
                setColunmWidth(fileInfo);
                fileInfo.setBorder(BorderFactory.createCompoundBorder());
                go.setEnabled(true);
            }

            //update GUI
            //object.repaint();
        }


        //Method to set the width of all table columns
        public void setColunmWidth(JTable table) {

            TableColumnModel tcm = table.getColumnModel();

            for (int i = 0; i < (tcm.getColumnCount()); i++) {
                tcm.getColumn(i).setPreferredWidth(90);
            }
        }

        /**
         * Method to set contraints for gridbag layout
         * @param x
         * @param y
         * @param h
         * @param w
         * @param align
         * @return
         */
        private GridBagConstraints makeConstraints(int x, int y, int h, int w, int align) {
            GridBagConstraints rtn = new GridBagConstraints();
            rtn.gridx = x;
            rtn.gridy = y;
            rtn.gridheight = h;
            rtn.gridwidth = w;

            rtn.anchor = align;
            return rtn;
        }
    }
