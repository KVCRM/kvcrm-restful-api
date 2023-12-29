package com.kvcrm.repository;

import com.kvcrm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository
    extends PagingAndSortingRepository<Contact, Long>, JpaRepository<Contact, Long> {

}
