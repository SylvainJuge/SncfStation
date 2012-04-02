package sylvain.juge.sncfstation;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class AsciiTable {

    /** character used for column separation */
    private final char columnSeparator;

    /** character used for row separation, and horizontal separtors */
    private final char rowSeparator;

    /** character used for corner and junctions */
    private final char corner;

    /** list of data rows */
    private final List<List<String>> rows;

    /** column widths, holds maximum column width for each data columns */
    private final List<Integer> columnWidths;

    /** reference used as marker for separator rows */
    private final static List<String> SEPARATOR = new ArrayList<String>();

    // TODO : enhance this class and create a builder to make it immutable ??
    
    public AsciiTable(char columnSeparator, char rowSeparator, char corner){
        this.columnSeparator = columnSeparator;
        this.rowSeparator = rowSeparator;
        this.corner = corner;
        this.rows = new ArrayList<List<String>>();
        this.columnWidths = new ArrayList<Integer>();
    }

    /**
     * @return ascii table with default separators 
     */
    public static AsciiTable newDefault(){
        return new AsciiTable('|','-','o');
    }

    /** 
     * Adds a separator row to table 
     */
    public void addSeparator(){
        rows.add(SEPARATOR);
    }

    /** 
     * Adds a data row to table
     * @param row : row to add
     */
    public void addRow(List<String> row){
        // we have to copy values when thy are added because
        // lists are mutable by nature
        List<String> newRow = new ArrayList<String>(row);
        rows.add(newRow);
        while( columnWidths.size() < newRow.size() ){
            columnWidths.add(0);
        }
        for(int i=0;i<newRow.size();i++){
            int length = newRow.get(i).length();
            if( columnWidths.get(i) < length ){
                columnWidths.set(i,length);
            }
        }
   }
    /** 
     * Adds a data row to table
     * @param data : row values
     */
   public void addRow(String... data){
       addRow(Arrays.asList(data));
   }

   /** @return table total width */
   public int getWidth(){
       // columns count -1 + 2 separators
       // + sum of all columns widths
       int width = columnWidths.size()+1;
       if( width < 2 ){
           width = 2;
       }
       for(int columnWidth:columnWidths){
           width+=columnWidth;
       }
       return width;
   }

    /** @return table total height */
    public int getHeight(){
        return 2 + rows.size();
    }

    /** 
     * Renders table rows formatted for printing
     * @return formatted table rows 
     */
    public List<String> getRows(){
        List<String> result = new ArrayList<String>();
        result.add(separatorRow());
        for(List<String> row:rows){
            result.add( row == SEPARATOR ? separatorRow() : dataRow( row ) );
        }
        result.add(separatorRow());
        return result;
    }

    /** 
     * renders a data row to ascii
     * @param row : row to render
     * @return formatted data row properly padded to fit current column widths
     */
    private String dataRow(List<String> row){
        StringBuilder sb = new StringBuilder();
        sb.append(columnSeparator);
        if( row.isEmpty() ){
            sb.append(columnSeparator);
        } else {
            for(int i=0;i<row.size();i++){
                String cellValue = row.get(i);
                int cellLength = cellValue.length();
                sb.append(cellValue);
                while( cellLength < columnWidths.get(i) ){
                    sb.append(" ");
                    ++cellLength;
                }
                sb.append(columnSeparator);
            }
        }
        return sb.toString();
    }

    /** 
     * renders a separtor row to ascii
     * @return formatted separator row fitting current column widths
     * */
    private String separatorRow(){
        StringBuilder sb = new StringBuilder();
        sb.append(corner);
        if( columnWidths.isEmpty() ){
            sb.append(corner);
        } else {
            for(Integer colWidth:columnWidths){
                for(int i=0;i<colWidth;++i){
                    sb.append(rowSeparator);
                }
                sb.append(corner);
            }
        }
        return sb.toString();
    }

    //@Override
    //public String toString(){
    //    return null;
    //}
}

