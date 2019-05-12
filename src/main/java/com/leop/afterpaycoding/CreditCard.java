package com.leop.afterpaycoding;

import java.util.LinkedList;
import java.util.List;

public class CreditCard {

	private List<Transaction> transactions = new LinkedList<Transaction>();
	private String creditCardHash;

	public String getCreditCardHash() {
		return creditCardHash;
	}

	public void setCreditCardHash(String creditCardHash) {
		this.creditCardHash = creditCardHash;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * Check if this credit card transactions has been over the 24 hours period price threshold.
	 * @return
	 */
	public boolean isOverThreshold() {
		
		return true;
	}
	
}
