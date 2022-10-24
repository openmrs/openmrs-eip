package org.openmrs.eip.component.config;

/**
 * Class holding encryption properties on sender's side
 */
public class SenderEncryptionProperties implements EncryptionProperties {
	
	private String keysFolderPath;
	
	private String userId;
	
	private String password;
	
	private String receiverUserId;
	
	/**
	 * Path to the folder containing the private key and the public keys of the module If the path
	 * starts with 'file:', the program will look in the absolute path following the prefix. Otherwise,
	 * it will look relatively to the root folder of the application
	 * 
	 * @return path
	 */
	@Override
	public String getKeysFolderPath() {
		return keysFolderPath;
	}
	
	public void setKeysFolderPath(final String keysFolderPath) {
		this.keysFolderPath = keysFolderPath;
	}
	
	/**
	 * The user id of the private key
	 * 
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(final String userId) {
		this.userId = userId;
	}
	
	/**
	 * Password for the private pgp key
	 * 
	 * @return password
	 */
	@Override
	public String getPassword() {
		return password;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	/**
	 * The user id of the receiver
	 * 
	 * @return the user id
	 */
	public String getReceiverUserId() {
		return receiverUserId;
	}
	
	public void setReceiverUserId(final String receiverUserId) {
		this.receiverUserId = receiverUserId;
	}
}
