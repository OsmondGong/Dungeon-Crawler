package Entities.movingEntities;

import Entities.Entities;
import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.util.Position;

import java.util.List;

public abstract class MovingEntities extends Entities implements Movable {
    private double health;

    public MovingEntities(String id, String type, Position position, boolean isInteractable, boolean isWalkable, double health) {
        super(id, type, position, isInteractable, isWalkable);
        this.health = health;
    }

    
    /** 
     * @return double
     */
    public double getHealth() {
        return health;
    }

    
    /** 
     * @param health
     */
    public void setHealth(double health) {
        this.health = health;
    }


    
    /** 
     * @param position
     * @param controller
     * @return boolean
     */
    @Override
    public boolean checkMovable(Position position, List<Entities> entities) {
        // if position has unwalkable entity
        for (Entities e : entities) {
            if (e.getPosition().equals(position) && !e.isWalkable()) {
                return false;
            }
        }
        return true;
    }

    
    /** 
     * @param e
     * @return boolean
     */
    protected boolean isMovingEntityButNotCharacter(Entities e) {
        if (e instanceof MovingEntities && !(e instanceof Character)) {
            return true;
        }
        return false;
    }
}
