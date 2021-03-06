package com.github.dmn1k.table.diff;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class TableRow {
    private List<TableCell> cells;

    public static TableRow create() {
        return create(List.empty());
    }

    public static TableRow create(List<TableCell> cells) {
        return new TableRow(cells);
    }

    /**
     * @return all Primary Key-Cells concatenated as a single string
     * or ALL cell-values if there is no primary key-cell
     */
    public String primaryKeyValue() {
        return cells
                .filter(TableCell::isPrimaryKey)
                .orElse(cells)
                .map(TableCell::getValue)
                .foldLeft("", String::concat);
    }

    public TableRow addCell(TableCell cell) {
        return create(cells.append(cell));
    }

    public TableCell getCellOrMissing(Option<Integer> index) {
        return index.map(cells::get).getOrElse(TableCell.MISSING_CELL);
    }

    /**
     * Uses comparisonFn to determine of normalizedOther is the same row
     *
     * @param normalizedOther row to compare against
     * @param comparisonFn    function which defines equality of cells
     * @return true if both rows are considered the same
     */
    public boolean isSameAs(TableRow normalizedOther, Function2<TableCell, TableCell, Boolean> comparisonFn) {
        if (cells.size() != normalizedOther.getCells().size()) {
            return false;
        }

        return cells.zipWith(normalizedOther.getCells(), comparisonFn)
                .foldLeft(true, (c1, c2) -> c1 && c2);
    }
}
