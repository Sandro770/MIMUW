package pl.edu.mimuw.matrix;

import java.util.*;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;

public class Sparse extends Irregular {
    private final List<List<MatrixCellValue>> matrix;

    private List<MatrixCellValue> getRow(int rowId) {
        int candidateId = matrix.size();

        for (int id = 0; id < matrix.size(); id++) {
            var row = matrix.get(id);

            if (!row.isEmpty()) {
                if (row.get(0).row == rowId)
                    return row;

                if (row.get(0).row > rowId) {
                    candidateId = id;
                    break;
                }
            }
            else
                return row;
        }

        matrix.add(candidateId, new LinkedList<>());

        return matrix.get(candidateId);
    }

    private void addCell(MatrixCellValue cell) {
        var row = getRow(cell.row);

        assert (!existsDistinct(row, cell));

        if (cell.value == 0)
            return;

        int id = 0;

        for (var c : row) {
            if (c.column == cell.column)
                return;

            if (c.column > cell.column)
                break;

            id++;
        }

        row.add(id, cell);
    }

    private boolean existsDistinct(List<MatrixCellValue> row,
                                   MatrixCellValue cell) {
        return row.stream()
                .anyMatch(c ->
                        c.column == cell.column &&
                                c.value != cell.value);
    }

    public Sparse(Shape shape, MatrixCellValue... values) {
        super(shape);
        
        for (var val: values) 
            this.assertGet(val.row, val.column);
        
        this.matrix = new LinkedList<>();

        Arrays.stream(values)
                .forEach(this::addCell);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);

        var elementsInARow = getRow(row);


        var element = elementsInARow.stream()
                        .filter(c -> c.column == column)
                        .findAny();

        return element.map(c -> c.value).orElse(0.0);
    }

    protected static Sparse sparseTimesSparse(Sparse left, Sparse right) {
        return left.timesSparse(right);
    }

    private static double getSumOfAbsoluteValuesInARow(List<MatrixCellValue> row) {
        return row.stream()
                .mapToDouble(c -> Math.abs(c.value))
                .sum();
    }

    @Override
    public double normOne() {
        return matrix.stream()
                .mapToDouble(Sparse::getSumOfAbsoluteValuesInARow)
                .max()
                .orElse(0);
    }

    private MatrixCellValue[] getColumn(int colId) {
        return matrix.stream()
                .flatMap(Collection::stream)
                .filter(c -> c.column == colId)
                .toArray(MatrixCellValue[]::new);
    }

    @Override
    public double normInfinity() {
        return getNonEmptyColumnIds()
                .stream()
                .mapToDouble(id ->
                        Arrays.stream(this.getColumn(id))
                                .mapToDouble(c -> Math.abs(c.value))
                                .sum()
                ).max()
                .orElse(0);
    }

    @Override
    public double frobeniusNorm() {
        return Math.sqrt(
                matrix.stream()
                        .flatMap(Collection::stream)
                        .mapToDouble(c -> c.value * c.value)
                        .sum()
        );
    }

    protected static boolean isSparse(IDoubleMatrix x) {
        return Objects.equals(x.getClass(), Sparse.class);
    }

    protected Sparse plusSparse(Sparse other) {
        var values = new ArrayList<>(
                this.getNonEmptyCells().stream()
                        .map(c ->
                                cell(c.row, c.column,c.value + other.get(c.row,
                                        c.column))
                        ).toList());


        other.getNonEmptyCells().stream()
                .filter(c -> this.get(c.row, c.column) == 0)
                .forEach(values::add);

        return new Sparse(this.shape, values.toArray(MatrixCellValue[]::new));
    }

    private List<MatrixCellValue> getNonEmptyCells() {
        return this.matrix.stream()
                .flatMap(Collection::stream)
                .toList();
    }

    private Sparse timesSparse(Sparse right) {
        Shape shape = getShapeAfterMultiplication(this, right);

        var nonEmptyRowIds = matrix.stream()
                .map(row -> row.get(0).row)
                .toList();

        var nonEmptyColIds = right.getNonEmptyColumnIds();

        var values = new LinkedList<MatrixCellValue>();

        for (var r: nonEmptyRowIds)
            for (var c: nonEmptyColIds) {
                double val = 0;

                var row = this.getRow(r);

                for (var elt: row) {
                    int k = elt.column;

                    val += elt.value * right.get(k, c);
                }

                values.add(cell(r, c, val));
            }

        return new Sparse(shape, values.toArray(MatrixCellValue[]::new));
    }

    private List<Integer> getNonEmptyColumnIds() {
        return matrix.stream()
                .flatMap(Collection::stream)
                .mapToInt(c -> c.column)
                .boxed()
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public IDoubleMatrix times(double scalar) {
        var list = matrix.stream()
                .flatMap(Collection::stream)
                .map(x -> cell(x.row, x.column,
                        x.value * scalar)
                )
                .toArray(MatrixCellValue[]::new);

        return new Sparse(shape, list);
    }

    @Override
    public StringBuilder valuesToString() {
        MatrixCellValue[] values;

        values =  matrix.stream()
                .flatMap(Collection::stream)
                .toArray(MatrixCellValue[]::new);

        return fillEmptySpaces(values, 0);
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Sparse");
    }
}
