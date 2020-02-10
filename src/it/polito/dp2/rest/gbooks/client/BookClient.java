package it.polito.dp2.rest.gbooks.client;

import javax.ws.rs.client.Client;
import it.polito.dp2.rest.gbooks.client.jaxb.*;
import it.polito.dp2.xml.biblio.PrintableItem;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;


public class BookClient {
	
	JAXBContext jc;
	javax.xml.validation.Validator validator;

	public static void main(String[] args) {
		if (args.length == 0) {
	          System.err.println("Usage: java BookClient keyword1 keyword2 ...");
	          System.exit(1);
	    }
		try {
			BookClient bclient = new BookClient();
			bclient.PerformSearch(args);
		} catch(Exception ex ){
			System.err.println("Error during execution of operation");
			ex.printStackTrace(System.out);
		}
	}
	
	public BookClient() throws Exception {        
    	// create validator that uses the DataTypes schema
    	SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    	Schema schema = sf.newSchema(new File("xsd/gbooks/DataTypes.xsd"));
    	validator = schema.newValidator();
    	validator.setErrorHandler(new MyErrorHandler());
    	
		// create JAXB context related to the classed generated from the DataTypes schema
        jc = JAXBContext.newInstance("it.polito.dp2.rest.gbooks.client.jaxb");
	}
	
	public void PerformSearch(String[] kw){
		// build the JAX-RS client object 
		Client client = ClientBuilder.newClient();
		
		// build the web target
		WebTarget target = client.target(getBaseURI()).path("volumes");
		
		// perform a get request using mediaType=APPLICATION_JSON
		// and convert the response into a SearchResult object
		StringBuffer queryString = new StringBuffer(kw[0]);
		for (int i=1; i<kw.length; i++) {
			queryString.append(' ');
			queryString.append(kw[i]);
		}
		System.out.println("Searching "+queryString+" on Google Books:");
		Response response = target
							   .queryParam("q", queryString)
							   .queryParam("printType", "books")
							   .request()
							   .accept(MediaType.APPLICATION_JSON)
							   .get();
		if (response.getStatus()!=200) {
			System.out.println("Error in remote operation: "+response.getStatus()+" "+response.getStatusInfo());
			return;
		}
		response.bufferEntity();
		System.out.println("Response as string: "+response.readEntity(String.class));
		SearchResult result = response.readEntity(SearchResult.class);
		
		System.out.println("OK Response received. Items:"+result.getTotalItems());
		
		System.out.println("Validating items and converting validated items to xml.");
		// create empty list
		List<PrintableItem> pitems = new ArrayList<PrintableItem>();
		int i=0;
		for (Items item:result.getItems()) {
			try {
				// validate item
		    	JAXBSource source = new JAXBSource(jc, item);
		    	System.out.println("Validating "+item.getSelfLink());
		    	validator.validate(source);
		    	System.out.println("Validation OK");
		    	// add item to list
				System.out.println("Adding item to list");
				pitems.add(Factory.createPrintableItem(BigInteger.valueOf(i++),item.getVolumeInfo()));
			} catch (org.xml.sax.SAXException se) {
			      System.out.println("Validation Failed");
			      // print error messages
			      Throwable t = se;
			      while (t!=null) {
				      String message = t.getMessage();
				      if (message!= null)
				    	  System.out.println(message);
				      t = t.getCause();
			      }
			} catch (IOException e) {
				System.out.println("Unexpected I/O Exception");
			} catch (JAXBException e) {
				System.out.println("Unexpected JAXB Exception");
			}
		}
	    System.out.println("Validated Bibliography items: "+pitems.size());
	    for (PrintableItem item:pitems)
	    	item.print();
	    System.out.println("End of Validated Bibliography items");
	}
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("https://www.googleapis.com/books/v1").build();
	}

}
