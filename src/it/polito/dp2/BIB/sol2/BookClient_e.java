package it.polito.dp2.BIB.sol2;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
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

import it.polito.dp2.BIB.sol2.crossref.client.jaxb.CrossrefSearchResult;
import it.polito.dp2.rest.gbooks.client.jaxb.Items;
import it.polito.dp2.rest.gbooks.client.jaxb.SearchResult;
import it.polito.dp2.xml.biblio.PrintableItem;

public class BookClient_e {
	JAXBContext jc;
	javax.xml.validation.Validator validator;
	JAXBContext jc2;
	javax.xml.validation.Validator validator2;

	public static void main(String[] args) {
		if (args.length == 0) {
	          System.err.println("Usage: java BookClient keyword1 keyword2 ...");
	          System.exit(1);
	    }
		for(int i = 0; i < args.length; i++){
			System.out.println(args[i]);
		}
		
		try{
			
			int n = Integer.parseInt(args[0]);
			System.out.println("N value: " + n);
			ArrayList<String> keywords = new ArrayList<>();
			for(int i = 1; i < args.length - 1; i++){
				keywords.add(args[i]);
			}
		
			BookClient_e bclient = new BookClient_e();
			ArrayList<PrintableItem> pitemsGoogle = new ArrayList<>();
			ArrayList<PrintableItem> pitemsCrossref = new ArrayList<>();
			System.out.println("Search in gbook:");
			bclient.PerformGBooksSearch(n, keywords, pitemsGoogle, 0);
			System.out.println();
			System.out.println("Search in crossref:");
			bclient.PerformCrossrefSearch(n, keywords, pitemsCrossref, 0);
			
		    System.out.println("Validated Google Bibliography items: "+pitemsGoogle.size());
		    for (PrintableItem item:pitemsGoogle)
		    	item.print();
		    System.out.println("End of Validated Google Bibliography items");
		    System.out.println();
		    System.out.println("Validated Crossref Bibliography items: "+pitemsCrossref.size());
		    for (PrintableItem item:pitemsCrossref)
		    	item.print();
		    System.out.println("End of Validated Crossref Bibliography items");
		    
		} catch(NumberFormatException exception) {
		    System.err.println("the first parameter '" + args[0] + "' is not a number");
		} catch(Exception ex ){
			System.err.println("Error during execution of operation");
			ex.printStackTrace(System.out);
		}
	}
	
	public BookClient_e() throws Exception {        
    	// create validator that uses the DataTypes schema
    	SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    	Schema schema = sf.newSchema(new File("xsd/gbooks/DataTypes.xsd"));
    	validator = schema.newValidator();
    	validator.setErrorHandler(new MyErrorHandler());
    	Schema schema2 = sf.newSchema(new File("xsd/gbooks/CrossrefDataTypes.xsd"));
    	validator2 = schema2.newValidator();
    	validator2.setErrorHandler(new MyErrorHandler());
    	
		// create JAXB context related to the classed generated from the DataTypes schema
        jc = JAXBContext.newInstance("it.polito.dp2.rest.gbooks.client.jaxb");
        jc2 = JAXBContext.newInstance("it.polito.dp2.BIB.sol2.crossref.client.jaxb");
	}
	
	public void PerformGBooksSearch(int n, ArrayList<String> keywords, List<PrintableItem> pitems, int startIndex) {
		// build the JAX-RS client object
		Client client = ClientBuilder.newClient();

		// build the web target
		WebTarget target = client.target(getBaseURI()).path("volumes");

		// perform a get request using mediaType=APPLICATION_JSON
		// and convert the response into a SearchResult object
		StringBuffer queryString = new StringBuffer(keywords.get(0));
		for (int i = 1; i < keywords.size(); i++) {
			queryString.append(' ');
			queryString.append(keywords.get(i));
		}
		System.out.println("Searching " + queryString + " on Google Books:");
		Response response = target.queryParam("q", queryString)
				.queryParam("printType", "books")
				.queryParam("startIndex", startIndex)
				.request()
				.accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			System.out.println("Error in remote operation: " + response.getStatus() + " " + response.getStatusInfo());
			return;
		}
		
