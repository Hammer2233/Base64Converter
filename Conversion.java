import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.awt.Desktop;

//created this class in version 8.1 to separate the conversion function from the button function
public class Conversion
{
    static int currentPDFNumber = 0;
    public static int setPDFNumTo0 (int currentNumber)
    {
        currentPDFNumber = currentNumber;
        return currentNumber;        
    }
    public static String convertProvidedBase64(String providedBase64)
    {
        /*
         * Created "convertProvidedBase64" in v8.0
         * This is to condense the duplicate functions used previously in converting text area and files in file input
         */
    	
    	testBase64Valid(providedBase64);

        //Variables for conversion
        String foundText;
        String endTextPlaceholder;

        //trims the providedBase64 line down to just the base64
        foundText = providedBase64.substring(providedBase64.indexOf("JVBER"));
        foundText.trim();
        //checks if there are any pipes at the end of the message
        if(!foundText.contains("|"))
        {
            //Added in V7.6. This detects if the HL7 was generated via a specifig
            if(foundText.contains("["))
            {
                System.out.println("IN Generated From Interface Monitor LOOP");
                String endChunk = foundText.substring(0, foundText.indexOf("["));
                foundText = endChunk;
            }
            System.out.println("NO ENDING FOUND");
            foundText = foundText.replaceAll("(?m)^[ \t]*\r?\n", "");
        }
        else
        {
            //condensed code for ending chars in version 8.2
            /* Below is the new evaluation logic
             * IF foundText contains "|"
             * THEN mark what the end characters are (from the first | to last character)
             * TRIM found characters off base64 string
             */
            System.out.println("ENDING FOUND");
            endTextPlaceholder = foundText.substring(foundText.indexOf("|"));
            System.out.println(endTextPlaceholder);

            System.out.println("IN | LOOP");
            foundText = foundText.replace(endTextPlaceholder, "");
        }

        //converts the current line to PDF
        String toTrim = foundText;

        File file = new File("C:\\Base64Conversion\\CONVERTED"+currentPDFNumber+".pdf");
        file.getParentFile().mkdirs();
        try ( FileOutputStream fos = new FileOutputStream(file); ) 
        {
            byte[] decoder = Base64.getDecoder().decode(toTrim);
    
            fos.write(decoder);
            System.out.println("PDF File Saved");
            if (Desktop.isDesktopSupported()) 
            {
                try 
                {
                    File myFile = new File("C:\\Base64Conversion\\CONVERTED"+currentPDFNumber+".pdf");
                    Desktop.getDesktop().open(myFile);
                } 
                catch (IOException ex) 
                {
                    // no application registered for PDFs
                }
            }
        }
        catch (Exception c) 
        {
            System.out.println("PDF GENERATION FAILED");
            System.out.println(c);
            if(c.toString().contains("Last unit does not have enough valid bits"))
            {
            	Base64V8.parrotError("ERROR GENERATING PDF!\nERROR: Last unit does not have enough valid bits\n\nCommon error for incomplete Base64 segments\nPlease verify the segment is fully intact");
            }
            else if(c.toString().contains("Illegal base64 character"))
            {
            	Base64V8.parrotError("ERROR GENERATING PDF!\nERROR: Illegal base64 character\n\nPlease ensure that there are no invalid characters in the\nBase64 (ex: semi-colons ';')");
            }
            c.printStackTrace();
        }
        currentPDFNumber++;

        return "hello";
    }
    
    public static String testBase64Valid(String base64)
    {
    	System.out.println("BEGINNING PDF VALIDATION");
    	try 
    	{
            // Decode the Base64 string
            byte[] pdfBytes = Base64.getDecoder().decode(base64);

            // Check if the byte array starts with the PDF signature
            String pdfHeader = "%PDF-";
            byte[] headerBytes = pdfHeader.getBytes();

            // Ensure the PDF starts with the correct header
            if (pdfBytes.length < headerBytes.length) 
            {
            	System.out.println("INVALID PDF");
                return "Invalid"; // Not enough data for a valid PDF header
            }

            for (int i = 0; i < headerBytes.length; i++) 
            {
                if (pdfBytes[i] != headerBytes[i]) 
                {
                	System.out.println("INVALID PDF");
                    return "Invalid"; // Header does not match
                }
            }

            System.out.println("VALID PDF");
            return "Valid"; // The Base64 string could likely be a valid PDF
        } 
    	catch (IllegalArgumentException e) 
    	{
            // This exception is thrown if the Base64 string is not properly encoded
    		System.out.println("VALID PDF");
            return "Invalid"; // Not a valid Base64 string
        }
    }
}