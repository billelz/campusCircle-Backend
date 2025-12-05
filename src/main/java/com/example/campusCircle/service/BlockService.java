package com.example.campusCircle.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Block;
import com.example.campusCircle.repository.BlockRepository;

@Service
public class BlockService {

    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public Block createBlock(Block block) {
        return blockRepository.save(block);
    }

    public Block getBlockById(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Block not found"));
    }

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public void deleteBlock(Long id) {
        blockRepository.deleteById(id);
    }
}
