package com.bank.CashWithdraw.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bank.CashWithdraw.Entity.Notes;

@Repository

public interface NotesRepository extends JpaRepository<Notes, Integer> {

	Notes findByDenominations(int denomination);
	
	

}
