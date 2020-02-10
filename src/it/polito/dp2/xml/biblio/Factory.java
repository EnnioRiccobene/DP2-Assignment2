package it.polito.dp2.xml.biblio;

import it.polito.pad.dp2.biblio.BiblioItemType;

public class Factory {
	public static PrintableItem createPrintableItem(BiblioItemType item) {
		if(item.getArticle()!=null)
			return new PrintableArticle(item);
		else if (item.getBook()!=null)
			return new PrintableBook(item);
		else return new PrintableItem(item);
	}

}
