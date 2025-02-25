package com.ltp.banksystem.repository;

import com.ltp.banksystem.dto.dtorequest.AccountCredentials;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTimestamp(LocalDate timestamp);

}
