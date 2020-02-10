package it.polito.dp2.BIB.sol2;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.BIB.sol2.jaxb.Data;
import it.polito.dp2.BIB.sol2.jaxb.Node;
import it.polito.dp2.BIB.sol2.jaxb.Relationship;
import it.polito.dp2.BIB.sol2.Neo4jClientException;
import it.polito.dp2.BIB.sol2.jaxb.ObjectFactory;


public class Neo4jClient {
	Client client;
	WebTarget target;
	String uri = "http://localhost:7474/db";
	String urlProperty = "it.polito.dp2.BIB.ass2.URL";
	String portProperty = "it.polito.dp2.BIB.ass2.PORT";
	ObjectFactory of = new ObjectFactory();

	public Neo4jClient() {
		client = ClientBuilder.newClient();
		
		String customUri = System.getProperty(urlProperty);
		String customPort = System.getProperty(portProperty);
		if (customUri != null)
			uri = customUri;
		
		target = client.target(uri).path("data");
	}
	
	public void close() {
		client.close();
	}

	public Node createNode(String title) throws Neo4jClientException {
		Data data = of.createData();
		data.setTitle(title);
		try {
			Node node = target.path("node")
				  .request(MediaType.APPLICATION_JSON_TYPE)
				  .post(Entity.json(data), Node.class);			
			return node;
		} catch (WebApplicationException|ProcessingException e) {
			throw new Neo4jClientException(e);
		}
	}
	
	public Relationship createRelationship(Node from, Node to) throws Neo4jClientException {
		Relationship relation = of.createRelationship();
		relation.setTo(to.getSelf());
		relation.setType("CitedBy");
		try {			
			relation = client.target(from.getSelf()).path("relationships")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.json(relation), Relationship.class);			
			return relation;			
		} catch (WebApplicationException|ProcessingException e) {
			throw new Neo4jClientException(e);
		}
	}
}
