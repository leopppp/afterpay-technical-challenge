package com.leop.afterpaycoding;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for AntiFraud.
 */
public class AntiFraudTest {
	private AntiFraud antiFraud;
	
	@Before
	public void setup() {
		antiFraud = new AntiFraud();
	}
	
	@Test
	public void noFraud() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.47;" 
				+ "20d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:16:54, 200.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:17:54, 30.99;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:18:54, 30.00";
		
		List<String> actual = this.antiFraud.identifyFraudCreditCards(transactions, 1000.00);
		
		assertTrue(actual.size() == 0);
	}

	@Test
	public void canDetectSingleFraud() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00;" 
				+ "20d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:16:54, 20.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:17:54, 30.00;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:18:54, 30.00";
		String expected = "10d7ce2f43e35fa57d1bbf8b1e2";

		List<String> actual = this.antiFraud.identifyFraudCreditCards(transactions, 35.0);
		
		assertTrue(actual.size() == 1);
		assertTrue(actual.contains(expected));
	}
	
	@Test
	public void canDetectMultiFraud() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:16:54, 20.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:17:54, 30.35;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:17:54, 30.54;"
				+ "20d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T14:17:54, 30.50;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T15:17:54, 230.80;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:17:54, 0.15";
		List<String> expectedList = new LinkedList<String>();
		expectedList.add("10d7ce2f43e35fa57d1bbf8b1e2");
		expectedList.add("30d7ce2f43e35fa57d1bbf8b1e2");
		
		List<String> actualList = this.antiFraud.identifyFraudCreditCards(transactions, 50.30);
		
		assertTrue(actualList.size() == 2);
		assertTrue(isEqual(expectedList, actualList));
	}
	
	@Test
	public void canDetectMultiFraudInMultiDays() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T10:16:54, 10.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T10:17:54, 30.31;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:17:54, 30.54;"
				+ "20d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T14:17:54, 30.50;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T15:17:54, 230.80;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T15:17:56, 0.15";
		List<String> expectedList = new LinkedList<String>();
		expectedList.add("10d7ce2f43e35fa57d1bbf8b1e2");
		expectedList.add("30d7ce2f43e35fa57d1bbf8b1e2");
		
		List<String> actualList = this.antiFraud.identifyFraudCreditCards(transactions, 50.30);
		
		assertTrue(actualList.size() == 2);
		assertTrue(isEqual(expectedList, actualList));
	}
	
	@Test
	public void notFraudIfAcrossDays() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:16:54, 10.00;" 
				+ "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:17:54, 30.31;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:17:54, 30.54;"
				+ "20d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T14:17:54, 30.50;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-05-01T15:17:54, 30.80;"
				+ "30d7ce2f43e35fa57d1bbf8b1e2, 2014-05-01T15:17:56, 0.15";
		
		List<String> actualList = this.antiFraud.identifyFraudCreditCards(transactions, 45.30);
		
		assertTrue(actualList.size() == 0);
	}
	
	@Test(expected = ParseException.class)
	public void invalidTimeFormat() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04/29T13/15:54, 10.00";
		this.antiFraud.identifyFraudCreditCards(transactions, 100.00);
	}
	
	@Test(expected = NumberFormatException.class)
	public void invalidTranctionAmount() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:17:54, xx";
		this.antiFraud.identifyFraudCreditCards(transactions, 100.00);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void priceThresholdIsNotPositive() throws ParseException {
		String transactions = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:17:54, 10.00";
		this.antiFraud.identifyFraudCreditCards(transactions, -100.00);
	}
	
	private boolean isEqual(List<String> expectedList, List<String> actualList) {
		if(expectedList.size() != actualList.size()) {
			return false;
		}
		
		for(String hash : expectedList) {
			if(!actualList.contains(hash)) {
				return false;
			}
		}
		
		return true;
	}
}
