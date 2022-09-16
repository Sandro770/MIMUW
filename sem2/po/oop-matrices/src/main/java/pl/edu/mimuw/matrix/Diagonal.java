package pl.edu.mimuw.matrix;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;

public class Diagonal extends SquareWithOneOfDiagonals {

    protected Diagonal(int length) {
        super(length);
    }

    protected Diagonal(double[] diagonal) {
        super(diagonal.length);

        this.diagonalOrAntiDiagonalValues = new MatrixCellValue[diagonal.length];
        
        for (int i = 0; i < diagonal.length; i++) {
            double value = diagonal[i];
            this.diagonalOrAntiDiagonalValues[i] = cell(i, i, value);
        }
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Diagonal");
    }

    protected static Diagonal multiplyBothDiagonals(Diagonal left,
                                                 Diagonal right) {
        var newShape = getShapeAfterMultiplication(left, right);

        var values = new double[newShape.rows];

        for (int i = 0; i < newShape.rows; i++)
            values[i] = left.get(i, i) * right.get(i, i);

        return new Diagonal(values);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);

        return row != column ? 0 : diagonalOrAntiDiagonalValues[row].value;
    }
}
