package pl.edu.mimuw;

import pl.edu.mimuw.matrix.IDoubleMatrix;

import static pl.edu.mimuw.matrix.DoubleMatrixFactory.*;
import static pl.edu.mimuw.matrix.MatrixCellValue.cell;
import static pl.edu.mimuw.matrix.Shape.matrix;

public class Main {

  private static void assertEquals(double[][] m1, double[][] m2) {
    assert(m1 != null && m2 != null);
    assert(m1.length == m2.length);
    assert(m1[0].length == m2[0].length);

    for (int i = 0; i < m1.length; i++)
      for (int j = 0; j < m1[0].length; j++) {
        assert Math.abs(m1[i][j] - m2[i][j]) < 0.0001;
      }
  }

  public static void main(String[] args) {
    var shape = matrix(10, 10);

    double[] values = {1,2,3,4,5,6,7,8,9,10};

    double[][] valuesToFull = new double[10][10];
    for(int i = 0; i < 10; i++)
      for(int j = 0; j < 10; j++)
        valuesToFull[i][j] = i * 10 + j + 1;

    var FULL = full(valuesToFull);
    var SPARSE = sparse(shape,
            cell(0, 0, 11),
            cell(5 , 2, 63),
            cell(2, 3, 34),
            cell(3, 2, 43),
            cell(9, 8, 98),
            cell(9, 9, 99)
    );
    var IDENTITY = identity(10);
    var ROW = row(shape, values);
    var COLUMN = column(shape, values);
    var DIAGONAL = diagonal(values);
    var ANTIDIAGONAL = antiDiagonal(values);
    var Constant = constant(shape, -2);
    var ZERO = zero(shape);
    var VECTOR = vector(values);

    IDoubleMatrix[] matrices = {
            FULL,
            SPARSE,
            IDENTITY,
            ROW,
            COLUMN,
            DIAGONAL,
            ANTIDIAGONAL,
            Constant,
            ZERO,
            VECTOR
    };

    for (var m: matrices)
      System.out.println(m);

    System.out.println("TAKING SQUARES-----------------");

    for (var m: matrices)
      if (m != VECTOR)
        System.out.println(m.times(m));

    System.out.println("ADDING SAME MATRICES-------");
    for (var m: matrices)
      assertEquals(m.plus(m).data(), m.times(2).data());

    System.out.println("CHECKING PLUS MINUS-------");
    for (var m: matrices)
      if (m != VECTOR)
       assertEquals(m.plus(m).minus(m).minus(m).data(), ZERO.data());
  }
}
