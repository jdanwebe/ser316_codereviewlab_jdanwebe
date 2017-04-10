package banking.primitive.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

import banking.primitive.core.Account.State;

class ServerSolution implements AccountServer {

	static String fileName = "accounts.ser";

	Map<String,Account> accountMap = null;
	/**
	 * Constructor for server solution class
	 */
	public ServerSolution() {
		accountMap = new HashMap<String,Account>();
		File file = new File(fileName);
		ObjectInputStream in = null;
		try {
			if (file.exists()) {
				System.out.println("Reading from file " + fileName + "...");
				in = new ObjectInputStream(new FileInputStream(file));

				Integer sizeI = (Integer) in.readObject();
				int size = sizeI.intValue();
				for (int i=0; i < size; i++) {
					Account acc = (Account) in.readObject();
					if (acc != null)
						accountMap.put(acc.getName(), acc);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Creates a new account
	 * @param type The type of account created: checking or savings
	 * @param name Name on the account
	 * @param balance Balance of the account
	 * @return True is the account was made successfully, false if not
	 * @throws IllegalArgumentException If account was made with an illegal argument
	 */
	private boolean newAccountFactory(String type, String name, float balance)
		throws IllegalArgumentException {
		
		if (accountMap.get(name) != null) return false;
		
		Account acc;
		if ("Checking".equals(type)) {
			acc = new Checking(name, balance);

		} else if ("Savings".equals(type)) {
			acc = new Savings(name, balance);

		} else {
			throw new IllegalArgumentException("Bad account type:" + type);
		}
		try {
			accountMap.put(acc.getName(), acc);
		} catch (Exception exc) {
			return false;
		}
		return true;
	}
	
	/**
	 * Creates new account
	 * @param type Type of account, checking or savings
	 * @param name Name of the account
	 * @param balance Balance of the account
	 * @return Returns true if account was made successfully, false if not
	 */
	public boolean newAccount(String type, String name, float balance) 
		throws IllegalArgumentException {
		
		if (balance < 0.0f) throw new IllegalArgumentException("New account may not be started with a negative balance");
		
		return newAccountFactory(type, name, balance);
	}
	
	/**
	 * Closes the account of name
	 * @param name Name of the account
	 * @return Returns true if account was closed successfully, false if not
	 */
	public boolean closeAccount(String name) {
		Account acc = accountMap.get(name);
		if (acc == null) {
			return false;
		}
		acc.setState(State.CLOSED);
		return true;
	}

	/**
	 * Returns the account of name
	 * @param name Name on the account
	 * @return Returns account of name
	 */
	public Account getAccount(String name) {
		return accountMap.get(name);
	}

	/**
	 * Returns all accounts
	 * @return Returns all accounts
	 */
	public List<Account> getAllAccounts() {
		return new ArrayList<Account>(accountMap.values());
	}

	/**
	 * Returns only active accounts
	 * @return Returns all active accounts
	 */
	public List<Account> getActiveAccounts() {
		List<Account> result = new ArrayList<Account>();

		for (Account acc : accountMap.values()) {
			if (acc.getState() != State.CLOSED) {
				result.add(acc);
			}
		}
		return result;
	}
	
	/**
	 * Saves the account(s)
	 */
	public void saveAccounts() throws IOException {
		ObjectOutputStream out = null; 
		try {
			out = new ObjectOutputStream(new FileOutputStream(fileName));

			out.writeObject(Integer.valueOf(accountMap.size()));
			for (int i=0; i < accountMap.size(); i++) {
				out.writeObject(accountMap.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Could not write file:" + fileName);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

}
