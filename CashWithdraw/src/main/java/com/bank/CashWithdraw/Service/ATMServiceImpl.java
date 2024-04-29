package com.bank.CashWithdraw.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bank.CashWithdraw.Entity.Notes;
import com.bank.CashWithdraw.Exception.InsufficientFundsException;
import com.bank.CashWithdraw.Exception.InvalidWithdrawalAmountException;

import com.bank.CashWithdraw.Repository.NotesRepository;

@Service
public class ATMServiceImpl implements ATMService {

	@Autowired
	private NotesRepository noteRepo;

	private final Object lock = new Object();

	@Autowired
	public ATMServiceImpl(NotesRepository noteRepo) {
		this.noteRepo = noteRepo;
	}

	public void addDenominations(Map<Integer, Integer> cashAvailable) {
		for (Map.Entry<Integer, Integer> entry : cashAvailable.entrySet()) {
			Notes note = new Notes();
			note.setDenominations(entry.getKey());
			note.setQuantity(entry.getValue());
			noteRepo.save(note);
		}
	}

	@Override
	public String withDrawCash(int amount) {
		if (amount % 10 != 0) {
			throw new InvalidWithdrawalAmountException("Invalid withdrawal amount. Please enter amount in multiples of 10 and try again.");
		}
		
		synchronized(lock) {
					
			Map<Integer, Integer> cashAvailable = getCashAvailable();
				
			int remainingAmount = amount;
			Map<Integer, Integer> updatedInventory = new HashMap<>(cashAvailable);
			
			List<Integer> denominations = new ArrayList<>(cashAvailable.keySet());
		    Collections.sort(denominations, Collections.reverseOrder());
		    
			for (int denomination : denominations) {
				int requiredNotes = remainingAmount / denomination;
				int availableNotes = cashAvailable.getOrDefault(denomination, 0);
				int notesToDispense = Math.min(requiredNotes, availableNotes);
				remainingAmount -= notesToDispense * denomination;
				updatedInventory.put(denomination, availableNotes - notesToDispense);
				
			}
	
			if (remainingAmount == 0) {
				updateCashAvailable(updatedInventory);
				cashAvailable.clear();
				cashAvailable.putAll(updatedInventory);
				return "Cash withdrawal successful.";
			} else {
				// Revert back changes to cash inventory if withdrawal failed
				cashAvailable.clear();
				cashAvailable.putAll(updatedInventory);
				throw new InsufficientFundsException( "Insufficient funds. Cash withdrawal failed.");
			}
		}
	}
	
	public Map<Integer, Integer> getCashAvailable() {

		return noteRepo.findAll().stream().collect(Collectors.toMap(Notes::getDenominations, Notes::getQuantity));
	}

        
    
	public void updateCashAvailable(Map<Integer, Integer> updatedInventory) {
        for (Map.Entry<Integer, Integer> entry : updatedInventory.entrySet()) {
         int denomination = entry.getKey();
         int quantity = entry.getValue();
         Notes note = noteRepo.findByDenominations(denomination);
         note.setQuantity(quantity);
         noteRepo.save(note);
     }
}
}
