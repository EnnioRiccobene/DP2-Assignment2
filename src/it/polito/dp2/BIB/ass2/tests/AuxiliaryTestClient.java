package it.polito.dp2.BIB.ass2.tests;
import java.math.BigInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.BIB.ass2.lib.jaxb.ObjectFactory;
import it.polito.dp2.BIB.ass2.lib.jaxb.Results;
import it.polito.dp2.BIB.ass2.lib.jaxb.Statement;
import it.polito.dp2.BIB.ass2.lib.jaxb.Statements;


public class AuxiliaryTestClient {
	private static String base_url="http://localhost:7474/db";
	Client client;
	
	int initialNumberOfNodes;
	int initialNumberOfRelationships;

	public AuxiliaryTestClient() throws AuxiliaryTestClientException{
		init(ClientBuilder.newClient());
	}

	public AuxiliaryTestClient(Client client) throws AuxiliaryTestClientException {
		init(client);
	}
	
	private void init(Client client) throws AuxiliaryTestClientException {
		this.client=client;
		initialNumberOfNodes = getCurrentNumberOfNodes();		
		initialNumberOfRelationships = getCurrentNumberOfRelationships();		
	}

	private int getCurrentNumberOfNodes() throws AuxiliaryTestClientException {
		try {
			// get number of nodes from Neo4J using cypher statement
			Results response = executeQuery("MATCH (n) RETURN COUNT(*)");
			BigInteger retval = response.getResults().get(0).getData().get(0).getRow().get(0);

			//return result as integer;
			return retval.intValue();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new AuxiliaryTestClientException("Unable to get current number of nodes from service");
		}
	}

	private int getCurrentNumberOfRelationships() throws AuxiliaryTestClientException {
		try {
			// get number of relationships from Neo4J using cypher statement
			Results response = executeQuery("MATCH((a)-[r:CitedBy]->(b)) RETURN(COUNT(*))");
			BigInteger retval = response.getResults().get(0).getData().get(0).getRow().get(0);

			//return result as integer;
			return retval.intValue();
		} catch (Exception e) {
			// e.printStackTrace(System.out);
			throw new AuxiliaryTestClientException("Unable to get current number of nodes from service");
		}
	}
	
	private int getNumberOfLinkedNodes(int id, int maxDepth) throws AuxiliaryTestClientException {
		try {
			// get number of relationships from Neo4J using cypher statement
			// get number of nodes from Neo4J using cypher statement
			String query = "START a=node("+id+") MATCH((a)-[r:CitedBy*.."+maxDepth+"]->(b)) RETURN(COUNT(*))";
			Results response = executeQuery(query);
			BigInteger retval = response.getResults().get(0).getData().get(0).getRow().get(0);

			//return result as integer;
			return retval.intValue();
		} catch (Exception e) {
			// e.printStackTrace(System.out);
			throw new AuxiliaryTestClientException("Unable to get current number of nodes from service");
		}
	}


	// performs the specified query on Neo4J and returns a Results object
	private Results executeQuery(String query) {
		ObjectFactory of = new ObjectFactory();
		Statements statements = of.createStatements();
		Statement stat1=of.createStatement();
		stat1.setStatement(query);
		statements.getStatements().add(stat1);
		Results response =client.target(base_url)
				.path("data/transaction/commit").request(MediaType.APPLICATION_JSON).post(Entity.entity(statements,MediaType.APPLICATION_JSON),Results.class);
		return response;
	}
	
	public int getAddedNodes() throws AuxiliaryTestClientException {
		return getCurrentNumberOfNodes()-initialNumberOfNodes;
	}
	
	public int getAddedRelationships() throws AuxiliaryTestClientException {
		return getCurrentNumberOfRelationships()-initialNumberOfRelationships;
	}

	public static void main(String[] args) throws AuxiliaryTestClientException {
		AuxiliaryTestClient client = new AuxiliaryTestClient();
		System.out.println(client.initialNumberOfNodes);
	}

}
