package com.kvcrm.repository;

import com.kvcrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends PagingAndSortingRepository<User, Long>, JpaRepository<User, Long> {

}
