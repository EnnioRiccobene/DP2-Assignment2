package it.polito.dp2.xml.biblio;

import java.math.BigInteger;
import java.util.Iterator;

import it.polito.pad.dp2.biblio.BiblioItemType;

public class PrintableItem {
	private BiblioItemType item;

	public PrintableItem(BiblioItemType item) {
		this.item = item;
	}
	
	public void print() {
		System.out.println("----");
	    System.out.println("Item "+item.getId());
	    System.out.println("Title: "+item.getTitle());
	    if (item.getSubtitle()!=null)
	    	System.out.println("Subtitle: "+item.getSubtitle());
	    System.out.print("Authors: ");
	    Iterator<String> si = item.getAuthor().iterator();
	    System.out.print(si.next());
	    while (si.hasNext())
	    	System.out.print(", "+si.next());
	    System.out.println();
	    System.out.print("Cited by: ");
	    Iterator<BigInteger> ii = item.getCitedBy().iterator();
	    if (ii.hasNext())
	    	System.out.print(ii.next());
	    while (ii.hasNext())
	    	System.out.print(", "+ii.next());
	    System.out.println();
	}

}
