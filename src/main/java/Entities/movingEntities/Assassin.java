package Entities.movingEntities;

import java.util.HashMap;
import java.util.Map;

import Entities.Entities;
import Entities.EntitiesFactory;
import Entities.Interactable;
import Items.InventoryItem;
import Items.ItemsFactory;
import Items.TheOneRingItem;
import Items.materialItem.SunStoneItem;
import Items.materialItem.TreasureItem;
import dungeonmania.Dungeon;
import dungeonmania.Buffs.Invincible;
import dungeonmania.Buffs.Invisible;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Assassin extends MindControllableEntities implements Interactable, Portalable, Boss {
    private static final int BRIBE_RADIUS = 2;
    private static final int ATTACK_DAMAGE = 4;
    private static final int MAX_HEALTH = 80;
    private Map<String, Double> itemDrop = new HashMap<String, Double>() {
        {
            // One ring = 5%
            // Armour = 20%
            // Anduril = 10%
            put("one_ring", 5.0);
            put("armour", 20.0);
            put("anduril", 10.0);
        }
    };

    public Assassin(String id, Position position) {
        super(id, "assassin", position, true, true, MAX_HEALTH, ATTACK_DAMAGE);
    }

    /**
     * @param dungeon
     */
    @Override
    public void makeMovement(Dungeon dungeon) {
        Direction moveDirection = Direction.NONE;
        Position originalPos = getPosition();
        // invisible has higher priority
        if (dungeon.getCharacter().getBuffs(Invisible.class) != null) {
            // does not move if invis
            setPosition(originalPos, dungeon);
            walkOn(originalPos, dungeon);
            return;
        } else {
            // runs away if invis
            Invincible invin = (Invincible) dungeon.getCharacter().getBuffs(Invincible.class);
            if (invin != null) {
                invin.invinMovement(dungeon, this);
                return;
            }
        }

        // get next position in shortest path
        Position nextPos = getOneStepPos(dungeon, dungeon.getCharacter().getPosition());
        // if there is no path, it does not move
        if (nextPos == null) {
            setPosition(originalPos, dungeon);
            walkOn(originalPos, dungeon);
        } else {
            // move to next position in shortest path
            setPosition(nextPos, dungeon);
            // call walk on on all entities that are on the position
            walkOn(nextPos, dungeon);
            // if it enters a portal, correct its position
            if (getPosition() != nextPos) {
                Position changePos = Position.calculatePositionBetween(nextPos, originalPos);
                if (changePos.getX() != 0) {
                    moveDirection = getDirection(changePos.getX(), "x");
                } else {
                    moveDirection = getDirection(changePos.getY(), "y");
                }
                Position newerPosition = getPosition().translateBy(moveDirection);
                if (checkMovable(newerPosition, dungeon)) {
                    setPosition(getPosition().translateBy(moveDirection), dungeon);
                }
            }
        }
    }

    /**
     * @param dungeon
     */
    public void bribeAssassin(Dungeon dungeon) throws InvalidActionException {
        Character c = dungeon.getCharacter();

        // check if sun_stone is in inventory
        InventoryItem s = c.getInventoryItem(SunStoneItem.class);
        InventoryItem t = null;
        InventoryItem o = c.getInventoryItem(TheOneRingItem.class);
        // check if char has treasure
        if (s == null) {
            t = c.getInventoryItem(TreasureItem.class);
            if (t == null) {
                throw new InvalidActionException("Character does not have a treasure or sun stone!!");
            }
        }

        // check if char has one ring
        if (o == null) {
            throw new InvalidActionException("Character does not have the one ring!!");
        }

        // check if assassin is in range
        Position p = Position.calculatePositionBetween(c.getPosition(), this.getPosition());
        int d = Math.abs(p.getX()) + Math.abs(p.getY());
        if (d > BRIBE_RADIUS) {
            throw new InvalidActionException("Assassin is not in range!!");
        }
        // remove assassin from list
        dungeon.removeEntities(this);
        // add bribed assassin from list
        c.removeInventory(t);
        c.removeInventory(o);
        Entities newBribedAssassin = EntitiesFactory.createEntities("bribed_assassin", this.getPosition());
        dungeon.addEntities(newBribedAssassin);
    }

    /**
     * @param dungeon
     * @throws InvalidActionException
     */
    @Override
    public void interact(Dungeon dungeon) throws InvalidActionException {
        if (!mindControl(dungeon)) {
            bribeAssassin(dungeon);

        }
    }

    /**
     * @param dungeon
     */
    @Override
    public void dropItems(Dungeon dungeon) {
        for (String item : itemDrop.keySet()) {
            if (dungeon.getRandom().nextInt(100) < itemDrop.get(item)) {
                dungeon.getCharacter().addInventory(ItemsFactory.createItem(item));
            }
        }
    }
}
