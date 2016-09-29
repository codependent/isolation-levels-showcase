package com.codependent.isolation.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.isolation.entity.BankAccount;

public interface BankAccountDao extends JpaRepository<BankAccount, Integer>{

}
