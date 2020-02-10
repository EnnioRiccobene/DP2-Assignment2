package it.polito.dp2.BIB.sol2;

import java.math.BigInteger;
import java.util.List;

import it.polito.dp2.BIB.sol2.crossref.client.jaxb.Message.Items;
import it.polito.dp2.BIB.sol2.crossref.client.jaxb.Message.Items.Author;
import it.polito.dp2.rest.gbooks.client.jaxb.IndustryIdentifier;
import it.polito.dp2.rest.gbooks.client.jaxb.VolumeInfo;
import it.polito.dp2.xml.biblio.PrintableItem;
import it.polito.pad.dp2.biblio.BiblioItemType;
import it.polito.pad.dp2.biblio.BookType;

public class Factory extends it.polito.dp2.xml.biblio.Factory {

	public static PrintableItem createPrintableItem(BigInteger id, VolumeInfo info) {
		BiblioItemType item = new BiblioItemType();
		item.setId(id);
		item.setTitle(info.getTitle());
		item.setSubtitle(info.getSubtitle());
		item.getAuthor().addAll(info.getAuthors());
		BookType book = new BookType();
		book.setPublisher(info.getPublisher());
		book.setYear(info.getPublishedDate());
		List<IndustryIdentifier> list = info.getIndustryIdentifiers();
		IndustryIdentifier ii = list.get(0);
		if (ii!=null)
			book.setISBN(ii.getIdentifier());
		item.setBook(book);
		return createPrintableItem(item);
	}
	
	public static PrintableItem createPrintableItem(BigInteger id, Items item) {		
		BiblioItemType biblioitem = new BiblioItemType();
		biblioitem.setId(id);
		biblioitem.setTitle(item.getTitle());
		biblioitem.setSubtitle(item.getSubtitle());
		for(Author author : item.getAuthor()){
			biblioitem.getAuthor().add(author.getGiven() + " " + author.getFamily());
		}
		BookType book = new BookType();
		if(item.getISBN().size() != 0)
			book.setISBN(item.getISBN().get(0));
		book.setPublisher(item.getPublisher());
		book.setYear(item.getCreated().getDateTime());
		biblioitem.setBook(book);
		return createPrintableItem(biblioitem);
		
	}

}
