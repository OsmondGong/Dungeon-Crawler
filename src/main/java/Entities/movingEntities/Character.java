package Entities.movingEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entities.BeforeWalkedOn;
import Entities.Entities;
import Entities.WalkedOn;
import Items.InventoryItem;
import Items.ItemsFactory;
import Items.TheOneRingItem;
import Items.ConsumableItem.InvincibilityPotionItem;
import Items.ConsumableItem.InvisibilityPotionItem;
import Items.Equipments.Armours.Armours;
import Items.Equipments.Shields.Shields;
import Items.Equipments.Weapons.Weapons;
import Items.materialItem.Materials;
import Items.materialItem.TreasureItem;
import dungeonmania.Dungeon;
import dungeonmania.Buffs.Buffs;
import dungeonmania.Buffs.Invincible;
import dungeonmania.Buffs.Invisible;
import dungeonmania.util.Battle;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

public class Character extends Mobs implements WalkedOn, Portalable {

    /**
     * inventory = [ {item1}, {item2}... ]
     */
    private static int MAX_HEALTH;
    public static Object getInventory;
    private static final int ATTACK_DAMAGE = 3;
    private ArrayList<InventoryItem> inventory;
    private Fightable inBattleWith = null;
    private Position prevPosition;
    private List<Buffs> buffs = new ArrayList<Buffs>();

    public Character(String id, Position position) {
        super(id, "player", position, false, true, Character.MAX_HEALTH, ATTACK_DAMAGE);
        setPrevPosition(getPosition());
        inventory = new ArrayList<InventoryItem>();
    }

    /**
     * @return Buffs
     */
    public Buffs getBuffs(Class<?> cls) {
        for (Buffs buff : getBuffs()) {
            if (cls.isInstance(buff)) {
                return buff;
            }
        }
        return null;
    }

    /**
     * @param b
     */
    public void addBuff(Buffs b) {
        for (Buffs buff : getBuffs()) {
            if (buff.getClass() == b.getClass()) {
                getBuffs().remove(buff);
                break;
            }
        }
        buffs.add(b);
    }

    /**
     * @param b
     */
    public void removeBuff(Buffs b) {
        buffs.remove(b);
    }

    /**
     * @return List<Buffs>
     */
    public List<Buffs> getBuffs() {
        return buffs;
    }

    /**
     * @return InventoryItem
     */
    public InventoryItem hasKey() {
        for (InventoryItem i : getInventory()) {
            if (i.getType().substring(0, 3).equals("key")) {
                return i;
            }
        }
        return null;
    }

    public static int getMAX_HEALTH() {
        return MAX_HEALTH;
    }

    public static void setMAX_HEALTH(int MAX_HEALTH) {
        Character.MAX_HEALTH = MAX_HEALTH;
    }

