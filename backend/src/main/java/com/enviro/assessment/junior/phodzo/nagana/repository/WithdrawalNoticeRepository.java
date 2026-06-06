package com.enviro.assessment.junior.phodzo.nagana.repository;

import com.enviro.assessment.junior.phodzo.nagana.model.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice,Long> {
    List<WithdrawalNotice> findByProductId(Long productId);
}
