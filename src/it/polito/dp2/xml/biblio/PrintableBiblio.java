package it.polito.dp2.xml.biblio;

import java.util.ArrayList;
import java.util.List;

import it.polito.pad.dp2.biblio.Biblio;
import it.polito.pad.dp2.biblio.BiblioItemType;
import it.polito.pad.dp2.biblio.JournalType;

public class PrintableBiblio {
	private List<PrintableItem> items;
	private List<PrintableJournal> journals;

	public PrintableBiblio(Biblio b) {
		items = new ArrayList<PrintableItem>();
		for (BiblioItemType item:b.getItem()) {
			items.add(Factory.createPrintableItem(item));
		}
		journals = new ArrayList<PrintableJournal>();
		for (JournalType journal:b.getJournal()) {
			journals.add(new PrintableJournal(journal));
		}
	}
	
	public void print()
    {
	    System.out.println("Bibliography");
	    System.out.println("Bibliography items:");
	    for (PrintableItem item:items)
	    	item.print();
	    System.out.println("====");
	    System.out.println("Bibliography Journals:");
	    for (PrintableJournal journal:journals)
	    	journal.print();
	    System.out.println("====");
	    System.out.println("End of Bibliography:");
    }

}
