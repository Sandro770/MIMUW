package pl.edu.mimuw.matrix;

public class Constant extends Regular {
    final double constant;

    public Constant(Shape shape, double constant) {
        super(shape);

        this.constant = constant;
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Constant");
    }

    @Override
    protected StringBuilder valuesToString() {
        return fillEmptySpaces(null, constant);
    }

    @Override
    public double get(int row, int column) {
        assertGet(row, column);

        return this.constant;
    }
}
