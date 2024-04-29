package com.bank.CashWithdraw.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bank.CashWithdraw.Entity.Notes;
import com.bank.CashWithdraw.Exception.InsufficientFundsException;
import com.bank.CashWithdraw.Exception.InvalidWithdrawalAmountException;
import com.bank.CashWithdraw.Repository.NotesRepository;

@SpringBootTest

public class ATMServiceImplTest {
	
	@InjectMocks
	private ATMServiceImpl atmServiceImpl;
	
   @MockBean
    private NotesRepository noteRepo;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testWithdrawCashSuccessful() {
    	
    	int amount=100;
    	
    	Map<Integer, Integer> initialInventory = new HashMap<>();
        initialInventory.put(100, 5); // 5 notes of denomination 100
        initialInventory.put(200, 3); // 3 notes of denomination 200
        initialInventory.put(500, 2); // 2 notes of denomination 500
    	
    	
        Map<Integer, Integer> updatedInventory = new HashMap<>();
        updatedInventory.put(100, 20); // denomination 100 has 20 notes
        updatedInventory.put(200, 15); // denomination 200 has 15 notes

        // Mock behavior of findByDenominations
        when(noteRepo.findByDenominations(anyInt())).thenAnswer(invocation -> {
            int denomination = invocation.getArgument(0);
            Notes note = new Notes();
            note.setDenominations(denomination);
            // Assuming there are some existing notes in the database with the given denomination
            note.setQuantity(10);
            return note;
        });
        atmServiceImpl.updateCashAvailable(updatedInventory);
        
        when(noteRepo.findAll()).thenReturn(
    	        initialInventory.entrySet().stream().map(entry -> {
    	            Notes note = new Notes();
    	            note.setDenominations(entry.getKey());
    	            note.setQuantity(entry.getValue());
    	            return note;
    	        }).collect(Collectors.toList())
    	    );               

         String result = atmServiceImpl.withDrawCash(amount);
         
         assertEquals("Cash withdrawal successful.", result);
         }
    

    @Test
    public void testWithdrawCashInsufficientFunds() {
        
        int amount = 200;
        Map<Integer, Integer> initialInventory = new HashMap<>();
        initialInventory.put(100, 1); // Assuming there is only 1 note of 100
        
        assertThrows(InsufficientFundsException.class, () -> {
        	atmServiceImpl.withDrawCash(amount);
        });
    }
   
	@Test
	public void withdrawCashInvalidAmountTest() {
		
		int amount=95;
		assertThrows(InvalidWithdrawalAmountException.class, ()->{
			atmServiceImpl.withDrawCash(amount);
		});
		
	}
		
	}


