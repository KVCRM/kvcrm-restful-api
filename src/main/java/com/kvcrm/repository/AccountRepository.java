package com.kvcrm.repository;

import java.util.List;

import com.kvcrm.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
    extends PagingAndSortingRepository<Account, Long>, JpaRepository<Account, Long> {

  List<Account> findByName(String name);

  List<Account> findByNameContaining(String name);

}
