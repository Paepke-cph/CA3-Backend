package dtos;

import webscraper.LinkTracker;

import java.util.ArrayList;
import java.util.List;

public class LinkDTO {
    public String url;
    public List<LinkDTO> children = new ArrayList<>();
    public int childrenCount;



    public static LinkDTO toLinkDTO(LinkTracker linkTracker) {
        LinkDTO result = new LinkDTO();
        result.url = linkTracker.getUrl();
        List<LinkTracker> trackers = linkTracker.getChildren();
        result.childrenCount = trackers.size();
        for(LinkTracker tra : trackers) {
            result.children.add(toLinkDTO(tra));
        }
        return result;
    }
}
