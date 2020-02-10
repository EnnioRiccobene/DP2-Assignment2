package it.polito.dp2.BIB.sol2;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.BibReaderFactory;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.ass2.CitationFinderException;
import it.polito.dp2.BIB.ass2.ServiceException;
import it.polito.dp2.BIB.ass2.UnknownItemException;
import it.polito.dp2.BIB.sol2.Neo4jClient;
import it.polito.dp2.BIB.sol2.Neo4jClientException;
import it.polito.dp2.BIB.sol2.jaxb.Node;
import it.polito.dp2.BIB.sol2.jaxb.PruneType;
import it.polito.dp2.BIB.sol2.jaxb.RelType;
import it.polito.dp2.BIB.sol2.jaxb.Relationship;
import it.polito.dp2.BIB.sol2.jaxb.Traverse;

public class MyCitationFinder implements it.polito.dp2.BIB.ass2.CitationFinder {

	private BibReader monitor;
	private Map<ItemReader,Node> readerToNode;
	private Map<URL,ItemReader> urlToReader;
	private Neo4jClient nClient;
	
	public static void main(String[] args) throws CitationFinderException {
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
		MyCitationFinder cfi = new MyCitationFinder();
	}
	
	
	public MyCitationFinder() throws CitationFinderException {
		try {
			BibReaderFactory factory = BibReaderFactory.newInstance();
			monitor = factory.newBibReader();
			readerToNode = new HashMap<ItemReader, Node>();
			urlToReader = new HashMap<URL, ItemReader>();			
			nClient = new Neo4jClient();
			
			// create nodes
			Set<ItemReader> items = monitor.getItems(null, 0, 3000);
			for (ItemReader item : items) {
				Node node = nClient.createNode(item.getTitle());
				readerToNode.put(item, node);
				URL url = new URL(node.getSelf());
				urlToReader.put(url, item);	
			}
			for (ItemReader item : items) {
				for(ItemReader i : item.getCitingItems()){
					Relationship relationship = nClient.createRelationship(readerToNode.get(item), readerToNode.get(i));
				}	
			}
		} catch (Neo4jClientException | BibReaderException | MalformedURLException e) {
			throw new CitationFinderException(e);
		}
	}

	@Override
	public Set<ItemReader> findAllCitingItems(ItemReader item, int maxDepth) throws UnknownItemException, ServiceException {
		Set<ItemReader> citingItems = new HashSet<>();
		if(!readerToNode.containsKey(item)){
			throw new UnknownItemException("The starting element is not a valid ItemReader.");
		}
		if(maxDepth <= 0){
			maxDepth = 1;
		}
		
		Traverse traverse = nClient.of.createTraverse();
		
		traverse.setOrder("depth_first");
		PruneType prune = nClient.of.createPruneType();
		traverse.setPruneEvaluator(prune);
		traverse.getPruneEvaluator().setName("none");
		traverse.getPruneEvaluator().setLanguage("builtin");
		RelType relt = nClient.of.createRelType();
		relt.setDirection("out");
		relt.setType("CitedBy");
		traverse.getRelationships().add(relt);
		traverse.setMaxDepth(BigInteger.valueOf(maxDepth));
		
		try {

			List<Node> nodeInDepth = nClient.client.target(readerToNode.get(item).getSelf()).path("traverse").path("node")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.json(traverse), new GenericType<List<Node>>() {});

			for(Node n : nodeInDepth){
				citingItems.add(urlToReader.get(new URL(n.getSelf())));
			}

		} catch (WebApplicationException|ProcessingException e) {
			throw new ServiceException(e);
		} catch (MalformedURLException e) {
			throw new ServiceException(e);
		}  catch (Exception e) {
			throw new ServiceException(e);
		}	
		
		return citingItems;
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
