# my-web-crawler
My web crawler.

## Exercise Description
Here are the instructions for the Buildit Platform Engineer exercise:

What you need to share with us:
- Working crawler as per requirements above
- A README.md explaining
- How to build and run your solution
- Reasoning and describe any trade offs
- Explanation of what could be done with more time
- Project builds / runs / tests as per instruction

# My solution

## Process planning
1. Get user input
2. Start crawler
3. Set user input as top level page
4. Maybe read robots.txt
5. Maybe apply robots.txt rules
6. Skip if page has been visited already
7. Download html file
8. Parse html file
9. Extract title from file
10. Extract links from file
11. Maybe consider spawning threads to process multiple pages at the same time
12. Create page object with title, url and list of links.
13. Add page to a map
14. Add links a to toVisit list, filtering out links from other websites.
15. save to file (consider that links written before, as skipped to avoid circular references)
16. maybe format the file

 
### How to build
Dependencies: 
- [gradle](https://gradle.org/)
- [java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

With the dependencies installed, run the command below to clean build and 
execute tests:

```
gradle clean build test
```

### How to run

To run the web crawler, use the command below:

```
java -jar <jarfile> <website> <sitemap>
```

Where:
- `jarfile` is the full path to the jar file
- `website` is the domain name to crawl
- `sitemap` is the file to save

### Improvements

- Make the crawler multi-threaded
- Allows for distribution
- Generate a pretty site map  
- Add more functional tests
- Add wiremock to for an end to end test, with a mocked
- Add proper logging 