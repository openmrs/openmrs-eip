package org.openmrs.eip.component.config;

/**
 * interface for object holding the path to find public and private keys and the password of the
 * private key
 */
public interface EncryptionProperties {
	
	/**
	 * Password for the private pgp key
	 * 
	 * @return password
	 */
	String getPassword();
	
	/**
	 * Path to the folder containing the private key and the public keys of the module If the path
	 * starts with 'file:', the program will look in the absolute path following the prefix. Otherwise,
	 * it will look relatively to the root folder of the application
	 * 
	 * @return path
	 */
	String getKeysFolderPath();
}
