package com.kvcrm.repository;

import com.kvcrm.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository
    extends PagingAndSortingRepository<Organization, Long>, JpaRepository<Organization, Long> {

}
