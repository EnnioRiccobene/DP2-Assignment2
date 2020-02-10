package it.polito.dp2.BIB.ass2.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.BIB.*;
import it.polito.dp2.BIB.ass2.*;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;


public class BibTests {

	private static BibReader referenceBibReader;		// reference data generator
	private static CitationFinder testCitationFinder;	// implementation under test

	private static Client client;						// Client object used by auxiliary test client

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create reference data generator
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
		referenceBibReader = BibReaderFactory.newInstance().newBibReader();
		client = ClientBuilder.newClient();
	}

	@Before
	public void setUp() throws Exception {
		assertNotNull("Internal tester error during test setup: null reference", referenceBibReader);
	}

	private void createClient() throws CitationFinderException {
		// Create client under test
		try {
			testCitationFinder = CitationFinderFactory.newInstance().newCitationFinder();
		} catch (FactoryConfigurationError fce) {
			fce.printStackTrace();
		}
		assertNotNull("Internal tester error during test setup: null reference", referenceBibReader);
		assertNotNull("Could not run test: the implementation under test generated a null CitationFinder", testCitationFinder);
	}

	@Test
	public final void testNodeCreation() throws AuxiliaryTestClientException, CitationFinderException {
		System.out.println("DEBUG: starting testNodeCreation");
		// create auxiliary client for tracking added nodes
		AuxiliaryTestClient ct = new AuxiliaryTestClient(client);

		// create client under test
		createClient();

		// check right number of nodes has been created
		assertEquals("Wrong number of nodes", referenceBibReader.getItems(null, 0, 9999).size(), ct.getAddedNodes());
	}
	
	@Test
	public final void testRelationshipCreation() throws AuxiliaryTestClientException, CitationFinderException {
		System.out.println("DEBUG: starting testRelationshipCreation");
		// create auxiliary client for tracking added relationships
		AuxiliaryTestClient ct = new AuxiliaryTestClient(client);

		// create client under test
		createClient();

		// check right number of relationships has been created
		Set<ItemReader> items = referenceBibReader.getItems(null, 0, 9999);
		int numberOfRelationships = 0;
		for (ItemReader item:items) {
			numberOfRelationships += item.getCitingItems().size();
		}
		assertEquals("Wrong number of relationships", numberOfRelationships, ct.getAddedRelationships());
	}
	
	@Test
	public final void testFindAllCitingItems() throws CitationFinderException, UnknownItemException, ServiceException {
		System.out.println("DEBUG: starting testFindAllCitingItems");
		// create client under test
		createClient();
		
		Set<ItemReader> items = testCitationFinder.getItems(null, 0, 9999);
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", items);
		for (ItemReader item:items) {
			String title = item.getTitle();
			assertNotNull("The getItems of the implementation under test generated an ItemReader with null title", title);				
			System.out.println("item "+title);
			
			System.out.println("calling findAllCitingItems with maxDepth 1");
			Set<ItemReader> citingItems1 = testCitationFinder.findAllCitingItems(item, 1);
			assertNotNull("The findAllCitingItems of the implementation under test generated a null set of ItemReader", citingItems1);
			Set<ItemReader> refCitingItems1 = item.getCitingItems();
			assertNotNull("The getCitingItems of the implementation under test generated a null set", refCitingItems1);				
			assertEquals("Wrong number of citing items", refCitingItems1.size(), citingItems1.size());
			
			System.out.println("calling findAllCitingItems with maxDepth 2");
			Set<ItemReader> citingItems2 = testCitationFinder.findAllCitingItems(item, 2);
			assertNotNull("The findAllCitingItems of the implementation under test generated a null set of ItemReader", citingItems2);
			Set<ItemReader> refCitingItems2 = item.getCitingItems();
			assertNotNull("The getCitingItems of the implementation under test generated a null sete", refCitingItems2);				
			for (ItemReader nextItem:refCitingItems2) {
				Set<ItemReader> nextCitingItems = nextItem.getCitingItems();
				assertNotNull("The findAllCitingItems of the implementation under test generated a null set of ItemReader", nextCitingItems);
				refCitingItems2.addAll(nextCitingItems);
			}
			assertEquals("Wrong number of citing items", refCitingItems2.size(), citingItems2.size());
		}
	}

	@Test(expected = UnknownItemException.class)
	public final void testUnknownItem() throws CitationFinderException, UnknownItemException, ServiceException {
		System.out.println("DEBUG: starting testUnknownItem");
		// create client under test
		createClient();
		
		// try to execute findAllCitingItems using a wrong ItemReader
		testCitationFinder.findAllCitingItems(
			new ItemReader() {
				@Override
				public String[] getAuthors() {
					String[] retval = new String[1];
					retval[0]="Author";
					return retval;
				}

				@Override
				public Set<ItemReader> getCitingItems() {
					return new HashSet<ItemReader>();
				}

				@Override
				public String getSubtitle() {
					return null;
				}

				@Override
				public String getTitle() {
					return "Title";
			}}, 
			1
		);
	}
}
