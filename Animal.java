import java.util.List;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Entity
{
    private boolean female;
    public Animal(boolean female, Field field, Location location){
        super(field,location);
        this.female = female;
    }
    
    /**
     * This is a method that returns whether or not the animal is female or not
     * @return  boolean whether the instance is a female animal
     */
    protected boolean isFemale(){
        return this.female;
    }
}
