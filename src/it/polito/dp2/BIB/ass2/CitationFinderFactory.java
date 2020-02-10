/**
 * 
 */
package it.polito.dp2.BIB.ass2;

import it.polito.dp2.BIB.FactoryConfigurationError;

/**
 * Defines a factory API that enables applications to obtain one or more objects
 * implementing the {@link CitationFinder} interface.
 *
 */
public abstract class CitationFinderFactory {

	private static final String propertyName = "it.polito.dp2.BIB.ass2.CitationFinderFactory";
	
	protected CitationFinderFactory() {}
	
	/**
	 * Obtain a new instance of a <tt>CitationFinderFactory</tt>.
	 * 
	 * <p>
	 * This static method creates a new factory instance. This method uses the
	 * <tt>it.polito.dp2.BIB.ass2.CitationFinderFactory</tt> system property to
	 * determine the CitationFinderFactory implementation class to load.
	 * </p>
	 * <p>
	 * Once an application has obtained a reference to a
	 * <tt>CitationFinderFactory</tt> it can use the factory to obtain a new
	 * {@link CitationFinder} instance.
	 * </p>
	 * 
	 * @return a new instance of a <tt>CitationFinderFactory</tt>.
	 * 
	 * @throws FactoryConfigurationError if the implementation is not available 
	 * or cannot be instantiated.
	 */
	public static CitationFinderFactory newInstance() throws FactoryConfigurationError {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if(loader == null) {
			loader = CitationFinderFactory.class.getClassLoader();
		}
		
		String className = System.getProperty(propertyName);
		if (className == null) {
			throw new FactoryConfigurationError("cannot create a new instance of a CitationFinderFactory"
												+ "since the system property '" + propertyName + "'"
												+ "is not defined");
		}
		
		try {
			Class<?> c = (loader != null) ? loader.loadClass(className) : Class.forName(className);
			return (CitationFinderFactory) c.newInstance();
		} catch (Exception e) {
			throw new FactoryConfigurationError(e, "error instantiatig class '" + className + "'.");
		}
	}
	
	
	/**
	 * Creates a new instance of a {@link CitationFinder} implementation.
	 * 
	 * @return a new instance of a {@link CitationFinder} implementation.
	 * @throws CitationFinderException if an implementation of {@link CitationFinder} cannot be created.
	 */
	public abstract CitationFinder newCitationFinder() throws CitationFinderException;
}