package pl.edu.mimuw.matrix;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;

public class Identity extends Diagonal {
    public Identity(int length) {
        super(length);
    }

    public IDoubleMatrix times(IDoubleMatrix other) {
        getShapeAfterMultiplication(this, other);

        return other;
    }

    protected IDoubleMatrix timesFromLeft(IDoubleMatrix other) {
        getShapeAfterMultiplication(other, this);

        return other;
    }

    protected StringBuilder valuesToString() {
        var diagonal = new MatrixCellValue[shape.rows];

        for (int i = 0; i < shape.rows; i++)
            diagonal[i] = cell(i, i, 1);

        return fillEmptySpaces(diagonal, 0);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);

        return row == column ? 1 : 0;
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Identity");
    }
}
