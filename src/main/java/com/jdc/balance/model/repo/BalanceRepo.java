package com.jdc.balance.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.jdc.balance.model.domain.entity.Balance;

@Repository
public interface BalanceRepo extends JpaRepository<Balance,Integer>,JpaSpecificationExecutor<Balance>{

}
