package dungeonmania;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import Entities.Entities;
import Entities.EntitiesFactory;
import Entities.Interactable;
import Entities.movingEntities.Ally;
import Entities.movingEntities.Assassin;
import Entities.movingEntities.BribedAssassin;
import Entities.movingEntities.BribedMercenary;
import Entities.movingEntities.Character;
import Entities.movingEntities.Enemy;
import Entities.movingEntities.Hydra;
import Entities.movingEntities.Mercenary;
import Entities.movingEntities.Spider;
import Entities.movingEntities.ZombieToast;
import Entities.staticEntities.Exit;
import Entities.staticEntities.FloorSwitch;
import Entities.staticEntities.ZombieToastSpawner;
import Items.InventoryItem;
import Items.ConsumableItem.Consumables;
import Items.Equipments.SceptreItem;
import data.Data;
import dungeonmania.Buffs.AllyBuff;
import dungeonmania.Buffs.Buffs;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.gamemodes.Hard;
import dungeonmania.gamemodes.Peaceful;
import dungeonmania.gamemodes.Standard;
import dungeonmania.goals.AndGoal;
import dungeonmania.goals.BooleanGoalEvaluator;
import dungeonmania.goals.GoalLeaf;
import dungeonmania.goals.GoalNode;
import dungeonmania.goals.OrGoal;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Battle;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import spark.utils.IOUtils;

public class Dungeon {
    private String dungeonId;
    private String dungeonName;
    private ArrayList<Entities> entities;
    private ArrayList<String> buildables;
    private String goals;
    private String gameMode;
    private int ticksCounter;
    private int width;
    private int height;
    private int spawnRate;
    private Integer startTick = null;

    private Random random;

    // Map<String, EntityResponse> entitiesResponse = new ArrayList<>();
    // Map<ItemResponse> inventory = new ArrayList<>();
    // List<String> buildables = new ArrayList<>();

    public Dungeon(String dungeonId, Random random) {
        this.dungeonId = dungeonId;
        this.dungeonName = "";
        this.entities = new ArrayList<Entities>();
        this.buildables = new ArrayList<String>();
        this.goals = "";
        this.gameMode = "";
        ticksCounter = 0;
        this.width = 0;
        this.height = 0;
        this.random = random;
    }

    /**
     * @return Random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @return Character
     */
    public Character getCharacter() {
        for (Entities e : getEntities()) {
            if (e instanceof Character) {
                return (Character) e;
            }
        }
        return null;
    }

    /**
     * @return String
     */
    public String getDungeonId() {
        return this.dungeonId;
    }

    /**
     * @param dungeonId
     */
    public void setDungeonId(String dungeonId) {
        this.dungeonId = dungeonId;
    }

    /**
     * @return String
     */
    public String getDungeonName() {
        return this.dungeonName;
    }

    /**
     * @param dungeonName
     */
    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    /**
     * @return ArrayList<Entities>
     */
    public ArrayList<Entities> getEntities() {
        return entities;
    }

    /**
     * @param entities
     */
    public void setEntities(ArrayList<Entities> entities) {
        this.entities = entities;
    }

    /**
     * @param entity
     */
    public void addEntities(Entities entity) {
        this.entities.add(entity);

    }

    /**
     * @param entity
     */
    public void removeEntities(Entities entity) {
        this.entities.remove(entity);
    }

    /**
     * @return ArrayList<String>
     */
    public ArrayList<String> getBuildables() {
        return this.buildables;
    }

    /**
     * @param buildables
     */
    public void setBuildables(ArrayList<String> buildables) {
        this.buildables = buildables;
    }

    /**
     * @param buildable
     */
    public void addBuildables(String buildable) {
        this.buildables.add(buildable);
    }

    /**
     * @return String
     */
    // public List<Class<? extends BuildableItems>> getBuildableItems() {
    // return this.buildableItems;
    // }

    // public void setBuildableItems(List<Class<? extends BuildableItems>>
    // buildableItems) {
    // this.buildableItems = buildableItems;
    // }

