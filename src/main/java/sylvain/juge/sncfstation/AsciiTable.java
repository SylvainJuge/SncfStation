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
        rows.add(SEPARATOR);
    }
    private final static List<String> SEPARATOR = new ArrayList<String>();

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

    public int getHeight(){
        return 2 + rows.size();
    }

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
            }
            sb.append(corner);
        }
        return sb.toString();
    }

    @Override
    public String toString(){
        return null;
    }
}