		response.bufferEntity();
		System.out.println("Response as string: " + response.readEntity(String.class));
		SearchResult result = response.readEntity(SearchResult.class);

		System.out.println("OK Response received. Items:" + result.getTotalItems());

		System.out.println("Validating items and converting validated items to xml.");

		for (Items item : result.getItems()) {
			try {
				// validate item
				JAXBSource source = new JAXBSource(jc, item);
				System.out.println("Validating " + item.getSelfLink());
				validator.validate(source);
				System.out.println("Validation OK");
				// add item to list
				System.out.println("Adding item to list");
				pitems.add(Factory.createPrintableItem(BigInteger.valueOf(pitems.size()), item.getVolumeInfo()));
				if(pitems.size() == n)
					return;
				
			} catch (org.xml.sax.SAXException se) {
				System.out.println("Validation Failed");
				// print error messages
				Throwable t = se;
				while (t != null) {
					String message = t.getMessage();
					if (message != null)
						System.out.println(message);
					t = t.getCause();
				}
			} catch (IOException e) {
				System.out.println("Unexpected I/O Exception");
			} catch (JAXBException e) {
				System.out.println("Unexpected JAXB Exception");
			}
		}
		
		if(pitems.size() < n){
			startIndex = startIndex + 10;
			this.PerformGBooksSearch(n, keywords, pitems, startIndex);
		}

	}
	
	public void PerformCrossrefSearch(int n, ArrayList<String> keywords, List<PrintableItem> pitems, int startIndex){
		// build the JAX-RS client object
		Client client = ClientBuilder.newClient();

		// build the web target
		WebTarget target = client.target(getBaseCrossrefURI());

		// perform a get request using mediaType=APPLICATION_JSON
		// and convert the response into a SearchResult object
		StringBuffer queryString = new StringBuffer(keywords.get(0));
		for (int i = 1; i < keywords.size(); i++) {
			queryString.append(' ');
			queryString.append(keywords.get(i));
		}
		System.out.println("Searching " + queryString + " on Crossref:");
		Response response = target.queryParam("query", queryString)
				.queryParam("filter", "type:book")
				.queryParam("rows", 20)
				.queryParam("offset", startIndex)
				.request()
				.accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			System.out.println("Error in remote operation: " + response.getStatus() + " " + response.getStatusInfo());
			return;
		}

		response.bufferEntity();
		System.out.println("Response as string: " + response.readEntity(String.class));

		CrossrefSearchResult result = response.readEntity(CrossrefSearchResult.class);

		System.out.println("OK Response received. Items:" + result.getMessage().getTotalResults());

		System.out.println("Validating items and converting validated items to xml.");

		for (it.polito.dp2.BIB.sol2.crossref.client.jaxb.Message.Items item : result.getMessage().getItems()) {
			try {
				// validate item
				JAXBSource source = new JAXBSource(jc2, result.getMessage());
				System.out.println("Validating " + item.getTitle());
				validator2.validate(source);
				System.out.println("Validation OK");
				// add item to list
				System.out.println("Adding item to list");

				pitems.add(Factory.createPrintableItem(BigInteger.valueOf(pitems.size()), item));

				if(pitems.size() == n)
					return;

			} catch (org.xml.sax.SAXException se) {
				System.out.println("Validation Failed");
				// print error messages
				Throwable t = se;
				while (t != null) {
					String message = t.getMessage();
					if (message != null)
						System.out.println(message);
					t = t.getCause();
				}
			} catch (IOException e) {
				System.out.println("Unexpected I/O Exception");
			} catch (JAXBException e) {
				System.out.println("Unexpected JAXB Exception");
			}
		}

		if(pitems.size() < n) {
			startIndex = startIndex + 20;
			this.PerformCrossrefSearch(n, keywords, pitems, startIndex);
		}
			
	}
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("https://www.googleapis.com/books/v1").build();
	}
	
	private static URI getBaseCrossrefURI() {		
		//https://api.crossref.org/works?query=introduction+welcome&filter=type:book&rows=20&offset=0
	    return UriBuilder.fromUri("https://api.crossref.org/works").build();
	}
}
