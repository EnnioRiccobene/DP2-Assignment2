/*
 * Sample JAXB Programming
 * This class uses the JAXB annotated classes generated from the schema biblio.xsd 
 * This class uses validation
 * 
 */
package it.polito.dp2.xml.biblio;
import it.polito.pad.dp2.biblio.*;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import org.xml.sax.*;

public class JAXBBiblio {
    private PrintableBiblio printableBiblio;

    public JAXBBiblio(String fname) throws SAXException, JAXBException 
    {
        try {
        	// initialize JAXBContext and create unmarshaller
            JAXBContext jc = JAXBContext.newInstance( "it.polito.pad.dp2.biblio" );
            Unmarshaller u = jc.createUnmarshaller();
            
            // set validation wrt schema using default validation handler (rises exception with non-valid files)
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File("xsd/biblio.xsd"));
            u.setSchema(schema);
                       
            // unmarshal file named fname
            Biblio biblio = (Biblio) u.unmarshal( new File( fname ) );
            
            // initialize specific entities
            printableBiblio = new PrintableBiblio(biblio);
   	
        } catch (SAXException se) {
    		System.out.println("Unable to validate schema");
    		throw se;
        }
    }

	// Print out biblio contents
    public void printBiblio() 
    {		
		printableBiblio.print();
    }

    public static void main(String args[]) 
    {
        if (args.length != 1) {
            System.err.println("Usage: java JAXBBiblio xmlfilename");
            System.exit(1);
        }
		try {
			JAXBBiblio dl = new JAXBBiblio(args[0]);
	        dl.printBiblio();
		} catch (Exception e) {
			System.out.println("Aborting due to error in file unmarshalling phase");
			e.printStackTrace();
			System.exit(1);
		}
    }
}

