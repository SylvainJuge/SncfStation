package sylvain.juge.sncfstation;

public class TrainRecord {
    private String city = null;
    private String number = null;
    private String platform = null;
    private String time = null;
    // TODO : add a "direction" boolean field to indicate if it's an arrival or a departure
    // TODO : create a builder class to enforece invariants ?
    public TrainRecord(String city, String number, String time, String platform){
        this.city = notNull(city);
        this.number = notNull(number);
        this.time = notNull(time);
        this.platform = platform;
    }
    // TODO : translation of "provenance" 
    /** @return train where the trains comes from or goes to, depending on direction */
    public String getCity(){ 
        return city;
    }
    /** @return train number, never null */
    public String getNumber(){ 
        return number;
    }
    /** @return train platform if known, null otherwise */
    public String getPlatform(){ 
        return platform; 
    }
    public String getTime(){
        return time;
    }
    @Override
    public String toString(){
        return "city="+city+" number="+number+" time="+time+" platform="+platform;
    }
    
    // ensure non-nullity class invariants
    private static <T> T notNull(T o){
        if( null == o ){
            throw new RuntimeException("expected not null value");
        }
        return o;
    }
}

