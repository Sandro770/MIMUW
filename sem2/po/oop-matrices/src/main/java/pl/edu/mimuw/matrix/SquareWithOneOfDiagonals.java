package pl.edu.mimuw.matrix;

public abstract class SquareWithOneOfDiagonals extends Regular {
    protected MatrixCellValue[] diagonalOrAntiDiagonalValues;

    protected SquareWithOneOfDiagonals(int length) {
        super(Shape.matrix(length, length));
    }

    protected StringBuilder valuesToString() {
        return fillEmptySpaces(diagonalOrAntiDiagonalValues, 0);
    }

    @Override
    public IDoubleMatrix times(IDoubleMatrix other) {
        return super.times(other);
    }
}
