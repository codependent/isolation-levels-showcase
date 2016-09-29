package com.codependent.isolation.service;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.HibernateException;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.codependent.isolation.entity.BankAccount;

@Service
public class BankAccountServiceImpl implements BankAccountService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("bankAccountDaoJdbcTemplate")
	private CrudRepository<BankAccount, Integer> baDaoJdbcTemplate;
	
	@Autowired
	@Qualifier("bankAccountDao")
	private CrudRepository<BankAccount, Integer> baDao;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Transactional
	public BankAccount createAccount(String owner, Integer initialAmmount){
		printIsolationLevel();
		BankAccount ba = new BankAccount();
		ba.setOwner(owner);
		ba.setMoney(initialAmmount);
		ba = baDao.save(ba);
		return ba;
	}
	
	@Transactional(readOnly=true, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public BankAccount getReadUncommited(int accountId){
		printIsolationLevel();
		BankAccount account = baDao.findOne(accountId);
		logger.info("get({}) -> {}",accountId, account);
		return account;
	}
	
	@Transactional(readOnly=true, isolation=Isolation.READ_COMMITTED)
	@Override
	public BankAccount getReadCommited(int accountId){
		printIsolationLevel();
		BankAccount account = baDao.findOne(accountId);
		logger.info("get({}) -> {}",accountId, account);
		return account;
	}
	
	@Transactional(readOnly=true, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public int getMoney(int accountId){
		printIsolationLevel();
		BankAccount ba = baDao.findOne(accountId);
		return ba.getMoney();
	}
	
	@Transactional(readOnly=true, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public Iterable<BankAccount> findAllTwiceReadUncommited(){
		printIsolationLevel();
		Iterable<BankAccount> accounts = baDao.findAll();
		logger.info("findAllTwiceReadUncommited() 1 -> {}", accounts);
		clearEntityManager();
		accounts = baDao.findAll();
		logger.info("findAllTwiceReadUncommited() 2 -> {}", accounts);
		return accounts;
	}
	
	@Transactional(readOnly=true, isolation=Isolation.READ_COMMITTED)
	@Override
	public Iterable<BankAccount> findAllTwiceReadCommited(){
		printIsolationLevel();
		Iterable<BankAccount> accounts = baDao.findAll();
		logger.info("findAllTwiceReadUncommited() 1 -> {}", accounts);
		clearEntityManager();
		accounts = baDao.findAll();
		logger.info("findAllTwiceReadUncommited() 2 -> {}", accounts);
		return accounts;
	}
	
	@Transactional(readOnly=true, isolation=Isolation.REPEATABLE_READ)
	@Override
	public Iterable<BankAccount> findAllTwiceRepeteableRead(){
		printIsolationLevel();
		Iterable<BankAccount> accounts = baDao.findAll();
		logger.info("findAllTwiceRepeteableRead() 1 -> {}", accounts);
		clearEntityManager();
		accounts = baDao.findAll();
		logger.info("findAllTwiceRepeteableRead() 2 -> {}", accounts);
		return accounts;
	}
	
	@Transactional(readOnly=true, isolation=Isolation.SERIALIZABLE)
	@Override
	public Iterable<BankAccount> findAllTwiceSerializable(){
		printIsolationLevel();
		Iterable<BankAccount> accounts = baDao.findAll();
		logger.info("findAllTwiceSerializable() 1 -> {}", accounts);
		clearEntityManager();
		accounts = baDao.findAll();
		logger.info("findAllTwiceSerializable() 2 -> {}", accounts);
		return accounts;
	}
	
	@Transactional(isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public void addMoneyReadUncommited(int accountId, int ammount) {
		printIsolationLevel();
		BankAccount ba = baDao.findOne(accountId);
		ba.setMoney(ba.getMoney()+ammount);
		baDao.save(ba);
		flushEntityManager();
		logger.info("Money added");
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED)
	@Override
	public void addMoneyReadCommited(int accountId, int ammount){
		printIsolationLevel();
		BankAccount ba = baDao.findOne(accountId);
		ba.setMoney(ba.getMoney()+ammount);
	}

	@Transactional(isolation=Isolation.REPEATABLE_READ)
	@Override
	public void addMoneyRepeteableRead(int accountId, int ammount) {
		printIsolationLevel();
		BankAccount ba = baDao.findOne(accountId);
		ba.setMoney(ba.getMoney()+ammount);
	}

	@Transactional(isolation=Isolation.SERIALIZABLE)
	@Override
	public void addMoneySerializable(int accountId, int ammount) {
		printIsolationLevel();
		BankAccount ba = baDao.findOne(accountId);
		ba.setMoney(ba.getMoney()+ammount);
	}
	
	private void printIsolationLevel(){
		EntityManagerHolder holder = (EntityManagerHolder)TransactionSynchronizationManager.getResource(entityManagerFactory);
		EntityManager em = holder.getEntityManager();
		SessionImpl session = (SessionImpl)em.getDelegate();
		try {
			logger.info("ISOLATION LEVEL: {}", session.connection().getTransactionIsolation());
		} catch (HibernateException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void flushEntityManager(){
		EntityManagerHolder holder = (EntityManagerHolder)TransactionSynchronizationManager.getResource(entityManagerFactory);
		EntityManager em = holder.getEntityManager();
		em.flush();
	}
	
	private void clearEntityManager(){
		EntityManagerHolder holder = (EntityManagerHolder)TransactionSynchronizationManager.getResource(entityManagerFactory);
		EntityManager em = holder.getEntityManager();
		em.clear();
	}
	
}
