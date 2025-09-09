package com.haradakatsuya190511.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {}