    /**
     * @return InventoryItem
     */
    public InventoryItem getInventoryItem(Class<?> cls) {
        for (InventoryItem item : getInventory()) {
            if (cls.isInstance(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @return ArrayList<InventoryItem>
     */
    public ArrayList<InventoryItem> getInventory() {
        return inventory;
    }

    /**
     * @param inventory
     */
    public void setInventory(ArrayList<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    /**
     * @param item
     */
    public void addInventory(InventoryItem item) {
        inventory.add(item);
    }

    /**
     * @param item
     */
    public void removeInventory(InventoryItem item) {
        inventory.remove(item);
    }

    /**
     * @param collectable
     * @param dungeon
     */
    public void checkForBuildables(InventoryItem collectable, Dungeon dungeon) {
        dungeon.setBuildables(new ArrayList<String>());

        int wood = 0;
        int arrow = 0;
        int key = 0;
        int treasure = 0;

        for (InventoryItem item : inventory) {
            if (item.getType().equals("wood")) {
                wood++;
            } else if (item.getType().equals("arrow")) {
                arrow++;
            } else if (item.getType().substring(0, 3).equals("key")) {
                key++;
            } else if (item.getType().equals("treasure")) {
                treasure++;
            }
        }

        // Temporary, refactor later
        // List<Map<String, Integer>> bowRecipes = BowItem.getRecipes();
        // bow
        if (wood >= 1 && arrow >= 3) {
            dungeon.addBuildables("bow");
        }

        // shield
        if (wood >= 2) {
            if (treasure >= 1) {
                dungeon.addBuildables("shield");

            } else if (key >= 1) {
                dungeon.addBuildables("shield");
            }
        }
    }

    /**
     * @param buildable
     * @return boolean
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public boolean build(String buildable) throws IllegalArgumentException, InvalidActionException {
        // Refactor later
        if (buildable.equals("bow")) {
            List<InventoryItem> wood = new ArrayList<>();
            List<InventoryItem> arrow = new ArrayList<>();
            for (InventoryItem item : inventory) {
                if (wood.size() < 1 && item.getType().equals("wood"))
                    wood.add(item);
                else if (arrow.size() < 3 && item.getType().equals("arrow"))
                    arrow.add(item);

                if (wood.size() == 1 && arrow.size() == 3) {
                    // build bow
                    inventory.removeAll(wood);
                    inventory.removeAll(arrow);

                    InventoryItem bow = ItemsFactory.createItem("bow");
                    inventory.add(bow);
                    return true;
                }
            }
            throw new InvalidActionException("Player does not have required materials");
        } else if (buildable.equals("shield")) {
            List<InventoryItem> wood = new ArrayList<>();
            List<InventoryItem> key = new ArrayList<>();
            List<InventoryItem> treasure = new ArrayList<>();
            for (InventoryItem item : inventory) {
                if (wood.size() < 2 && item.getType().equals("wood"))
                    wood.add(item);
                else if (key.size() < 1 && item.getType().substring(0, 3).equals("key"))
                    key.add(item);
                else if (treasure.size() < 1 && item.getType().equals("treasure"))
                    treasure.add(item);

                if (wood.size() == 2) {
                    if (key.size() == 1) {
                        // build shield
                        inventory.removeAll(wood);
                        inventory.removeAll(key);

                        InventoryItem shield = ItemsFactory.createItem("shield");
                        inventory.add(shield);
                        return true;
                    } else if (treasure.size() == 1) {
                        // build bow
                        inventory.removeAll(wood);
                        inventory.removeAll(treasure);

                        InventoryItem shield = ItemsFactory.createItem("shield");
                        inventory.add(shield);
                        return true;
                    }
                }
            }
            throw new InvalidActionException("Player does not have required materials");
        } else {
            throw new IllegalArgumentException("Buildable is not bow or shield");
        }
    }

    /**
     * @param dungeon
     */
    @Override
    public void makeMovement(Dungeon dungeon) {
        setInBattleWith(null);
        Position newPosition = getPosition().translateBy(getMovementDirection());
        if (checkMovable(newPosition, dungeon)) {
            setPrevPosition(getPosition());

            // If position changed after walking on newPosition
            // (e.g. walking into portal)
            if (!getPosition().translateBy(getMovementDirection()).equals(newPosition)) {
                Position newerPosition = getPosition().translateBy(getMovementDirection());
                if (checkMovable(newerPosition, dungeon)) {
                    setPosition(newerPosition);
                }
            } else {
                setPosition(newPosition);
            }
        } else {
            for (Entities e : dungeon.getEntitiesOnTile(getPosition())) {
                // Walking on spot, call walkedOn for entities on current position
                if (e instanceof WalkedOn) {
                    WalkedOn w = (WalkedOn) e;
                    w.walkedOn(dungeon, this);
                }
            }
        }
    }

    /**
     * @param position
     * @param dungeon
     * @return boolean
     */
    @Override
    public boolean checkMovable(Position position, Dungeon dungeon) {
        for (Entities e : dungeon.getEntitiesOnTile(position)) {
            // Calls walkedOn for entities which should do something if a character wishes
            // to walk on it (e.g. boulder movement, door unlocking)
            if (e instanceof BeforeWalkedOn) {
                BeforeWalkedOn b = (BeforeWalkedOn) e;
                b.walkedOn(dungeon, this);
            }
        }
        for (Entities e : dungeon.getEntitiesOnTile(position)) {
            if (!e.isWalkable()) {
                return false;
            }
        }
        // Player CAN move to given position
        for (Entities e : dungeon.getEntitiesOnTile(position)) {
            // Do what happens when character wants to walk onto entities at
            // target position
            if (e instanceof WalkedOn) {
                WalkedOn w = (WalkedOn) e;
                w.walkedOn(dungeon, this);
            }
        }
        return true;
    }

    /**
     * @return double
     */
    @Override
    public double calculateDamage() {
        double damage = getAttackDamage();
        // One shot enemy if invincible. Weapon durability is not lowered when
        // invincible.
        if (getBuffs(Invincible.class) != null) {
            return getHealth() * 1000;
        }
        for (InventoryItem item : getInventory()) {
            // get first weapon in inventory
            if (item instanceof Weapons) {
                Weapons weapon = (Weapons) item;
                // increase damage
                damage = weapon.calculateDamage(this, damage);
                break;
            }
        }
        return getHealth() * damage;
    }

    /**
     * @param dungeon
     * @param damage
     */
    @Override
    public void takeDamage(Dungeon dungeon, double damage) {
        // No damage taken when invincible. Equipment durability not lowered.
        if (getBuffs(Invincible.class) != null) {
            return;
        }
        boolean armourChecked = false;
        boolean shieldChecked = false;
        Armours armour = null;
        Shields shield = null;
        for (InventoryItem item : getInventory()) {
            // get first armour
            if (item instanceof Armours && !armourChecked) {
                armour = (Armours) item;
                armourChecked = true;
            }
            // get first shield
            if (item instanceof Shields && !shieldChecked) {
                shield = (Shields) item;
                shieldChecked = true;
            }
            if (armourChecked && shieldChecked)
                break;
        }
        // reduce damage if character has armour
        if (armour != null) {
            damage = armour.calculateDamage(this, damage);
        }
        // reduce damage if character has shield
        if (shield != null) {
            damage = shield.calculateDamage(this, damage);
        }
        // lower health
        setHealth(getHealth() - (damage / 10));

        // if character is killed
        if (isKilled()) {
            InventoryItem i = getInventoryItem(TheOneRingItem.class);
            // character revives to full hp if has one ring
            if (i != null) {
                removeInventory(i);
                setHealth(getMaxHealth());
            } else {
                // character dies
                dungeon.getEntities().remove(this);
            }
        }
    }

    /**
     * @param dungeon
     * @param walker
     */
    @Override
    public void walkedOn(Dungeon dungeon, Entities walker) {
        if (walker instanceof Enemy) {
            Battle.battle(this, (Enemy) walker, dungeon);
        }
        return;
    }

    /**
     * @return Position
     */
    public Position getPrevPosition() {
        return prevPosition;
    }

    /**
     * @param prevPosition
     */
    public void setPrevPosition(Position prevPosition) {
        this.prevPosition = prevPosition;
    }

    /**
     * @return Fightable
     */
    public Fightable getInBattleWith() {
        return inBattleWith;
    }

    /**
     * @param inBattleWith
     */
    public void setInBattleWith(Fightable inBattleWith) {
        this.inBattleWith = inBattleWith;
    }
}
