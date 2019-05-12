package com.leop.afterpaycoding;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A credit card will be identified as fraudulent if the sum of amounts for a
 * unique hashed credit card number over a 24 hour sliding window period exceeds
 * the price threshold.
 * 
 * This class provides the functions to identify the credit cards which break
 * the rules.
 * 
 * @author leo
 *
 */
public class AntiFraud {

	private Long MILLISECONDS_OF_24HOURS = 24 * 60 * 60 * 1000L;

	public static void main(String[] args) {

	}

	/**
	 * Identify the fraud credit cards 
	 * @param transactions the transactions to be processed
	 * @param priceThreshold the price threshold
	 * @return the identified credit card list
	 * @throws ParseException throws if the transaction format is not valid
	 */
	public List<String> identifyFraudCreditCards(String transactions, Double priceThreshold) throws ParseException {
		
		if(priceThreshold <= 0) {
			throw new IllegalArgumentException("The price threshold should be greater than zero.");
		}
		
		String[] transactionList = transactions.split(";");
		List<String> fraudCreditCards = new LinkedList<>();

		Map<String, CreditCard> notProcessedCreditCards = new HashMap<String, CreditCard>();
		parseTransactions(transactionList, fraudCreditCards, notProcessedCreditCards, priceThreshold);
		
		for(String cardHash : notProcessedCreditCards.keySet()) {
			if(isCreditCardFraud(notProcessedCreditCards.get(cardHash), priceThreshold)) {
				fraudCreditCards.add(cardHash);
			}
		}
		
		return fraudCreditCards;
	}

	private void parseTransactions(String[] transactionList, List<String> fraudCreditCards, Map<String, CreditCard>  notProcessedCreditCards,
			Double priceThreshold) throws ParseException {
		
		for (String transactionString : transactionList) {
			Transaction transaction = new Transaction();
			
			transaction = transaction.parse(transactionString);

			if (!fraudCreditCards.contains(transaction.getCreditCardHash())) {
				// Process only if the transactions are not in the fraud list.
				if (transaction.getAmount() > priceThreshold) {
					// Add the credit to fraud list if the single transaction is over price
					// threshold.
					fraudCreditCards.add(transaction.getCreditCardHash());
					// Remove the credit card from not processed cards 
					notProcessedCreditCards.remove(transaction.getCreditCardHash());
				} else {
					// Otherwise, put the transactions into the list and process it later.
					if (notProcessedCreditCards.containsKey(transaction.getCreditCardHash())) {
						notProcessedCreditCards.get(transaction.getCreditCardHash()).getTransactions().add(transaction);
					} else {
						CreditCard creditCard = new CreditCard();
						creditCard.setCreditCardHash(transaction.getCreditCardHash());
						creditCard.getTransactions().add(transaction);
						notProcessedCreditCards.put(creditCard.getCreditCardHash(), creditCard);
					}
				}
			}
		}
	}

	private boolean isCreditCardFraud(CreditCard creditCard, Double priceThreshold) {
		int transactionCount = creditCard.getTransactions().size();

		if (transactionCount == 1) {
			// Should not be fraud if it only has one transaction as the not processed transactions are less than the price threshold.
			return false;
		}

		// Work it out in the time stamp descending order.
		for (int i = transactionCount - 1; i >= 0; i--) {
			Double totalIn24Hour = 0.0;
			totalIn24Hour += creditCard.getTransactions().get(i).getAmount();
			for (int j = i - 1; j >= 0; j--) {
				double difference = creditCard.getTransactions().get(i).getTimeStamp().getTime()
						- creditCard.getTransactions().get(j).getTimeStamp().getTime();
				if (difference < MILLISECONDS_OF_24HOURS) {
					totalIn24Hour += creditCard.getTransactions().get(j).getAmount();
					if (totalIn24Hour > priceThreshold) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
