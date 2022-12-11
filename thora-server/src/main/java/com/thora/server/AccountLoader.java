package com.thora.server;

public abstract class AccountLoader {
	
	public static final int MIN_USERNAME_LENGTH = 1;
	public static final int MAX_USERNAME_LENGTH = 16;

	public static final int MIN_PASSWORD_LENGTH = 0;
	public static final int MAX_PASSWORD_LENGTH = 24;

	public SimpleResult isValidUsername(String username) {
		if(username == null) throw new NullPointerException("Username cannot be null.");
		if(username.length() < MIN_USERNAME_LENGTH) {
			return SimpleResult.failSimple("Username has to be at least " + MIN_USERNAME_LENGTH + " characters long.");
		}
		if(username.length() > MAX_USERNAME_LENGTH) {
			return SimpleResult.failSimple("Username cannot be longer than " + MAX_USERNAME_LENGTH + " characters long.");
		}
		for(char c : username.toCharArray()) {
			if(!Character.isLetterOrDigit(c))
				return SimpleResult.failSimple("Username can not contain special characters.");
		}
		return SimpleResult.successSimple("Given username is valid");
	}

	public SimpleResult isValidPassword(String password) {
		if(password == null)
			SimpleResult.failSimple("Password cannot be null.");
		if(password.length() < MIN_PASSWORD_LENGTH)
			SimpleResult.failSimple("Password has to be at least " + MIN_PASSWORD_LENGTH + " characters long.");
		if(password.length() > MAX_PASSWORD_LENGTH)
			SimpleResult.failSimple("Password cannot be longer than " + MAX_PASSWORD_LENGTH + " characters long.");
		return SimpleResult.successSimple("Given password is valid");
	}

	protected SimpleResult isValid(String username, String password) {
		SimpleResult r = isValidUsername(username);
		if(r.isFail()) return r;
		r = isValidPassword(password);
		if(r.isFail()) return r;
		return SimpleResult.successSimple("Both username and password are a valid tuple");
	}

	public Result<Player> getAccount(String username, String password) {
		SimpleResult validPair = isValid(username, password);
		if(validPair.isFail())
			return Result.fail(validPair);

		if(accountExists(username)) {
			return loadAccount(username, password);
		} else {
			return createAccount(username, password);
		}
	}

	public abstract boolean accountExists(String username);

	public abstract Result<Player> loadAccount(String username, String password);

	public abstract Result<Player> createAccount(String username, String password);

	public abstract boolean saveAccount(Player player);

//	protected final Player createPlayer(String username, String password) {
//		return Server.get().createPlayer(username, password);
//	}
	
}
