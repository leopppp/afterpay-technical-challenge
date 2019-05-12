package com.leop.afterpaycoding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

	private String creditCardHash;
	private Date timeStamp;
	private Double amount;

	public String getCreditCardHash() {
		return creditCardHash;
	}

	public void setCreditCardHash(String creditCardHash) {
		this.creditCardHash = creditCardHash;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * Parse the comma separated transaction string into Transaction object.
	 * @param transactionString the comma separated transaction string
	 * @return the {@link Transaction} object
	 * @throws ParseException throws if format is invalid
	 */
	public Transaction parse(String transactionString) throws ParseException {
		String[] elements = transactionString.split(",");

		if (elements.length < 3) {
			throw new IllegalArgumentException("The transaction format is not valid.");
		}

		this.setCreditCardHash(elements[0]);

		Date timeStamp = (Date) new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(elements[1]);
		this.setTimeStamp(timeStamp);

		this.setAmount(Double.parseDouble(elements[2]));

		return this;
	}

}
