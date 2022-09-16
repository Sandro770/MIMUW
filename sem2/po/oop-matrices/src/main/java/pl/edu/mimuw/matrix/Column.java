package pl.edu.mimuw.matrix;

public class Column extends Regular {
    double[] rows;

    public Column(Shape s, double[] values) {
        super(s);

        assert(values != null && s.rows == values.length);

        rows = new double[s.rows];

        System.arraycopy(values, 0, rows, 0, values.length);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);

        return this.rows[row];
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Column");
    }

    @Override
    protected StringBuilder valuesToString() {
        var sb = new StringBuilder();

        for (double v: rows) {
            String vStr = Double.toString(v);

            sb.append(getCompressedStringOfOneValue(vStr, shape.columns));
            sb.append('\n');
        }

        return sb;
    }
}
