package com.enviro.assessment.junior.phodzo.nagana.repository;

import com.enviro.assessment.junior.phodzo.nagana.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//This handles all the CRUD functionality by simply using JPARepository
@Repository
public interface InvestorRepository extends JpaRepository<Investor,Long> {
}
