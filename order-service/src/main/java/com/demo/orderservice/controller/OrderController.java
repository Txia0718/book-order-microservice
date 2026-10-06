package com.demo.orderservice.controller;

import com.demo.common.entity.Book;
import com.demo.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public String createOrder(@RequestParam Long bookId, @RequestParam Integer quantity){
        return orderService.createOrder(bookId,quantity);
    }

    @GetMapping("/hello")
    public String isAlive() {
        return orderService.isAlive();
    }

    @GetMapping("/book/{id}")
    public Book searchBook(@PathVariable Long id) {
        return orderService.searchBook(id);
    }
}
