package pl.edu.mimuw.matrix;

public class DoubleMatrixFactory {

  private DoubleMatrixFactory() {
  }

  public static IDoubleMatrix sparse(Shape shape, MatrixCellValue... values){
    return new Sparse(shape, values);
  }

  public static IDoubleMatrix full(double[][] values) {
    return new Full(values);
  }

  public static IDoubleMatrix identity(int size) {
    return new Identity(size);
  }

  public static IDoubleMatrix diagonal(double... diagonalValues) {
    return new Diagonal(diagonalValues);
  }

  public static IDoubleMatrix antiDiagonal(double... antiDiagonalValues) {
    return new AntiDiagonal(antiDiagonalValues);
  }

  public static IDoubleMatrix vector(double... values){
    return new Full(values);
  }

  public static IDoubleMatrix zero(Shape shape) {
    return new Zero(shape);
  }

  public static IDoubleMatrix constant(Shape shape, double constant) {
    return new Constant(shape, constant);
  }

  public static IDoubleMatrix column(Shape shape, double... rowValues) {
    return new Column(shape, rowValues);
  }

  public static IDoubleMatrix row(Shape shape, double... columnValues) {
    return new Row(shape, columnValues);
  }
}
