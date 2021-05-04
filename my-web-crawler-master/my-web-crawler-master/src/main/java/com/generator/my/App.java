package com.generator.my;

import com.generator.my.webcrawler.Crawler;
import com.generator.my.webcrawler.pojo.UserInput;
import com.generator.my.webcrawler.WebCrawler;
import com.google.common.base.Stopwatch;
import com.generator.my.webcrawler.input.InputParser;

public class App {

    public static void main(String args[]){
        Stopwatch stopwatch = new Stopwatch().start();
        UserInput input = InputParser.parse(args);

        Crawler crawler = new WebCrawler(input);
        crawler.crawl();

        stopwatch.stop();
        System.out.println(String.format("Crawled %s in %d  milliseconds", input.getDomainToCrawl(), stopwatch.elapsedMillis()));
    }
}
