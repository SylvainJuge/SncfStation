package sylvain.juge.sncfstation;

import java.util.List;
import java.util.ArrayList;

public class AsciiTable {
    // characters used to decorate ascii table
    private final char columnSeparator;
    private final char rowSeparator;
    private final char corner;

    private final List<List<String>> rows;
    private final List<Integer> columnWidths;

    // TODO : enhance this class and create a builder to make it immutable ??
    
    public AsciiTable(char columnSeparator, char rowSeparator, char corner){
        this.columnSeparator = columnSeparator;
        this.rowSeparator = rowSeparator;
        this.corner = corner;
        this.rows = new ArrayList<List<String>>();
        this.columnWidths = new ArrayList<Integer>();
    }

    /** @return ascii table with default separators */
    public static AsciiTable newDefault(){
        return new AsciiTable('|','-','o');
    }

    // TODO : separator rows must be implemented using a reference to a static 
    // value in order to avoid re-formatting them when a column is resized
    public void addSeparatorRow(){
    }

    public void addRow(List<String> row){
        // we have to copy values when thy are added because
        // lists are mutable by nature
        List<String> newRow = new ArrayList<String>(row);
        rows.add(newRow);
        while( columnWidths.size() < newRow.size()){
            columnWidths.add(0);
        }
        for(int i=0;i<newRow.size();i++){
            int length = newRow.get(i).length();
            if( columnWidths.get(i) < length ){
                columnWidths.set(i,length);
            }
        }
   }

    public int getWidth(){
        return 0;
    }

    public int getHeight(){
        return 0;
    }

    public List<String> getRows(){
        // TODO : make a safe copy before returning values
        for(List<String> row:rows){
        }
        return null;
    }

    @Override
    public String toString(){
        return null;
    }
}

