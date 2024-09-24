package com.ddong_kka.hagojabi.User.Repository;

import com.ddong_kka.hagojabi.User.Model.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity , Long> {

    Boolean existsByRefresh(String refresh);


    @Transactional
    void deleteByRefresh(String refresh);
}
