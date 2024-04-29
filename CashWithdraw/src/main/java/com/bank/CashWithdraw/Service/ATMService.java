package com.bank.CashWithdraw.Service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ATMService {
	
	public String withDrawCash(int amount);


	public void addDenominations(Map<Integer, Integer> cashAvailable);
	
	

}
