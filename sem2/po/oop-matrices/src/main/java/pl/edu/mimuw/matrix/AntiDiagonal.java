package pl.edu.mimuw.matrix;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;

public class AntiDiagonal extends SquareWithOneOfDiagonals {
    public AntiDiagonal(double[] antiDiagonal) {
        super(antiDiagonal.length);
        
        int length = antiDiagonal.length;

        this.diagonalOrAntiDiagonalValues = new MatrixCellValue[length];

        for (int i = 0; i < length; i++) {
            double value = antiDiagonal[i];

            this.diagonalOrAntiDiagonalValues[i] = cell(i, length - i - 1,
                    value);
        }
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("AntiDiagonal");
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);
        if (onAntiDiagonal(row, column))
            return diagonalOrAntiDiagonalValues[row].value;
        else
            return 0;
    }

    private boolean onAntiDiagonal(int row, int column) {
        return row == shape.rows - column - 1;
    }
}
