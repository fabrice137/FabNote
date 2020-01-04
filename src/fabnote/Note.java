
package fabnote;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Note {
    private int id;
    private String content, lastTime; 
    private Set<String> hashTagSet;
    private static String formatOfDate = "yyyyMMdd_hhmmss";

    public Note() {
        this.hashTagSet = new HashSet<>();
        this.content = "";
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(formatOfDate);
        this.lastTime = dateFormat.format(new java.util.Date());
    }
    
    public Note(int id, String time, String hashtags, String content) {
        this.hashTagSet = new HashSet<>();
        this.id = id;
        this.content = content; 
        this.lastTime = time;
        addHashTags(hashtags);
    }
    
    public void setContent(String newContent){
        this.content = newContent;
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(formatOfDate);
        this.lastTime = dateFormat.format(new Date());
    }
    public String getContent(){ return this.content;  }
    
    public int getId(){ return this.id;  }
    public void setId(int id){ this.id = id;  }
    
    public String getPreview(){
        int sz = (this.content.length() > 30) ? 30 : this.content.length();
        return  this.content.substring(0, sz) + "\n---------------\n" + this.lastTime;
    }
    
    public void setNewTags(String hashtags){
        this.hashTagSet.clear();
        addHashTags(hashtags);
    }
    
    public int tagsMatched(String[] anArrayOfTags){
        int toReturn = 0;
        for(String temp: anArrayOfTags){
            if(this.hashTagSet.contains(temp)) toReturn++;
        } return toReturn;
    }
    public String getLastTime(){ return this.lastTime;
    }
    public String getHashTags(){
        StringBuilder sb = new StringBuilder(" # ");
        java.util.Iterator<String> iter = hashTagSet.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next()).append(" ");
        } return sb.toString();
    }
    
    private void addHashTags(String text){
        StringBuilder sb = new StringBuilder();
        for(int i=0 ; i<text.length() ; i++){
            if (Character.isLetterOrDigit(text.charAt(i))) {
                sb.append(text.charAt(i));
            } else{
                if(sb.toString().isEmpty()) continue;
                this.hashTagSet.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        if(!sb.toString().isEmpty()) 
            this.hashTagSet.add(sb.toString());
    }
    
}
