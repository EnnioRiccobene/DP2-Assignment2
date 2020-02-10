package it.polito.dp2.rest.gbooks.client;

import java.math.BigInteger;
import java.util.List;

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

}
