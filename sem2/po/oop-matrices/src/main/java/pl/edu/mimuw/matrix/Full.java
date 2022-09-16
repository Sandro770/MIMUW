package pl.edu.mimuw.matrix;

import static pl.edu.mimuw.matrix.Shape.matrix;

public class Full extends Irregular {
    private final double[][] matrix;

    protected Full(double[][] matrix) {
        super(Full.getShape(matrix));

        int tmpLen = matrix[0].length;
        for (double[] arr: matrix) {
            assert (arr.length == tmpLen);
        }

        Shape s = this.shape();

        this.matrix = createDoubleMatrix(s);

        for (int i = 0; i < s.rows; i++)
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, s.columns);
    }

    protected Full(double[] matrix) {
        super(Full.getShape(matrix));

        Shape s = this.shape();

        this.matrix = createDoubleMatrix(s);

        for (int i = 0; i < s.rows; i++)
            this.matrix[i][0] = matrix[i];
    }

    private static Shape getShape(double[] matrix) {
        assert(matrix != null);

        return matrix(matrix.length, 1);
    }

    private static Shape getShape(double[][] matrix) {
        assert(matrix != null && matrix.length > 0);

        return matrix(matrix.length, matrix[0].length);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);
        return this.matrix[row][column];
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Full").append(this.shape.columns == 1 ?
                " Vector" : "");
    }
}
