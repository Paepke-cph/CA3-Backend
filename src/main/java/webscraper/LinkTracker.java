package webscraper;

import concurrent.ParallelTask;
import concurrent.ParallelWorker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinkTracker implements ParallelTask {
    private static final int MAX_DEPTH = 1;
    private String url;
    private int depth;
    private int count;
    List<LinkTracker> children = new ArrayList<>();

    public LinkTracker(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    @Override
    public void doWork() {
        Document document;
        try{
            document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for(Element element :  links) {
                String absLink = element.attr("abs:href");
                if(!absLink.contains(url) &&
                        !absLink.contains(".jpg") &&
                        (absLink.contains("https://") || absLink.contains("http://"))) {
                    children.add(new LinkTracker(absLink,depth+1));
                }
            }
            count = links.size();
            if(depth < MAX_DEPTH) {
                ParallelWorker<LinkTracker> pw = new ParallelWorker<>();
                pw.getAsParallel(children);
            }
        } catch (IOException e) {
        }
    }

    public String getUrl() {
        return url;
    }

    public List<LinkTracker> getChildren() {
        return children;
    }
}
