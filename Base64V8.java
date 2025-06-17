import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Desktop;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

public class Base64V8 extends JPanel {
    private static JTextArea Base64Input;
    public static JLabel res;
    static int currentPDFNumber = 0;
    public static JFrame frame = new JFrame("Base64 to PDF Converter");
    //private ImageIcon iconImg = null;

    //private static JLabel runText;

    private Base64V8() {
        setLayout(new BorderLayout());
        
        //Image icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/64ToolIcon.png"));
        frame.setIconImage(icon.getImage());

        //Top of the application. Contains the "Convert", "Parse HL7 files", info, and resize buttons
        JPanel centerPanel = new JPanel();

        JButton help = new JButton("?");
        help.addActionListener(new userHelp());
        help.setBackground(new java.awt.Color(255,255,255));
        centerPanel.add(help);

        JButton resizeWindow = new JButton("⛶");
        resizeWindow.addActionListener(new resizeWindowPane());
        resizeWindow.setBackground(new java.awt.Color(255,255,255));
        centerPanel.add(resizeWindow);
        
        JButton btn = new JButton("Convert");
        btn.addActionListener(new convertTextArea());
        btn.setBackground(new java.awt.Color(81,209,161));
        centerPanel.add(btn);

        JButton cycleHL7Files = new JButton("Parse HL7 Files");
        cycleHL7Files.addActionListener(new cycleHL7FilesFolder());
        cycleHL7Files.setBackground(new java.awt.Color(81,209,161));
        centerPanel.add(cycleHL7Files);

        this.res = new JLabel();
        res.setVisible(false);
        centerPanel.add(res);

        centerPanel.setBackground(new java.awt.Color(214, 216, 233));
        add(centerPanel, BorderLayout.NORTH);

        //middle of application. Contains delete PDF, Clear Text, and HL7
        JPanel extraButtons = new JPanel();
        JButton deletePDF = new JButton("Delete Last PDF");
        deletePDF.setForeground(new java.awt.Color(240,40,40));
        //deletePDF.setBackground(new java.awt.Color(250,250,250));
        deletePDF.setBackground(new java.awt.Color(235,239,240));
        deletePDF.addActionListener(new deletePDFCall());

        JButton clearTextArea = new JButton("Clear Text");
        clearTextArea.setForeground(new java.awt.Color(240,40,40));
        clearTextArea.setBackground(new java.awt.Color(235,239,240));
        clearTextArea.addActionListener(new clearTextCall());

        JButton openFileDropPath = new JButton("HL7 Input");
        openFileDropPath.setBackground(new java.awt.Color(247, 251, 164));
        openFileDropPath.addActionListener(new openHL7FilePath());

        extraButtons.setVisible(true);
        extraButtons.add(deletePDF);
        extraButtons.add(clearTextArea);
        extraButtons.add(openFileDropPath);

        extraButtons.setBackground(new java.awt.Color(174, 174, 174));
        add(extraButtons, BorderLayout.CENTER);

        //bottom panel. Contains the space to enter Base64, scrollbar
        JLabel textInfo = new JLabel();
        textInfo.setVisible(true);
        textInfo.setText("Paste Base64 or HL7 Message Below:");

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());

        Base64Input = new JTextArea(5, 50);
        Base64Input.setLineWrap(true);

        JScrollPane scrollingInput = new JScrollPane(Base64Input);
        scrollingInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollingInput.setPreferredSize(new Dimension(500,575));

        northPanel.setVisible(true);
        northPanel.add(textInfo);
        northPanel.add(scrollingInput);
        northPanel.setBackground(new java.awt.Color(214, 216, 233));
        add(northPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        frame.add(new Base64V8());
        frame.setVisible(true);
        frame.setSize(600, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    

    private static class convertTextArea implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            String messageCheck = res.getText();
            if (messageCheck.toString() != "")
            {
                res.setText("");
            }
            String content = Base64Input.getText();

            /**
             * Added below section in V7. Allows for a full HL7 message to be pasted into the text input area
             * and then parsed for the HL7 segments
             * Had to update logic to name PDFs incrementally 
             */
            if (content.toString().matches("[A-Za-z0-9]+"))
            {
                res.setText("");
            }
            else if(content.toString().isEmpty())
            {
                res.setVisible(true);
                res.setText("NO TEXT ENTERED");
            }
            else
            {
                res.setText("");
            }
            
            if(!content.toString().contains("JVBER") && !content.toString().isEmpty())
            {
            	//Message will pop up if there is no JVBER in the Base64/input area
            	JOptionPane.showMessageDialog(res, "Invalid Base64 segment!\n\nAll Base64 segments contain 'JVBER'.\nPlease ensure the HL7 file/Base64 string\nis complete");
            }
            else
            {
            	List<String> base64InEntry = new ArrayList();
                try (Scanner readingSpace = new Scanner(content))
                {
                    while(readingSpace.hasNext())
                    {
                        String currentLine = readingSpace.nextLine();
                        if(currentLine.contains("JVBER"))
                        {
                            //convertProvidedBase64(currentLine);
                            Conversion convert = new Conversion();
                            convert.convertProvidedBase64(currentLine);
                            base64InEntry.add(currentLine); 
                        }
                    }
                }
                getConversionResults();
            }                       
        }
    }

    private static class deletePDFCall implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {

