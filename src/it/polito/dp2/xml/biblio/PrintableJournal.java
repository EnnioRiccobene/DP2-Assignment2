package it.polito.dp2.xml.biblio;

import it.polito.pad.dp2.biblio.JournalType;

public class PrintableJournal {
	private JournalType j;

	public PrintableJournal(JournalType j) {
		this.j = j;
	}
	
	public void print()
    {
		System.out.println("----");
		System.out.println("Journal:"+j.getTitle());
	    System.out.println("Publisher:"+j.getPublisher());
	    System.out.println("ISSN:"+j.getISSN());
	    System.out.println("Issues:");
	    for(JournalType.Issue issue: j.getIssue()) {
		    System.out.println("issue "+issue.getId()
		                       +", year:"+issue.getYear()
		                       +", number:"+issue.getNumber());
	    }
    }

}
