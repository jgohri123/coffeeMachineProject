package domainLayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CoffeeMachine {

    //TODO: Control concurrency
    private final int numberOfSlots;

    private final Map<Ingredient, ConsumablePortion> inventory;

    private final Map<String, Beverage> beverages;


    public CoffeeMachine(int numberOfSlots, Collection<Portion> fill, Collection<Beverage> beverages) {
        this.numberOfSlots = numberOfSlots;
        this.inventory = new HashMap<>();
        this.beverages = new HashMap<>();

        for (Portion portion : fill) {
            this.inventory.put(portion.getIngredient(),
                    new ConsumablePortion(portion.getIngredient(), portion.getQuantity()));
        }

        for(Beverage beverage: beverages) {
            this.beverages.put(beverage.getName().toLowerCase(), beverage);
        }

    }

    public CoffeeMachine(Collection<Portion> fill, Collection<Beverage> beverages) {
        this(1, fill, beverages);
    }


    public Beverage prepareAndDispense(String beverageName) throws BeverageCannotBePreparedException, IngredientNotAvailableException {
        Beverage beverage = this.beverages.get(beverageName);
        this.consumeIngredients(beverage);

        // No exception means beverage is prepared. We return the same instance because its immutable.
        // Otherwise a clone should have been returned.
        System.out.println(beverage.getName() + " is prepared");
        return beverage;
    }

    private synchronized void consumeIngredients(Beverage beverage) throws BeverageCannotBePreparedException, IngredientNotAvailableException {
        Collection<Portion> requiredPortions = beverage.getPortions();

        // Check all required ingredients are present
        for (Portion required : requiredPortions) {
            if (!this.inventory.containsKey(required.getIngredient())) {
                throw new IngredientNotAvailableException(beverage.getName() + " cannot be prepared because "
                        + required.getIngredient().getName() + " is not available", required.getIngredient());
            }
        }

        // Check all required ingredients are present in sufficient quantity
        for (Portion required : requiredPortions) {
            ConsumablePortion available = this.inventory.get(required.getIngredient());
            try {
                available.ensureQuantity(required.getQuantity());
            } catch (InsufficientIngredientQuantityException e) {
                throw new BeverageCannotBePreparedException(beverage.getName()
                        + " cannot be prepared because item " + required.getIngredient().getName()
                        + " is " + e.getCurrentPortion().getQuantity(), e);
            }
        }

        // Consume all required ingredients in the quantity as required to prepare the beverage
        for (Portion required : requiredPortions) {
            ConsumablePortion available = this.inventory.get(required.getIngredient());
            try {
                available.consume(required.getQuantity());
            } catch (InsufficientIngredientQuantityException e) {
                String message = "ERROR: some fault occurred, discarding the mix. Please try again.";
                System.err.println(message);
                throw new BeveragePreparationError(message);
            }
        }
    }
}
