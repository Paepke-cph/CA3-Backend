package webscraper;

import dtos.LinkDTO;

import javax.ejb.Local;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class CacheResult {
    private static final int MAX_DAYS = 1;
    private String url;
    private List<LinkDTO> results;
    private LocalDate cached;
    public CacheResult(String url, List<LinkDTO> results) {
        this.url = url;
        this.results = results;
        cached = LocalDate.now();
    }

    public boolean isStale() {
        LocalDate now = LocalDate.now();
        long noOfDaysBetween = DAYS.between(cached, now);
        return (noOfDaysBetween > MAX_DAYS);
    }

    public String getUrl() {
        return url;
    }

    public List<LinkDTO> getResults() {
        return results;
    }

    public LocalDate getCached() {
        return cached;
    }
}
