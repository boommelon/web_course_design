package bean;

import java.util.Date;

 



public class Announcement {
    private int id;
    private String title;    
    private String content;  
    private int isTop;       
    private Date createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getIsTop() { return isTop; }
    public void setIsTop(int isTop) { this.isTop = isTop; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
