package com.shkiddi_school.controller;


import com.shkiddi_school.domain.Article;
import com.shkiddi_school.domain.PhotoArticle;
import com.shkiddi_school.domain.Test;
import com.shkiddi_school.handler.HandlerTextHTML;
import com.shkiddi_school.repos.ArticleRepo;
import com.shkiddi_school.repos.PhotoArticleRepo;
import com.shkiddi_school.service.ArticleService;
import com.shkiddi_school.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/article")
@PreAuthorize("hasAuthority('ADMIN')")
public class ArticleController {

    @Autowired
    ArticleService articleService;
    @Autowired
    PhotoArticleRepo paRepo;
    @Autowired
    HandlerTextHTML handlerTextHTML;
    @Autowired
    TestService testService;
    @Autowired
    ArticleRepo articleRepo;


    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String getArticles(Model model) {
        model.addAttribute("articles", articleService.getAllAtricle());
        return "articleList";
    }

    @GetMapping("{article}")
    public String editArticle(@PathVariable Article article, Model model) {
        model.addAttribute("article", article);
        return "articleEdit";
    }

    @PostMapping("/photo/{article}")
    public String addPhoto(
            @PathVariable Article article,
            @RequestParam("file") MultipartFile file,
            Model model) {

        model.addAttribute("article", article);

        if (file != null) {

            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();


            try {

                file.transferTo(new File(uploadDir + "/" + resultFilename));
            } catch (IOException e) {
                e.printStackTrace();
            }

            PhotoArticle photoArticle = new PhotoArticle();
            photoArticle.setName(resultFilename);
            photoArticle.setNumber(article.getPhotoArticles().size() + 1);
            article.getPhotoArticles().add(photoArticle);

            paRepo.save(photoArticle);
            article.addPhoto(photoArticle);
        }

        return "articleEdit";
    }

    @GetMapping("add")
    public String addArticle(Model model) {
        Test test = new Test();

        Article article = new Article();
        article.setTest(test);


        testService.save(test);
        model.addAttribute("article", articleService.saveArticle(article));

        return "articleEdit";
    }

    @GetMapping("delete/{article}")
    public String deleteArticle(@PathVariable Article article, Model model) {
        articleService.deleteArticle(article.getId());
        Iterable<Article> articles = articleRepo.findAll();
        model.addAttribute("articles", articles);
        if (articles.iterator().hasNext()) {
            article = handlerTextHTML.procesArticleText(articles.iterator().next());

        } else {
            article = new Article();
            article.setText("Add Article");
            article.setTitle("Add article");
        }

        model.addAttribute("article",article);

        return "greeting";
    }

    @GetMapping("update/{article}")
    public String updateArticle(@PathVariable Article article, @RequestParam("text") String text, @RequestParam("title") String title, Model model) {
        article.setTitle(title);
        article.setText(text);
        articleService.saveArticle(article);

        model.addAttribute("article", handlerTextHTML.procesArticleText(article));
        model.addAttribute("articles", articleService.getAllAtricle());


        return "greeting";
    }


}
