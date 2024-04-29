package com.bank.CashWithdraw.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.CashWithdraw.Service.ATMService;

@RestController
@RequestMapping("/cash")
public class NotesController {
	
	
	    @Autowired
	    private ATMService atmService;
	    
	    @Autowired
	    public NotesController(ATMService atmService) {
	        this.atmService = atmService;
	    }
	    
	    @PostMapping("/add-denominations")
	    public ResponseEntity<String> addDenominations(@RequestBody Map<Integer, Integer> cashAvailable) {
	    	atmService.addDenominations(cashAvailable);
	        return ResponseEntity.status(HttpStatus.CREATED).body("Denominations added successfully.");
	    }

	    
	    @PostMapping("/withdraw/{amount}")
	    public String withdrawCash(@PathVariable int amount) {
	        return atmService.withDrawCash(amount);
	        
	    }
	    
	}


