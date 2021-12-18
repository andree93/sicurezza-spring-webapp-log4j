package com.nico.store.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nico.store.store.domain.Article;
import com.nico.store.store.service.ArticleService;

@Controller
public class HomeController {

	private static final Logger logger = LogManager.getLogger("HomeController");
		
	@Autowired
	private ArticleService articleService;
	
	//al controller ho aggiunto la richiesta della user agent
	@RequestMapping("/")
	public String index(@RequestHeader("User-Agent") String useragent, Model model) {

		List<Article> articles = articleService.findFirstArticles();
		model.addAttribute("articles", articles);
		logger.info(useragent); //loggo il valore
		return "index";
	}
}