            File dir = new File("C:\\Base64Conversion");
            File[] directoryListing = dir.listFiles();
            boolean deleted = false;
            for (int i=0; i<directoryListing.length; i++) 
            {
                File file = new File("C:\\Base64Conversion\\CONVERTED"+i+".pdf");
                res.setVisible(true);

                if (file.delete()) {
                
                    res.setText("FILE DELETED");
                    deleted = true;
                }
                else 
                {
                    res.setText("NO FILE FOUND");
                }
            }
            if (deleted == true)
            {
                res.setText("FILE DELETED");
                deleted = false;
            }
            else
            {
                res.setText("NO FILE FOUND");
            }
            deleted = false;
            //currentPDFNumber = 0;
            Conversion setPDFTo0 = new Conversion();
            setPDFTo0.setPDFNumTo0(0);
        }
    }

    private static class clearTextCall implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            res.setText("");
            Base64Input.setText(null);
        }
    }

    private static class resizeWindowPane implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            res.setVisible(true);
            res.setText("WINDOW RESIZED");
            frame.setSize(600, 700);
        }
    }


    private static class userHelp implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            String messageCheck = res.getText();
            if (messageCheck.toString() != "")
            {
                res.setText("");
            }
            JOptionPane.showMessageDialog(res, "\nProgrammed by CCM. V8\nBase64toPDF Conversion Tool User Guide\nBUTTON FUNCTIONS:\n========\n⛶- Resizes Window back to original size\n========\nConvert- Decodes either the Base64 text OR full HL7 pasted in the text input area and will \ngenerate and open the PDF.\n========\nParse HL7 Files- Reads all files within the 'HL7 Input' folder location. It will detect any base64\nand decode all PDFs within the messages\n========\nDelete Last PDF- Deletes any generated PDFs within the 'C:\\Base64Conversion' folder\n========\nClear Text- Wipes the current text from the text input area\n========\nHL7 Input- Opens the Windows File Explorer to the 'C:\\Base64Conversion\\HL7FileInput' folder.\nDrop any HL7 messages in here for the 'Parse HL7 Files' button to read\n========\n\nNOTES:\n-V7. Added ability for Text Area to parse either Base64 or Full HL7 Messages\n-V7.6. Added functionality to parse HL7 Generated by a specifig\n-V8.0. Cleaned up code. Added ability to parse other embedded PDF types\n-V8.1. Added more logging and popup messages for errors");
        }
    }


    private static class openHL7FilePath implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            File folderMaker = new File("C:\\Base64Conversion\\HL7FileInput\\test.txt");
            folderMaker.getParentFile().mkdirs();
            folderMaker.delete();

            File file = new File ("C:\\Base64Conversion\\HL7FileInput");
            file.getParentFile().mkdirs();
            Desktop desktop = Desktop.getDesktop();
            try 
            {
                desktop.open(file);
            } 
            catch (IOException e1) 
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private static class cycleHL7FilesFolder implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            String messageCheck = res.getText();
            if (messageCheck.toString() != "")
            {
                res.setText("");
            }

            List<String> base64Lines = new ArrayList();
            List<String> base64TrimmedLines = new ArrayList();

            File dir = new File("C:\\Base64Conversion\\HL7FileInput");
            dir.getParentFile().mkdirs();

            File[] directoryListing = dir.listFiles();
            for(int a=0; a<directoryListing.length; a++)
            {
                System.out.println("DIRECTORYLISTING -"+ directoryListing[a]);
            }

            for (int i=0; i<directoryListing.length; i++) 
            {
                // Do something with child
                try (Scanner in = new Scanner(directoryListing[i])) 
                {
                    Boolean found=false;
                    while(in.hasNext()) 
                    {
                    	String line = in.nextLine();
                    	if(line.contains("JVBER")) 
                    	{
                    		base64Lines.add(line);
                    		found=true;
                    	}           
                    }
            
                    if(found==false) System.out.println("no match for your search");
                } 
                catch (FileNotFoundException e1) 
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            String foundText;
            for(int c=0; c<base64Lines.size(); c++)
            {
                foundText = base64Lines.get(c).substring(base64Lines.get(c).indexOf("JVBER"));
                foundText.trim();
                //convertProvidedBase64(foundText);

                Conversion convert = new Conversion();
                //convert.testBase64Valid(foundText);
                convert.convertProvidedBase64(foundText);
            }
            getConversionResults();

            Arrays.fill(directoryListing, null);
            base64TrimmedLines.clear();
            
            //Added this in v8 to display a popup if there are no files in HL7 Input, but the user attempts to parse them
            System.out.println("DirectoryListing Size: " + directoryListing.length);
            if(directoryListing.length == 0)
            {
                res.setVisible(true);
                System.out.println("HL7 Input Empty");
                res.setText("HL7 Input Empty");
            }

        }
    }
    
    public static String parrotError(String error)
    {
    	JOptionPane.showMessageDialog(res, error);
    	return "I am talking";
    }
    
    public static String getConversionResults()
    {
    	int failed;
    	int success;
    	
    	success = Conversion.getSuccess();
    	failed = Conversion.getFailed();
    	
    	res.setVisible(true);
    	
    	if(success == 0 && failed != 0)
    	{
    		res.setText("FAILED CONVERSION(S): " + failed);
    	}
    	else if(success != 0 && failed == 0)
    	{
    		res.setText("SUCCESSFUL CONVERSION(S): " + success);
    	}
    	else if(success != 0 && failed != 0)
    	{
    		System.out.println("Attempt to do a multi line");
    		res.setText("SUCCESSFUL: " + success + ". FAILED: " + failed);
    	}
    	else
    	{
    		System.out.println("Somehow nothing happened");
    	}
    	
    	return "Pulled results";
    }
}