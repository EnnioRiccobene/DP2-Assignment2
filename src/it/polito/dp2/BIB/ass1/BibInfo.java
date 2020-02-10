package it.polito.dp2.BIB.ass1;

import java.util.Set;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.BibReaderFactory;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;


public class BibInfo {
	private BibReader monitor;

	
	/**
	 * Default constructror
	 * @throws BibReaderException 
	 */
	public BibInfo() throws BibReaderException {
		BibReaderFactory factory = BibReaderFactory.newInstance();
		monitor = factory.newBibReader();
	}
	
	public BibInfo(BibReader monitor) {
		super();
		this.monitor = monitor;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BibInfo wf;
		try {
			wf = new BibInfo();
			wf.printAll();
		} catch (BibReaderException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void printAll() {
		printLine(' ');
	    System.out.println("Bibliography");
	    System.out.println("Bibliography items:");
		printLine(' ');

		printItems();
		printJournals();

	}

	private void printItems() {
		// Get the list of Items
		Set<ItemReader> set = monitor.getItems(null, 0, 3000);
		
		/* Print the header of the table */
		printHeader('#',"#Information about ITEMS");
		printHeader("#Number of Items: "+set.size());
		printHeader("#List of Items:");
		printLine('-');
		
		// For each Item print related data
		for (ItemReader item: set) {
			System.out.println("Title: "+item.getTitle());
			if (item.getSubtitle()!=null)
				System.out.println("Subtitle: "+item.getSubtitle());
			System.out.print("Authors: ");
			String[] authors = item.getAuthors();
			System.out.print(authors[0]);
			for (int i=1; i<authors.length; i++)
				System.out.print(", "+authors[i]);
			System.out.println(";");
			if (item instanceof ArticleReader) {
				ArticleReader article = (ArticleReader) item;
				System.out.println("Article in Journal "+article.getJournal().getTitle()+ "; Issue "+ article.getIssue().getNumber() + ","+article.getIssue().getYear());
			}
			if (item instanceof BookReader) {
				BookReader book = (BookReader) item;
				System.out.print("Book ");
				System.out.print("published by "+book.getPublisher());
				System.out.print(" in "+book.getYear());
				System.out.println(" (ISBN "+book.getISBN()+")");
			}
			Set<ItemReader> citingItems = item.getCitingItems();
			System.out.println("Cited by "+citingItems.size()+" items:");
			for (ItemReader citing: citingItems) {
				System.out.println("- "+citing.getTitle());
			}	
			printLine('-');

		}
		printBlankLine();
	}
	
	private void printJournals() {
		// Get the list of journals
		Set<JournalReader> set = monitor.getJournals(null);
		/* Print the header of the table */
		printHeader('#',"#Information about JOURNALS");
		printHeader("#Number of Journals: "+set.size());
		printHeader("#List of Journals:");
		printLine('-');
		
		for (JournalReader journal:set) {
			System.out.println("Title: "+journal.getTitle());
			System.out.println("Publisher: "+journal.getPublisher());
			System.out.println("ISSN: "+journal.getISSN());
			System.out.println("Issues:");
			for (IssueReader issue: journal.getIssues(0, 3000)) {
				System.out.println("Year: "+issue.getYear()+";"+" Number: "+issue.getNumber());
			}
			printLine('-');
		}	

	}

	private void printBlankLine() {
		System.out.println(" ");
	}

	
	private void printLine(char c) {
		System.out.println(makeLine(c));
	}

	private void printHeader(String header) {
		System.out.println(header);
	}

	private void printHeader(String header, char c) {		
		System.out.println(header);
		printLine(c);	
	}
	
	private void printHeader(char c, String header) {		
		printLine(c);	
		System.out.println(header);
	}
	
	private StringBuffer makeLine(char c) {
		StringBuffer line = new StringBuffer(132);
		
		for (int i = 0; i < 132; ++i) {
			line.append(c);
		}
		return line;
	}

}
