package com.codependent.isolation;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.codependent.isolation.entity.BankAccount;
import com.codependent.isolation.service.BankAccountService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IsolationLevelsShowcaseApplicationTests {

	@Autowired
	private BankAccountService bankAccountService;
	
	@Test
	public void testUpdateAccountReadUncommited() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		BankAccount ba = bankAccountService.createAccount("Jose", 1000);
		
		Runnable updater = () -> {
			bankAccountService.addMoneyReadUncommited(ba.getId(), 200);
			bankAccountService.getMoney(ba.getId());
			latch.countDown();
		};
		
		Runnable reader = () -> {
			bankAccountService.getReadUncommited(ba.getId());
			latch.countDown();
		};
		
		Thread th1 = new Thread(updater);
		Thread th2 = new Thread(reader);
		
		th1.start();
		th2.start();
		
		latch.await();
	}
	
	@Test
	public void testUpdateAccountReadCommited() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		BankAccount ba = bankAccountService.createAccount("Jose", 1000);
		
		Runnable updater = () -> {
			bankAccountService.addMoneyReadUncommited(ba.getId(), 200);
			bankAccountService.getMoney(ba.getId());
			latch.countDown();
		};
		
		Runnable reader = () -> {
			bankAccountService.getReadCommited(ba.getId());
			latch.countDown();
		};
		
		Thread th1 = new Thread(updater);
		Thread th2 = new Thread(reader);
		
		th1.start();
		th2.start();
		
		latch.await();
	}
	
	@Test
	public void testQueryTwiceReadUncommited() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		BankAccount ba = bankAccountService.createAccount("Jose", 1000);
		bankAccountService.createAccount("Luis", 2000);
		
		Runnable reader = () -> {
			bankAccountService.findAllTwiceReadUncommited();
			latch.countDown();
		};
		
		Runnable updater = () -> {
			bankAccountService.addMoneyReadUncommited(ba.getId(), 200);
			bankAccountService.getMoney(ba.getId());
			latch.countDown();
		};
		
		Thread th1 = new Thread(reader);
		Thread th2 = new Thread(updater);
		
		th1.start();
		th2.start();
		
		latch.await();
	}
	
	@Test
	public void testQueryTwiceReadCommited() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		BankAccount ba = bankAccountService.createAccount("Jose", 1000);
		bankAccountService.createAccount("Luis", 2000);
		
		Runnable reader = () -> {
			bankAccountService.findAllTwiceReadCommited();
			latch.countDown();
		};
		
		Runnable updater = () -> {
			bankAccountService.addMoneyReadUncommited(ba.getId(), 200);
			bankAccountService.createAccount("Carlos", 3000);
			latch.countDown();
		};
		
		Thread th1 = new Thread(reader);
		Thread th2 = new Thread(updater);
		
		th1.start();
		th2.start();
		
		latch.await();
	}
	
	@Test
	public void testQueryTwiceRepeteableRead() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		BankAccount ba = bankAccountService.createAccount("Jose", 1000);
		bankAccountService.createAccount("Luis", 2000);
		
		Runnable reader = () -> {
			bankAccountService.findAllTwiceRepeteableRead();
			latch.countDown();
		};
		
		Runnable updater = () -> {
			bankAccountService.addMoneyReadUncommited(ba.getId(), 200);
			bankAccountService.createAccount("Carlos", 3000);
			latch.countDown();
		};
		
		Thread th1 = new Thread(reader);
		Thread th2 = new Thread(updater);
		
		th1.start();
		th2.start();
		
		latch.await();
	}
	
	@Test
	public void testQueryTwiceSerializable() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		BankAccount ba = bankAccountService.createAccount("Jose", 1000);
		bankAccountService.createAccount("Luis", 2000);
		
		Runnable reader = () -> {
			bankAccountService.findAllTwiceSerializable();
			latch.countDown();
		};
		
		Runnable updater = () -> {
			bankAccountService.addMoneyReadUncommited(ba.getId(), 200);
			bankAccountService.createAccount("Carlos", 3000);
			latch.countDown();
		};
		
		Thread th1 = new Thread(reader);
		Thread th2 = new Thread(updater);
		
		th1.start();
		th2.start();
		
		latch.await();
	}
	

}
