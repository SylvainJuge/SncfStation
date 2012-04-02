package sylvain.juge.sncfstation;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import sylvain.juge.sncfstation.AsciiTable;

import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class AsciiTableTest {

    @Test
    public void emptyTable(){
        AsciiTable table = AsciiTable.newDefault();
        checkLayout( table, 2, 2 );
        // TODO : find a way to test collections
        //assertThat( table.getRows(), contains(Arrays.asList("oo","oo")).describedAs("should only have corner characters"));
    }

    @Test
    public void emptyTableWithSeparatorRow(){
        AsciiTable table = AsciiTable.newDefault();
        table.addSeparatorRow();
        checkLayout( table, 2, 3 );
        assertThat( table.getRows(), is(Arrays.asList("oo","oo","oo")));
    }

    @Test
    public void singleEntryTable(){
        AsciiTable table = AsciiTable.newDefault();
        table.addRow(Arrays.asList("test"));
        checkLayout( table, 6, 3 );
        assertThat( table.getRows(), is(Arrays.asList("o----o","|test|","o----o")) );
    }

    /**
     * ensures that table is consistent between its rows and its getters, and is rectangular 
     * @param table ascii table to check
     * @param width expected width
     * @param height expected height
     */
    private static void checkLayout( AsciiTable table, int width, int height ){
        assertThat( table, is(notNullValue()) );
        assertThat( table.getWidth(), describedAs("width must be %0",is(width),width) );
        assertThat( table.getHeight(), describedAs("height must be %0",is(height),height) );
        List<String> rows = table.getRows();
        // TODO : to enhance when table will allow to introspect it's separators
        // TODO : then detect where columns separators are and make sure that they are properly aligned as on 1st line
        assertThat( rows, is( notNullValue() ) );
        assertThat( rows.size(), describedAs("row count must equal height %0",is(height), height ) );
        for( String row:rows ){
            assertThat( row.length(), describedAs( "row width must be %0", is(width) , width ) );
        }
    }

    // TODO : test defensive copy when adding rows
    // - create a table
    // - add one row with known values
    // - add one value to the list we passed in
    // - check that table does not have the latest value we added
//    @Test
//    public void defensiveCopyOnAddRow(){
//        AsciiTable table = AsciiTable.newDefault();
//        List<String> mutableRow = new ArrayList<String>(Arrays.asList("1st"));
//        table.addRow(mutableRow);
//        mutableRow.add("2cnd");
//        assertThat(table.getRows().size(), is(3));
//        assertThat(table.getRows().get(1), is("|1st|"));
//    }
//
}
