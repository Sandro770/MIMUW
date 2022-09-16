package pl.edu.mimuw.matrix;

import java.util.Objects;

import static java.lang.Math.*;
import static pl.edu.mimuw.matrix.Diagonal.multiplyBothDiagonals;
import static pl.edu.mimuw.matrix.DoubleMatrixFactory.constant;
import static pl.edu.mimuw.matrix.DoubleMatrixFactory.full;
import static pl.edu.mimuw.matrix.MatrixCellValue.cell;
import static pl.edu.mimuw.matrix.Sparse.isSparse;

public abstract class DoubleMatrix implements IDoubleMatrix {
    protected final Shape shape;

    protected void assertGet(int row, int column) {
        assert(min(row, column) >= 0 && row < shape.rows && column < shape.columns);
    }

    public DoubleMatrix(Shape shape) {
        assert(shape != null);

        this.shape = shape;
    }

    protected double[][] createDoubleMatrix(Shape shape) {
        return new double[shape.rows][shape.columns];
    }

    protected static Shape getShapeAfterMultiplication(IDoubleMatrix left,
                                             IDoubleMatrix right) {
        assert(left != null);
        assert(right != null);
        assert(left.shape().columns == right.shape().rows);

        return Shape.matrix(left.shape().rows, right.shape().columns);
    }

    protected IDoubleMatrix timesFromLeft(IDoubleMatrix other) {
        return null;
    }

    @Override
    public IDoubleMatrix times(IDoubleMatrix other) {
        Shape s = getShapeAfterMultiplication(this, other);

        if (other instanceof DoubleMatrix) {
            var res = ((DoubleMatrix) other).timesFromLeft(this);

            if (res != null)
                return res;

            if (Objects.equals(this.getClass(), other.getClass())
                    && Objects.equals(this.getClass(), Sparse.class))
                return Sparse.sparseTimesSparse((Sparse) this, (Sparse) other);
        }

        if (isDiagonal(this) && isDiagonal(other))
            return multiplyBothDiagonals((Diagonal) this, (Diagonal) other);

        double[][] resultMatrix = createDoubleMatrix(s);

        for (int k = 0; k < this.shape().columns; k++)
            for (int i = 0; i < s.rows; i++)
                for (int j = 0; j < s.columns; j++) {
                    double v1 = this.get(i, k);
                    double v2 = other.get(k, j);

                    resultMatrix[i][j] += v1 * v2;
                }

        return full(resultMatrix);
    }

    private boolean isDiagonal(IDoubleMatrix other) {
        return other instanceof Diagonal;
    }

    @Override
    public IDoubleMatrix times(double scalar) {
        Shape s = this.shape();

        double[][] resultMatrix = createDoubleMatrix(s);

        for (int i = 0; i < s.rows; i++)
            for (int j = 0; j < s.columns; j++)
                resultMatrix[i][j] = this.get(i, j) * scalar;

        return full(resultMatrix);
    }

    @Override
    public IDoubleMatrix plus(IDoubleMatrix other) {
        Shape s = this.shape();

        assert(s.equals(other.shape()));

        if (isSparse(this) && isSparse(other))
            return ((Sparse)this).plusSparse((Sparse)other);

        double[][] result = this.data();

        for (int i = 0; i < s.rows; i++)
            for (int j = 0; j < s.columns; j++)
                result[i][j] += other.get(i, j);

        return full(result);
    }

    @Override
    public IDoubleMatrix plus(double scalar) {
        return this.plus(constant(this.shape(), scalar));
    }

    @Override
    public IDoubleMatrix minus(IDoubleMatrix other) {
        return plus(other.times(-1));
    }

    @Override
    public IDoubleMatrix minus(double scalar) {
        return this.plus(-scalar);
    }

    protected double getSumOfAbsoluteValuesInAColumn(int columnId) {
        double sum = 0;

        for (int i = 0; i < this.shape().rows; i++)
            sum += abs(this.get(i, columnId));

        return sum;
    }

    protected double getSumOfAbsoluteValuesInARow(int rowId) {
        double sum = 0;

        for (int j = 0; j < this.shape().columns; j++)
            sum += abs(this.get(rowId, j));

        return sum;
    }

    @Override
    public double normOne() {
        Shape s = this.shape();

        double max = 0;

        for (int j = 0; j < s.columns; j++)
            max = max(max, getSumOfAbsoluteValuesInAColumn(j));

        return max;
    }

    @Override
    public double normInfinity() {
        double max = 0;

        for (int i = 0; i < this.shape().rows; i++)
            max = max(max, getSumOfAbsoluteValuesInARow(i));

        return max;
    }

    @Override
    public double frobeniusNorm() {
        double norm = 0;
        Shape s = this.shape();

        for (int row = 0; row < s.rows; row++)
            norm += getSumOfSquaresInARow(row);

        return sqrt(norm);
    }

    protected double getSumOfSquaresInARow(int row) {
        double sum = 0;

        for (int col = 0; col < this.shape().columns; col++) {
            double elt = this.get(row, col);

            sum += elt * elt;
        }

        return sum;
    }

    @Override
    public Shape shape() {
        return this.shape;
    }

    protected StringBuilder fillEmptySpaces(MatrixCellValue[] values,
                                            double fillingValue) {
        if (values == null)
            values = new MatrixCellValue[0];

        StringBuilder sb = new StringBuilder();

        int idInValues = 0;

        for (int row = 0; row < shape.rows; row++) {
            MatrixCellValue previous = null;

            while (idInValues < values.length && values[idInValues].row == row) {
                var actual = values[idInValues];

                sb.append(fillEmptySpace(previous, actual, fillingValue));
                sb.append(actual.value).append(" ");

                previous = actual;
                idInValues++;
            }



            sb.append(fillEmptySpace(previous, null, fillingValue));

            sb.append('\n');
        }

        return sb;
    }

    protected StringBuilder valuesToString() {
        int cols = shape.columns;
        int rows = shape.rows;

        var values = new MatrixCellValue[shape.columns*shape.rows];

        int id = 0;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                values[id] = cell(i, j, this.get(i, j));

                id++;
            }

        return new StringBuilder(fillEmptySpaces(values, 0));
    }

    @Override
    public String toString() {
        return new StringBuilder(this.description())
                .append(" Matrix ")
                .append(this.shape)
                .append('\n')
                .append(this.valuesToString())
                .toString();
    }

    protected abstract StringBuilder description();

    private StringBuilder fillEmptySpace(MatrixCellValue previous,
                                         MatrixCellValue actual, double fillingValue)  {
        String value = Double.toString(fillingValue);

        if (previous == null && actual == null)
            return getCompressedStringOfOneValue(value, shape.columns);

        if (previous == null)
            return getCompressedStringOfOneValue(value, actual.column);

        if (actual == null)
            return getCompressedStringOfOneValue(value,
                    shape.columns - previous.column - 1);

        return getCompressedStringOfOneValue(value,
                actual.column - previous.column - 1);
    }

    protected static StringBuilder getCompressedStringOfOneValue(String value,
                                                               int length) {
        value = value + " ";

        if (length == 0)
            return new StringBuilder();

        if (length >= 3)
            return new StringBuilder(value + "... " + value);

        var sb = new StringBuilder();

        sb.append(value.repeat(Math.max(0, length)));

        return sb;
    }

    @Override
    public double[][] data() {
        Shape s = this.shape;
        double[][] resultMatrix = createDoubleMatrix(s);

        for (int i = 0; i < s.rows; i++)
            for (int j = 0; j < s.columns; j++)
                resultMatrix[i][j] = get(i, j);

        return resultMatrix;
    }
}
