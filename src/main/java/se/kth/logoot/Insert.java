package se.kth.logoot;

/**
 * Created by sindrikaldal on 16/05/17.
 */
public class Insert implements Operation {

    private LineIdentifier id;
    private String content;

    public Insert(LineIdentifier id, String content) {
        this.id = id;
        this.content = content;
    }

    public LineIdentifier getId() {
        return id;
    }

    public void setId(LineIdentifier id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
