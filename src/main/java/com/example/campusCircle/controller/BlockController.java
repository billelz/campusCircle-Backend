package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Block;
import com.example.campusCircle.service.BlockService;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping
    public Block create(@RequestBody Block block) {
        return blockService.createBlock(block);
    }

    @GetMapping("/{id}")
    public Block getOne(@PathVariable Long id) {
        return blockService.getBlockById(id);
    }

    @GetMapping
    public List<Block> getAll() {
        return blockService.getAllBlocks();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        blockService.deleteBlock(id);
    }
}
