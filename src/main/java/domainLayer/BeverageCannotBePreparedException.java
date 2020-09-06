package domainLayer;

public class BeverageCannotBePreparedException extends Exception{

    public BeverageCannotBePreparedException(String message, InsufficientIngredientQuantityException e) {
        super(message, e);
    }

    public BeverageCannotBePreparedException(String message) {
        super(message);
    }
}
