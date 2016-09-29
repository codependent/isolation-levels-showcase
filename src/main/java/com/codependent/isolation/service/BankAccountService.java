package com.codependent.isolation.service;

import com.codependent.isolation.entity.BankAccount;

public interface BankAccountService{

	BankAccount createAccount(String owner, Integer initialAmmount);
	
	BankAccount getReadUncommited(int accountId);
	
	BankAccount getReadCommited(int accountId);
	
	int getMoney(int accountId);
	
	Iterable<BankAccount> findAllTwiceReadUncommited();
	
	Iterable<BankAccount> findAllTwiceReadCommited();
	
	Iterable<BankAccount> findAllTwiceRepeteableRead();
	
	Iterable<BankAccount> findAllTwiceSerializable();
	
	void addMoneyReadUncommited(int accountId, int ammount);
	
	void addMoneyReadCommited(int accountId, int ammount);
	
	void addMoneyRepeteableRead(int accountId, int ammount);
	
	void addMoneySerializable(int accountId, int ammount);
	
}
