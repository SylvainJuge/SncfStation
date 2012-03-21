package sylvain.juge.sncfstation;

import java.util.List;
import java.util.Arrays;

import org.testng.annotations.Test;

import sylvain.juge.sncfstation.AsciiTable;

public class AsciiTableTest {

    // TODO : add hamcrest matchers
    // - requires to find one that test for list equality

    @Test
    public void emptyTable(){
        AsciiTable table = AsciiTable.newDefault();
        assertThat( table.getWidth(), is(2));
        assertThat( table.getHeight(), is(2));
        assertThat( table.getRows(), is(Arrays.asList("oo","oo")));
    }

    @Test
    public void emptyTableWithSeparatorRow(){
        AsciiTable table = AsciiTable.newDefault();
        table.addSeparatorRow();
        assertThat( table.getWidth(), is(2));
        assertThat( table.getHeight(), is(3));
        assertThat( table.getRows(), is(Arrays.asList("oo","oo","oo")));
    }

    @Test
    public void singleEntryTable(){
        AsciiTable table = AsciiTable.newDefault();
        String value = "test";
        table.addRow(Arrays.asList(value));

        checkTableDimensions(table);
        assertThat( table.getWidth(), is(value.length()+2));
        assertThat( table.getHeight(), is(3));
        assertThat( table.getRows(), is(Arrays.asList("o----o","|test|","o----o"));
    }

    /** ensures that table is consistent between its rows and its getters, and is rectangular 
     * @param table ascii table to check
     */
    private static void checkTableLayout(AsciiTable table){
        int width = table.getWidth();
        int height = table.getHeight();
        List<String> rows = table.getRows();
        // TODO : to enhance when table will allow to introspect it's separators
        // TODO : then detect where columns separators are and make sure that they are properly aligned as on 1st line
        assertThat(height, is(rows.size()));
        for(String row:rows){
            assertThat(row.length(), is(width));
        }
    }

    // TODO : test defensive copy when adding rows
    // - create a table
    // - add one row with known values
    // - add one value to the list we passed in
    // - check that table does not have the latest value we added
    public void defensiveCopyOnAddRow(){
        AsciiTable table = AsciiTable.newDefault();
        List<String> mutableRow = new ArrayList<String>(Arrays.asList("1st"));
        table.addRow(mutableRow);
        mutableRow.add("2cnd");
        assertThat(table.getRows().size(), is(3));
        assertThat(table.getRows().get(1), is("|1st|"));
    }

    // just to make it compile before we get real test dependencies
    private void assertThat(Object o1, Object o2){
        throw new RuntimeException("just emulating for compilation yet");
    }
    private Object is(Object o){
        return o;
    }

}
