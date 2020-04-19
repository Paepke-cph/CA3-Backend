package webscraper;

import concurrent.ParallelWorker;
import dtos.LinkDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WebScraper {
    private static Map<String,CacheResult> cachedResults = new HashMap<>();

    public static List<TagCounter> runGeneric() {
        List<TagCounter> urls = new ArrayList();
        urls.add(new TagCounter("https://www.fck.dk"));
        urls.add(new TagCounter("https://www.google.com"));
        urls.add(new TagCounter("https://politiken.dk"));
        urls.add(new TagCounter("https://cphbusiness.dk"));

        ParallelWorker<TagCounter> pw = new ParallelWorker<>();
        return pw.getAsParallel(urls);
    }

    public static List<TagCounter> runParallel() throws InterruptedException {
        List<TagCounter> urls = new ArrayList();
        urls.add(new TagCounter("https://www.fck.dk"));
        urls.add(new TagCounter("https://www.google.com"));
        urls.add(new TagCounter("https://politiken.dk"));
        urls.add(new TagCounter("https://cphbusiness.dk"));

        ExecutorService worker = Executors.newFixedThreadPool(4);
        urls.forEach(tagCounter -> {
            Runnable task = () -> {
              tagCounter.doWork();
            };
            worker.submit(task);
        });
        worker.shutdown();
        worker.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        return urls;
    }

    public static void TestRun() throws InterruptedException {
        long start = System.nanoTime();
        List<TagCounter> list1 = WebScraper.runParallel();
        long half = System.nanoTime();
        List<TagCounter> list2 = WebScraper.runGeneric();
        long end = System.nanoTime();

        long firstRun = TimeUnit.NANOSECONDS.toMillis(half - start);
        long lastRun = TimeUnit.NANOSECONDS.toMillis(end - half);
        System.out.println("Base Parallel: " + firstRun + "ms");
        System.out.println("Generic Parallel: " + lastRun + "ms");
        System.out.println("How come the Generic version is 4x times faster than the regular parallel function?????");
    }

    public static List<LinkDTO> findLinks(String url) {
        if(cachedResults.containsKey(url)) {
            CacheResult found = cachedResults.get(url);
            if (found.isStale()) {
                cachedResults.remove(found);
            } else {
                return found.getResults();
            }
        }
        List<LinkTracker> urls = new ArrayList();
        urls.add(new LinkTracker(url,0));
        ParallelWorker<LinkTracker> pw = new ParallelWorker<>();
        pw.getAsParallel(urls);

        List<LinkDTO> dtos = new ArrayList<>();
        for(LinkTracker tracker : urls) {
            dtos.add(LinkDTO.toLinkDTO(tracker));
        }
        cachedResults.put(url,new CacheResult(url,dtos));
        return dtos;
    }

    public static void main(String[] args) {
        List<LinkDTO> dtos = findLinks("https://www.fck.dk");
        List<LinkDTO> dtos2 = findLinks("https://www.fck.dk");
        System.out.println("Done");
    }
}
