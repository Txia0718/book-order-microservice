package com.demo.bookstock.controller;

import com.demo.bookstock.service.BookService;
import com.demo.bookstock.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/hello")
    public String hello() {
        return "项目跑通了！";
    }

    @GetMapping("/book/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @PostMapping("/order/buy")
    public String buyBook(@RequestParam Long bookId, @RequestParam Integer quantity) {
        return bookService.buyBook(bookId, quantity);
    }
}
