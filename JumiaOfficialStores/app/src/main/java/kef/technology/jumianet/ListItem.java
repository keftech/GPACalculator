package kef.technology.jumianet;

public class ListItem {
    private final String title; private final int imageRes;

    public ListItem(String title, int imageRes){
        this.title = title; this.imageRes = imageRes;
    }
    public String getTitle() {
        return title;
    }

    public int getImageRes() {
        return imageRes;
    }
}
