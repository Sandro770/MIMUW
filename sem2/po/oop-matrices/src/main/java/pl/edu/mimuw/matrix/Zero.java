package pl.edu.mimuw.matrix;

public class Zero extends Constant {
    public Zero(Shape shape) {
        super(shape, 0);
    }

    @Override
    public IDoubleMatrix times(IDoubleMatrix other) {
        Shape newShape = getShapeAfterMultiplication(this, other);

        return new Zero(newShape);
    }

    @Override
    protected IDoubleMatrix timesFromLeft(IDoubleMatrix other) {
        Shape newShape = getShapeAfterMultiplication(other, this);

        return new Zero(newShape);
    }

    @Override
    protected StringBuilder description() {
        return new StringBuilder("Zero");
    }
}
