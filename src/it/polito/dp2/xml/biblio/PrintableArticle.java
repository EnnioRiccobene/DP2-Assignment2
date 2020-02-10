package it.polito.dp2.xml.biblio;

import it.polito.pad.dp2.biblio.ArticleType;
import it.polito.pad.dp2.biblio.BiblioItemType;

public class PrintableArticle extends PrintableItem {
	ArticleType article;

	public PrintableArticle(BiblioItemType item) {
		super(item);
		this.article = item.getArticle();
	}

	public void print() {
		super.print();
		if (article!=null)
			System.out.println("Article in journal "+article.getJournal()+", issue "+article.getIssue());
	}

}
