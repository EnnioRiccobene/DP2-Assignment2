package it.polito.dp2.rest.neo4j.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.BibReaderFactory;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.ass2.CitationFinder;
import it.polito.dp2.BIB.ass2.CitationFinderException;
import it.polito.dp2.BIB.ass2.ServiceException;
import it.polito.dp2.BIB.ass2.UnknownItemException;
import it.polito.dp2.rest.neo4j.client.jaxb.Node;

public class CitationFinderImpl implements CitationFinder {
	private BibReader monitor;
	private Map<ItemReader,Node> readerToNode;
	private Map<URL,ItemReader> urlToReader;
	private Neo4jClient client;

	
	public static void main(String[] args) throws CitationFinderException {
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
		CitationFinderImpl cfi = new CitationFinderImpl();
	}
	
	public CitationFinderImpl() throws CitationFinderException {
		try {
			BibReaderFactory factory = BibReaderFactory.newInstance();
			monitor = factory.newBibReader();
			readerToNode = new HashMap<ItemReader, Node>();
			urlToReader = new HashMap<URL, ItemReader>();
			client = new Neo4jClient();
			// create nodes
			Set<ItemReader> items = monitor.getItems(null, 0, 3000);
			for (ItemReader item : items) {
				Node node = client.createNode(item.getTitle());
				readerToNode.put(item, node);
				URL url = new URL(node.getSelf());
				urlToReader.put(url, item);
			}
		} catch (Neo4jClientException | BibReaderException | MalformedURLException e) {
			throw new CitationFinderException(e);
		}
	}

	@Override
	public Set<ItemReader> findAllCitingItems(ItemReader item, int maxDepth) throws UnknownItemException, ServiceException {
		return null;
	}

	@Override
	public BookReader getBook(String arg0) {
		return monitor.getBook(arg0);
	}

	@Override
	public Set<ItemReader> getItems(String arg0, int arg1, int arg2) {
		return monitor.getItems(arg0, arg1, arg2);
	}

	@Override
	public JournalReader getJournal(String arg0) {
		return monitor.getJournal(arg0);
	}

	@Override
	public Set<JournalReader> getJournals(String arg0) {
		return monitor.getJournals(arg0);
	}

}
