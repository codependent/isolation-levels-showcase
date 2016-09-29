package com.codependent.isolation.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.codependent.isolation.entity.BankAccount;

@Repository("bankAccountDaoJdbcTemplate")
public class BankAccountDao implements CrudRepository<BankAccount, Integer>{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public <S extends BankAccount> S save(S entity) {
		if(entity.getId()!=null){
			jdbcTemplate.update("update bank_account set owner = ?, money = ? where id = ?", 
					entity.getOwner(), entity.getMoney(), entity.getId());
			return entity;
		}else{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(
			    new PreparedStatementCreator() {
			        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			            PreparedStatement ps = connection.prepareStatement("insert into bank_account (owner, money) values (?, ?)", 
			            	new String[] {"owner", "money"});
			            ps.setString(1, entity.getOwner());
			            ps.setInt(2, entity.getMoney());
			            return ps;
			        }
			    },
			    keyHolder);
			entity.setId(keyHolder.getKey().intValue());
			return entity;
		}
	}

	@Override
	public <S extends BankAccount> Iterable<S> save(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BankAccount findOne(Integer id) {
		List<BankAccount> query = jdbcTemplate.query("select id, owner, money from bank_account a where a.id = ?", new Object[]{id},  (rs, rowNum) -> {
			BankAccount ba = new BankAccount();
			ba.setId(rs.getInt(1));
			ba.setOwner(rs.getString(2));
			ba.setMoney(rs.getInt(3));
			return ba;
		});
		if(query.isEmpty()){
			return null;
		}else{
			return query.get(0);
		}
			
	}

	@Override
	public boolean exists(Integer id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<BankAccount> findAll() {
		int random = (int)Math.round(Math.random()*10);
		return jdbcTemplate.query("select id, owner, money from bank_account where "+random+"="+random, new Object[]{}, (rs, rowNum) -> {
				BankAccount ba = new BankAccount();
				ba.setId(rs.getInt(1));
				ba.setOwner(rs.getString(2));
				ba.setMoney(rs.getInt(3));
				return ba;
			});
	}

	@Override
	public Iterable<BankAccount> findAll(Iterable<Integer> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Integer id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(BankAccount entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Iterable<? extends BankAccount> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}
	
}
