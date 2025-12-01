package com.example.campusCircle.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.campusCircle.model.Block;

public interface BlockRepository extends JpaRepository<Block, Long> { }