    // public void addBuildablesItems(Class<? extends BuildableItems>
    // buildableItems) {
    // this.buildableItems.add(buildableItems);
    // }

    public int getSpawnRate() {
        return spawnRate;
    }

    /**
     * @param spawnRate
     */
    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }

    /**
     * @return String
     */
    public String getGoals() {
        return this.goals;
    }

    /**
     * @param goals
     */
    public void setGoals(String goals) {
        this.goals = goals;
    }

    /**
     * @return String
     */
    public String getGameMode() {
        return this.gameMode;
    }

    /**
     * @param gameMode
     */
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        if (gameMode.equals("Hard")) {
            Hard.setGameMode(this);
        } else if (gameMode.equals("Standard")) {
            Standard.setGameMode(this);
        } else if (gameMode.equals("Peaceful")) {
            Peaceful.setGameMode(this);
        }
    }

    /**
     * @return int
     */
    public int getTicksCounter() {
        return this.ticksCounter;
    }

    /**
     * @param ticksCounter
     */
    public void setTicksCounter(int ticksCounter) {
        this.ticksCounter = ticksCounter;
    }

    /** 
     * 
     */
    public void incrementTicks() {
        this.ticksCounter++;
    }

    /**
     * @return int
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return int
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param data
     * @throws URISyntaxException
     * @throws IOException
     */
    public void setAllGoals(Data data, JSONObject json) {

        GoalNode res = createParsedExpression(json);
        setGoals(res.toString());

    }

    /**
     * Gets position and returns the entities matching the x and y coordinate
     * (combines all layers)
     * 
     * @param position
     * @return Entities
     */
    public List<Entities> getEntitiesOnTile(Position position) {
        List<Entities> entitiesList = new ArrayList<>();

        entitiesList = getEntities().stream().filter(e -> Objects.nonNull(e))
                .filter(e -> e.getPosition().getX() == position.getX())
                .filter(e -> e.getPosition().getY() == position.getY()).collect(Collectors.toList());

        return entitiesList;
    }

    /**
     * @param itemUsed
     * @param movementDirection
     * @return DungeonResponse after a tick has passed
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse tick(String itemUsedId, Direction movementDirection)
            throws IllegalArgumentException, InvalidActionException {

        incrementTicks(); // This increments the number of ticks in this dungeon

        if (itemUsedId != null && !itemUsedId.equals("")) {
            InventoryItem item = null;
            for (InventoryItem currItem : getCharacter().getInventory()) {
                if (currItem.getId().equals(itemUsedId)) {
                    item = currItem;
                }

            }

            // Checks for whether itemUsed is in inventory
            if (item == null) {
                throw new InvalidActionException(String.format("Character does not have %s in inventory", itemUsedId));
            }

            // Checks for valid itemUsedId
            if (!(item instanceof Consumables || itemUsedId == null)) {
                throw new IllegalArgumentException("itemUsedId provided does not correspond to a bomb or potion");
            }

            Consumables consumable = (Consumables) item;
            consumable.consume(this, getCharacter());
        }

        // Buffs
        List<Buffs> removeBuffs = new ArrayList<Buffs>();
        for (Buffs b : getCharacter().getBuffs()) {
            b.durationEnd(getTicksCounter(), removeBuffs);
        }

        for (Buffs b : removeBuffs) {
            if (b instanceof AllyBuff) {
                AllyBuff allyBuff = (AllyBuff) b;
                for (Entities entity : getEntities()) {
                    if (entity instanceof Ally) {
                        allyBuff.endAllyBuff(this, (Ally) entity);
                        break;

                    }
                }
                // Remove the sceptre from the inventory here
            }
            getCharacter().removeBuff(b);
        }

        // Character movement
        /**
         * check movable then move char if char on entity -> pickup/interact if entity
         * on character -> fight check movable then move entities if entity on character
         * -> fight (if haven't fought yet)
         */
        // Process:
        // Use item
        // Move character
        // Move all movableEntities

        // Move character
        // - Calculate character's next move based on given direction
        // - Check if future position is on entity
        // - If so, check what type of entity
        // - If entity is static, behaviour depends
        // - If entity is wall, character position does not update
        // - If entity is exit, you win
        // - If entity is boulder, you push it
        // - If entity is switch, you can move onto it, nothing happens
        // - ..etc.
        // - If entity is moving, fight
        // - If entity is collectable, move entity to inventory
        // - Check if items in inventory can build buildables, if so, append to
        // buildables
        // - Update character movement

        // Move all movableEntities
        // - Calculate movableEntity's next move
        // - Check if future position is on entity
        // - If so, check what type of entity
        // - Update moveableEntity movement

        // Suggestion
        // - Each entity has a function for when character/entity moves onto itself
        // - For now, move character

        /**
         * Movement order: - Character - Assassin - Mercenary - Zombie Toast - Spider -
         * Hydra
         */

        // Set character movement direction (needed for boulder movement)
        getCharacter().setMovementDirection(movementDirection);

        // Character movement (based on character's movement direction)
        getCharacter().makeMovement(this);

        // Mobs List
        List<Assassin> aList = new ArrayList<Assassin>();
        List<Mercenary> mList = new ArrayList<Mercenary>();
        List<ZombieToast> zList = new ArrayList<ZombieToast>();
        List<Spider> sList = new ArrayList<Spider>();
        List<Hydra> hList = new ArrayList<Hydra>();
        List<BribedAssassin> baList = new ArrayList<BribedAssassin>();
        List<BribedMercenary> bList = new ArrayList<BribedMercenary>();

        for (Entities e : getEntities()) {
            if (e instanceof Assassin) {
                Assassin a = (Assassin) e;
                aList.add(a);
            } else if (e instanceof Mercenary) {
                Mercenary m = (Mercenary) e;
                mList.add(m);
            } else if (e instanceof ZombieToast) {
                ZombieToast z = (ZombieToast) e;
                zList.add(z);
            } else if (e instanceof Spider) {
                Spider s = (Spider) e;
                sList.add(s);
            } else if (e instanceof Hydra) {
                Hydra h = (Hydra) e;
                hList.add(h);
            } else if (e instanceof BribedAssassin) {
                BribedAssassin b = (BribedAssassin) e;
                baList.add(b);
            } else if (e instanceof BribedMercenary) {
                BribedMercenary b = (BribedMercenary) e;
                bList.add(b);
            }
        }

        // Assassin
        for (Assassin a : aList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            a.makeMovement(this);
        }
        // Mercenary
        for (Mercenary m : mList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            m.makeMovement(this);
        }
        // Zombie Toast
        for (ZombieToast z : zList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            z.makeMovement(this);
        }
        // Spider
        for (Spider s : sList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            s.makeMovement(this);
        }
        // Hydra
        for (Hydra h : hList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            h.makeMovement(this);
        }
        // Bribed assassin
        for (BribedAssassin ba : baList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            ba.makeMovement(this);
        }
        // Bribed mercenary
        for (BribedMercenary b : bList) {
            if (getCharacter() == null)
                return newDungeonResponse();
            b.makeMovement(this);
        }

        if (getCharacter() == null)
            return newDungeonResponse();

        spawnEnemies(getRandom()); // Spawn Enemies
        if (hasCompletedGoals()) {
            gameCompleted();
        }

        // clear list that stores who character have fought in the current tick
        Battle.clearBattleEnemies();

        // Temporary, store responses and change necessary responses only
        return newDungeonResponse();
    }

    /**
     * @param entityId
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Interactable i = null;
        // get entity if interactible
        for (Entities e : getEntities()) {
            if (e.getId().equals(entityId)) {
                if (e instanceof Interactable) {
                    i = (Interactable) e;
                }
                break;
            }
        }
        // if entity isn't interactable, throw error
        if (i == null) {
            throw new IllegalArgumentException("Object is not interactable!!");
        }
        // interact with interactable
        i.interact(this);
        return newDungeonResponse();
    }

    /**
     * @return DungeonResponse
     */
    public DungeonResponse newDungeonResponse() {
        List<EntityResponse> entitiesResponses = new ArrayList<>();
        List<ItemResponse> inventoryResponses = new ArrayList<>();
        List<String> buildablesResponses = new ArrayList<>();

        for (Entities entity : getEntities()) {
            if (entity != null) {
                entitiesResponses.add(new EntityResponse(entity.getId(), entity.getType(), entity.getPosition(),
                        entity.isInteractable()));
            }

        }

        if (getCharacter() != null) {
            for (InventoryItem inventoryItem : getCharacter().getInventory()) {
                if (inventoryItem != null) {

                    inventoryResponses.add(new ItemResponse(inventoryItem.getId(), inventoryItem.getType()));
                }
            }
        }

        for (String builds : getBuildables()) {
            buildablesResponses.add(builds);
        }

        return new DungeonResponse(getDungeonId(), getDungeonName(), entitiesResponses, inventoryResponses,
                buildablesResponses, getGoals());
    }

    /**
     * @param gameMode
     * @param height
     * @param width
     */
    public void spawnEnemies(Random random) {
        if (getTicksCounter() == 0 || spawnRate == 0) {
            return;
        }

        // Spider
        if (getTicksCounter() % 25 == 0) {
            Entities spider = EntitiesFactory.createEntities("spider",
                    new Position(random.nextInt(getWidth()), random.nextInt(getHeight()), 2));
            addEntities(spider);
        }

        // Zombie Toast
        if (getTicksCounter() % spawnRate == 0) {
            for (Entities entity : getEntities()) {
                if (entity instanceof ZombieToastSpawner) {
                    ZombieToastSpawner zombieToastSpawner = (ZombieToastSpawner) entity;
                    Entities zombieToast = zombieToastSpawner.spawnZombies(this);
                    // zombieToast = null if no cardianlly adjacent open square
                    if (zombieToast != null)
                        addEntities(zombieToast);
                    break;
                }
            }
        }

        // Hydra
        if (getGameMode().equals("Hard")) {
            if (getTicksCounter() % 50 == 0) {
                Entities hydra = EntitiesFactory.createEntities("hydra",
                        new Position(random.nextInt(getWidth()), random.nextInt(getHeight()), 2));
                addEntities(hydra);
            }
        }

        boolean enemyExists = false;
        for (Entities entity : getEntities()) {
            if (entity instanceof Enemy)
                enemyExists = true;
        }
        if (enemyExists) {
            if (startTick == null) {
                startTick = getTicksCounter() - 1;
            } else if (getTicksCounter() == startTick + 30) {
                // mercenary/assassin spawns every 30 tick if there are enemies on the field
                int randomInt = random.nextInt(100);
                // assassin spawn chance is 20%
                if (randomInt <= 20) {
                    Entities assassin = EntitiesFactory.createEntities("assassin", getCharacter().getSpawnPos());
                    addEntities(assassin);
                    startTick = null;
                } else {
                    Entities mercenary = EntitiesFactory.createEntities("mercenary", getCharacter().getSpawnPos());
                    addEntities(mercenary);
                    startTick = null;
                }
            }
        } else {
            startTick = null;
        }

    }

    /**
     * This reads the individual goals and checks whether it has been completed
     * 
     * @return Boolean
     */
    public Boolean hasCompletedGoals() {

        try {
            String fileName = "src/main/resources/dungeons/" + dungeonName + ".json";
            InputStream is = new FileInputStream(fileName);
            String jsonTxt = IOUtils.toString(is);
            JSONObject json = new JSONObject(jsonTxt);
            GoalNode res = createParsedExpression(json.getJSONObject("goal-condition"));
            return BooleanGoalEvaluator.evaluate(res);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param json
     * @return GoalNode
     */
    public GoalNode createParsedExpression(JSONObject json) {

        List<String> inventoryTypes = new ArrayList<>();

        if (getCharacter() != null && getCharacter().getInventory() != null) {
            inventoryTypes = getCharacter().getInventory().stream().filter(i -> Objects.nonNull(i))
                    .map((item) -> item.getType()).collect(Collectors.toList());
        }

        String nodeType = json.getString("goal");
        JSONArray subgoals = null;
        JSONObject goal1 = null;
        JSONObject goal2 = null;
        if (json.has("subgoals")) {
            subgoals = json.getJSONArray("subgoals");
            goal1 = subgoals.getJSONObject(0);
            goal2 = subgoals.getJSONObject(1);
        }
        GoalNode current = null;
        if (nodeType.equals("AND")) {
            current = new AndGoal(createParsedExpression(goal1), createParsedExpression(goal2));

        } else if (nodeType.equals("OR")) {
            current = new OrGoal(createParsedExpression(goal1), createParsedExpression(goal2));
        } else {
            current = new GoalLeaf(checkIndividualGoals(json.getString("goal"), inventoryTypes),
                    json.getString("goal"));
        }
        return current;

    }

    /**
     * This individually checks all of the goals met by the given goal type and
     * returns a boolean
     * 
     * @param goal
     * @param inventoryTypes
     * @param goalsList
     * @return Boolean
     */
    public Boolean checkIndividualGoals(String goal, List<String> inventoryTypes) {
        if (getCharacter() == null) {
            return false;
        }
        switch (goal.toLowerCase()) {
        case "exit":
            List<Entities> entitiesAtPosition = getEntitiesOnTile(getCharacter().getPosition());
            for (Entities entity : entitiesAtPosition) {
                if (entity instanceof Exit) {
                    return true;
                }
            }
            return false;
        case "enemies":
            List<Entities> enemies = getEntities().stream().filter(e -> Objects.nonNull(e))
                    .filter((entity) -> entity instanceof Enemy).collect(Collectors.toList());

            if (enemies.isEmpty()) {
                return true;
            }
            return false;
        case "boulders":
            // Check all switches have a boulder on it
            for (Entities entity : getEntities()) {
                if (entity instanceof FloorSwitch) {
                    List<Entities> entityAtPosition = getEntitiesOnTile(entity.getPosition());
                    List<Entities> bouldersOnSwitch = entityAtPosition.stream().filter(e -> Objects.nonNull(e))
                            .filter((entityOnTile) -> entityOnTile.getType().equals("boulder"))
                            .collect(Collectors.toList());
                    if (bouldersOnSwitch.isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        case "treasure":
            if (inventoryTypes.contains("treasure")) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void gameCompleted() {
        // If you stop returning any goals (i.e. empty string) it'll say the game has
        // been completed
        setGoals("");
    }

    /**
     * @return List<Integer>
     */
    // returns x and y borders where entity exist on map
    // order starts from up and goes clockwise
    public List<Integer> getBorders() {
        List<Integer> borders = new ArrayList<Integer>();
        Integer upBorder = null;
        Integer rightBorder = null;
        Integer downBorder = null;
        Integer leftBorder = null;
        for (Entities e : getEntities()) {
            Position ePos = e.getPosition();
            if (upBorder == null) {
                upBorder = ePos.getY();
                rightBorder = ePos.getX();
                downBorder = ePos.getY();
                leftBorder = ePos.getX();
            } else {
                if (upBorder > ePos.getY())
                    upBorder = ePos.getY();
                if (rightBorder < ePos.getX())
                    rightBorder = ePos.getX();
                if (downBorder < ePos.getY())
                    downBorder = ePos.getY();
                if (leftBorder > ePos.getX())
                    leftBorder = ePos.getX();
            }
        }
        // border is made an extra tile wider so that dijkstra calculates around
        // entities
        borders.add(upBorder - 1);
        borders.add(rightBorder + 1);
        borders.add(downBorder + 1);
        borders.add(leftBorder - 1);
        return borders;
    }

}
