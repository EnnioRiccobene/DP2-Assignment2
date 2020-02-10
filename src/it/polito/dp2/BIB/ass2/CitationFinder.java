package it.polito.dp2.BIB.ass2;

import java.util.Set;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.ItemReader;

public interface CitationFinder extends BibReader {
	/**
	 * Finds readers for all the items that cite the given item directly or indirectly
	 * @param item the reader for the item for which all citing items have to be found (must be an ItemReader returned by the same interface implementation)
	 * @param maxDepth the maximum depth used by the find operation (a positive integer; if less than or equal to 0, it is assumed to be 1)
	 * @return a set containing readers for all the items that cite the given item
	 * @throws UnknownItemException if item is not an object returned by the same interface implementation
	 * @throws ServiceException if the operation cannot be completed because of other reasons related to the interaction with the service
	 */
	public Set<ItemReader> findAllCitingItems(ItemReader item, int maxDepth) throws UnknownItemException, ServiceException;

}
