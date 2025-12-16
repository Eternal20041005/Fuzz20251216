package com.fuzz.repository;

import com.fuzz.entity.ParamComboWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 👇 关键修改：Integer → Long（匹配实体类的id类型）
public interface ParamComboWeightRepository extends JpaRepository<ParamComboWeight, Long> {
}