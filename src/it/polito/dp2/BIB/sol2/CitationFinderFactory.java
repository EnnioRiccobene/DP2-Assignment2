package it.polito.dp2.BIB.sol2;

import it.polito.dp2.BIB.ass2.CitationFinder;
import it.polito.dp2.BIB.ass2.CitationFinderException;

public class CitationFinderFactory extends it.polito.dp2.BIB.ass2.CitationFinderFactory {
	
	private MyCitationFinder finder;

	@Override
	public CitationFinder newCitationFinder() throws CitationFinderException {

        try {
    		if(finder == null){
    			finder = new MyCitationFinder();
    			return finder;
    		} else {
    			return finder;
    		}
        }
        catch (Exception e) {
            throw new CitationFinderException(e);
        }
	}

}
