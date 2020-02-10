package it.polito.dp2.xml.biblio.json;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.math.BigInteger;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import it.polito.pad.dp2.biblio.Biblio;
import it.polito.pad.dp2.biblio.BiblioItemType;
import it.polito.pad.dp2.biblio.BookType;
import it.polito.pad.dp2.biblio.ObjectFactory;
/*
 * Class that demonstrates the marshalling and unmarshalling
 * of a JAXB annotated object to/from JSON using Eclipse MOXy
 * (with validation against an XML schema)
 */
public class MarshalBook {

    public static void main(String[] args) throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(2);
        // Here we could set the media type in the context
        // properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
        JAXBContext jc = JAXBContext.newInstance(new Class[] {Biblio.class, ObjectFactory.class}, properties);
        
        // Build a biblio object
        ObjectFactory of = new ObjectFactory();
        Biblio biblio = of.createBiblio();
        BiblioItemType item = of.createBiblioItemType();
        BookType book = of.createBookType();
        book.setISBN("1234567890");
        book.setPublisher("Publisher");
        try {
        	GregorianCalendar gc = new GregorianCalendar();
        	gc.set(GregorianCalendar.YEAR, 2001);
        	book.setYear(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
    	} catch (DatatypeConfigurationException e) {
    	    throw new Error(e);
    	}
        item.setBook(book);
        item.setId(new BigInteger("123"));
        item.getAuthor().add("Author");
        item.setTitle("Title");
        biblio.getItem().add(item);
        
        // Create marshaller
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        
        // Enable validation during marshalling
        try {
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File("xsd/biblio.xsd"));
            marshaller.setSchema(schema);
        } catch (org.xml.sax.SAXException se) {
            System.out.println("Unable to validate due to following error.");
            se.printStackTrace();
        }

        // Marshal the biblio object
        marshaller.marshal(biblio, new File("biblio.json"));
        
        // Create unmarshaller and unmarshal biblio object again
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        biblio = (Biblio)unmarshaller.unmarshal(new File("biblio.json"));
        // Validation after unmarshalling (validation of JAXB-annotated objects)
	    try {
	    	SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
	    	Schema schema = sf.newSchema(new File("xsd/biblio.xsd"));
	    	javax.xml.validation.Validator validator = schema.newValidator();
	    	validator.setErrorHandler(new MyErrorHandler());
	    	JAXBSource source = new JAXBSource(jc, biblio);
	    	validator.validate(source);
		} catch (org.xml.sax.SAXException se) {
		      System.out.println("Unable to validate due to following error.");
		      se.printStackTrace();
		}
	        
	    System.out.println("OK");
	        
	    // Finally, marshal to standard output without validation
	    marshaller.setSchema(null);
	    marshaller.marshal(biblio, System.out);
    }

}