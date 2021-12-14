package com.nico.store.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.logging.log4j.LogManager; //log4j import
import org.apache.logging.log4j.Logger; //log4j import

import com.nico.store.store.domain.Article;
import com.nico.store.store.service.ArticleService;

@Controller
public class HomeController {

	private static final Logger logger = LogManager.getLogger("HomeController"); // Logger instance
		
	@Autowired
	private ArticleService articleService;
	
	//I've added User-agent header request, will be used to exploit log4j
	@RequestMapping("/")
	public String index(@RequestHeader("User-Agent") String useragent, Model model) {

		List<Article> articles = articleService.findFirstArticles();
		model.addAttribute("articles", articles);
		logger.info(useragent); //here I will log User-agent value, the string will be a special crafted string with a payload
		return "index";
	}
}
