package com.tribmle.repository;


import com.tribmle.entity.LeaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseHistoryRepository extends JpaRepository<LeaseHistory, Long> {
    List<LeaseHistory> findByCustomerId(Long customerId);
}
