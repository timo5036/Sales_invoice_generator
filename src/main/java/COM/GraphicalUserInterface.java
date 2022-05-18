package COM;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
//import javax.swing.border.Border;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.border.Border;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.table.DefaultTableModel;

import java.awt.*;
//import java.awt.event.*;
import java.awt.event.*;
import java.io.*;


public class GraphicalUserInterface extends JFrame implements ActionListener, MouseListener
{



    // Left Panel


    private final DefaultTableModel invoicesTableModel;
    private final JTable invoicesTable;

    private int invoiceNumber;

    private int invoiceTotal;

    DefaultTableModel invoiceDetailsTableModel;
    private final JTable invoiceDetailsTable;
    private final String[] invoiceDetailsTableColumns = {"No.", "Item Name", "Item Price", "Count", "Item Total"};


    public GraphicalUserInterface(){
        super("Sales Invoice Generator");
        setLayout(null);
        setSize(870,700);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadFileItem = new JMenuItem("Load File", 'L');
        loadFileItem.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
        loadFileItem.addActionListener(this);
        loadFileItem.setActionCommand("loadFile");
        fileMenu.add(loadFileItem);
        JMenuItem saveFileItem = new JMenuItem("Save File", 'S');
        saveFileItem.setAccelerator(KeyStroke.getKeyStroke('S',KeyEvent.CTRL_DOWN_MASK));
        saveFileItem.addActionListener(this);
        saveFileItem.setActionCommand("saveFile");
        fileMenu.add(saveFileItem);
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Right Panel
        JLabel invoiceNumberLabel = new JLabel("Invoice Number"+ invoiceNumber);
        invoiceNumberLabel.setBounds(440,20, 400, 20);
        add(invoiceNumberLabel);

        JLabel invoiceDateLabel = new JLabel("Invoice Date");
        invoiceDateLabel.setBounds(440,60, 100, 20);
        add(invoiceDateLabel);

        JTextField invoiceDateTF = new JTextField(20);
        invoiceDateTF.setBounds(540,60, 300, 20);
        add(invoiceDateTF);

        JLabel customerNameLabel = new JLabel("Customer Name");
        customerNameLabel.setBounds(440,100, 100, 20);
        add(customerNameLabel);

        JTextField customerNameTF = new JTextField(20);
        customerNameTF.setBounds(540,100, 300, 20);
        add(customerNameTF);

        JLabel invoiceTotalLabel = new JLabel("Invoice Total: " + invoiceTotal);
        invoiceTotalLabel.setBounds(440,140, 400, 20);
        add(invoiceTotalLabel);

        // Right Panel
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(440,180,400,400);

        Border blacklineRight = BorderFactory.createTitledBorder("Invoice Items"); // Create border for the panel with Title
        rightPanel.setBorder(blacklineRight);
        getContentPane().add(rightPanel, BorderLayout.CENTER);
        add(rightPanel);
        //Left Panel
        String[] invoicesTableColumns = {"No.", "Date", "Customer", "Total"};
        String[][] invoicesTableData = csvToArray("src/InvoiceHeader.csv", false);
        invoicesTableModel = new DefaultTableModel(invoicesTableData, invoicesTableColumns);
        invoicesTable = new JTable(invoicesTableModel);
        invoicesTable.addMouseListener(this);
        JScrollPane invoicesTableSP = new JScrollPane(invoicesTable);
        invoicesTableSP.setBounds(20,30,400,540);
        add(invoicesTableSP);

        JButton newInvoiceButton = new JButton("Create New Invoice");
        newInvoiceButton.setBounds(70, 590, 150,20);
        add(newInvoiceButton);

        JButton deleteInvoiceButton = new JButton("Delete Invoice");
        deleteInvoiceButton.setBounds(230, 590, 150,20);
        deleteInvoiceButton.addActionListener(this);
        deleteInvoiceButton.setActionCommand("deleteInvoice");
        add(deleteInvoiceButton);

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++


        String[][] invoiceDetailsData = csvToArray("src/InvoiceLine.csv", true);
        invoiceDetailsTableModel = new DefaultTableModel(invoiceDetailsData, invoiceDetailsTableColumns);
        invoiceDetailsTable = new JTable(invoiceDetailsTableModel);
        JScrollPane invoiceDetailsTableSP = new JScrollPane(invoiceDetailsTable);
        invoiceDetailsTableSP.setBounds(10,20,380,370);
        rightPanel.add(invoiceDetailsTableSP, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(500,590,100,20);
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(670,590,100,20);
        add(cancelButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var i = invoicesTable.getSelectedRow();
        System.out.println(i);
        switch (e.getActionCommand()) {
            case "saveFile" -> Save();
            case "loadFile" -> Load();
            case "deleteInvoice" -> Delete();
            default -> {
            }
        }

    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // No implementation
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // No implementation
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // No implementation
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No implementation
    }
    @Override
    public void mouseExited(MouseEvent e) {
        // No implementation

    }
    private void Load(){
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Comma Separated Values (.csv)", "csv");
        fc.addChoosableFileFilter(restrict);
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){
            String path = fc.getSelectedFile().getPath();
            String[][] data = csvToArray(path, true);
            invoiceDetailsTable.setModel(new DefaultTableModel(data,invoiceDetailsTableColumns));
        }

    }
    private void Save() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save your invoice");
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Comma Separated Values (.csv)", "csv");
        fc.addChoosableFileFilter(restrict);
        int result = fc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File csvFile = fc.getSelectedFile();
            System.out.println(csvFile.getName());
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(csvFile);
                bw = new BufferedWriter(fw);
                for (int i = 0; i < invoiceDetailsTable.getRowCount(); i++) {
                    for (int j = 0; j < invoiceDetailsTable.getColumnCount(); j++) {
                        bw.write(invoiceDetailsTable.getValueAt(i, j).toString() + ","); // Save the value of each cell and follow it with ","
                    }
                    bw.newLine(); // Create a new line (new row)
                }
                JOptionPane.showMessageDialog(this, "Your file has been saved successfully", "INFORMATION", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "ERROR", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    assert bw != null;
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    private void Delete() {
        int row = invoicesTable.getSelectedRow();
        invoicesTableModel.removeRow(row);
    }
    private String[][] csvToArray(String path, boolean details){
        String line = "";
        BufferedReader br = null;
        String[][] data = new String[5][100];
        try {
            br = new BufferedReader(new FileReader(path));
            int i =0;
            while ((line = br.readLine()) != null)
            {
                String[] arr = line.split(",");
                System.arraycopy(arr, 0, data[i], 0, arr.length);
                i++;
            }
            if(details){
                for(int j = 0; j < 5; j++) {
                    data[j][4] = Integer.toString(Integer.parseInt(data[j][3]) * Integer.parseInt(data[j][2]));
                    invoiceTotal += Integer.parseInt(data[j][4]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}