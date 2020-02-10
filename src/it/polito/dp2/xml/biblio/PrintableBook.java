package it.polito.dp2.xml.biblio;

import it.polito.pad.dp2.biblio.BiblioItemType;
import it.polito.pad.dp2.biblio.BookType;

public class PrintableBook extends PrintableItem {
	private BookType book;

	public PrintableBook(BiblioItemType item) {
		super(item);
		book = item.getBook();
	}
	
	public void print() {
		super.print();
		if (book!=null) {
		    System.out.println("ISBN: "+book.getISBN());
		    System.out.println("Publisher: "+book.getPublisher());
		    System.out.println("Year: "+book.getYear().getYear());
		}
	}
}
