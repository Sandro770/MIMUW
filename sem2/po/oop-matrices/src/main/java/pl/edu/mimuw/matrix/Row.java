package pl.edu.mimuw.matrix;

public class Row extends Regular {
    double[] row;

    public Row(Shape s, double[] values) {
        super(s);

        assert(values != null && s.columns == values.length);

        row = new double[s.columns];

        System.arraycopy(values, 0, row, 0, values.length);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);

        return this.row[column];
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Row");
    }
}
