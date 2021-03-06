package Entities.movingEntities;

import java.util.ArrayList;
import java.util.List;

import Entities.Entities;
import Entities.staticEntities.Boulder;
import dungeonmania.Dungeon;
import dungeonmania.Buffs.Invincible;
import dungeonmania.Buffs.Invisible;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Spider extends SpawningEntities implements Portalable {
    private List<Position> spiderMovementPositions;
    private int index = -1;
    private int increment = 1;

    /**
     * @return int
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @param index
     */
    public void setIndex(int index) {
        this.index = Math.floorMod(index, spiderMovementPositions.size());
    }

    public void resetIndex() {
        this.index = -1;
    }

    public Spider(String id, Position position) {
        super(id, "spider", position, false, true, 30, 1);
        setSpiderMovementPositions(getSpiderMovement(position));
    }

    /**
     * @param index
     * @return Position
     */
    public Position getSpiderMovementPosition(int index) {
        int newIndex = Math.floorMod(index, spiderMovementPositions.size());
        return spiderMovementPositions.get(newIndex);
    }

    /**
     * @param spiderMovementPositions
     */
    public void setSpiderMovementPositions(List<Position> spiderMovementPositions) {
        this.spiderMovementPositions = spiderMovementPositions;
    }

    public void reverseIncrement() {
        this.increment *= -1;
    }

    /**
     * 
     * For a spider, it can traverse through anywhere
     * 
     * @param position
     * @param controller
     * @return boolean
     */
    @Override
    public boolean checkMovable(Position position, Dungeon dungeon) {
        for (Entities e : dungeon.getEntitiesOnTile(position)) {
            // && !(e instanceof Spider) ?
            if (e instanceof Boulder || (isMovingEntityButNotCharacter(e) && !this.equals(e))) {
                // If position isn't walkable OR another moving entity (e.g. spider)
                return false;
            }
        }
        return true;
    }

    /**
     * Takes starting position and calculates the next movement of this spider.
     * Reverses direction if it encounters a boulder or a moving entity other than
     * player.
     * 
     * @param startingPosition
     * @param spider
     */
    @Override
    public void makeMovement(Dungeon dungeon) {
        Invincible invin = null;
        // invisible has higher priority
        if (dungeon.getCharacter().getBuffs(Invisible.class) == null) {
            invin = (Invincible) dungeon.getCharacter().getBuffs(Invincible.class);
        }
        if (invin != null) {
            invin.invinMovement(dungeon, this);
            setSpiderMovementPositions(getSpiderMovement(getPosition()));
            resetIndex();
            this.increment = 1;
            return;
        }
        Position spiderPosition = getPosition();
        // The general movement of the spider is to go up then circles around the
        // starting position
        Position newPosition = getSpiderMovementPosition(getIndex() + increment);
        if (!checkMovable(newPosition, dungeon)) {
            // If blocked, attempt to go backwards
            reverseIncrement();
            // scenario where the first movement is blocked, the spider will go down and
            // rotate anti-clockwise
            if (getIndex() == -1) {
                newPosition = getSpiderMovementPosition(4);
                // checkMovable for reverse position
                if (checkMovable(newPosition, dungeon)) {
                    walkOn(newPosition, dungeon);
                    setIndex(4);
                } else {
                    // if the position is also blocked, it goes back to original direction and does
                    // not move
                    newPosition = getPosition();
                    walkOn(newPosition, dungeon);
                    reverseIncrement();
                }
            } else {
                // spider would
                newPosition = getSpiderMovementPosition(getIndex() + increment);
                if (checkMovable(newPosition, dungeon)) {
                    walkOn(newPosition, dungeon);
                    setIndex(getIndex() + increment);
                } else {
                    // if the position is also blocked, it goes back to original direction and does
                    // not move
                    newPosition = getPosition();
                    walkOn(newPosition, dungeon);
                    reverseIncrement();
                }
            }
        } else {
            walkOn(newPosition, dungeon);
            setIndex(getIndex() + increment);
        }

        // If position changed after walking on newPosition (e.g. walking into portal)
        Position positionBetween = Position.calculatePositionBetween(newPosition, spiderPosition);
        if (positionBetween.getX() != 0) {
            setMovementDirection(getDirection(positionBetween.getX(), "x"));
        } else if (positionBetween.getY() != 0) {
            setMovementDirection(getDirection(positionBetween.getY(), "y"));
        } else {
            setMovementDirection(Direction.NONE);
        }

        if (getPosition().translateBy(getMovementDirection()).equals(newPosition)) {
            setPosition(newPosition, dungeon);
        } else {
            Position newerPosition = getPosition().translateBy(getMovementDirection()).asLayer(2);
            if (checkMovable(newerPosition, dungeon)) {
                walkOn(newerPosition, dungeon);
                setPosition(newerPosition, dungeon);
            }
            setSpiderMovementPositions(getSpiderMovement(getPosition()));
            resetIndex();
            this.increment = 1;
        }

    }

    /**
     * @param startingPosition
     * @return List<Position>
     */
    // Return Adjacent positions in an array list with the following element
    // positions:
    // 0 1 2
    // 7 p 3
    // 6 5 4
    public List<Position> getSpiderMovement(Position startingPosition) {
        int x = startingPosition.getX();
        int y = startingPosition.getY();
        List<Position> spiderMovementPositions = new ArrayList<>();
        spiderMovementPositions.add(new Position(x, y - 1, 2));
        spiderMovementPositions.add(new Position(x + 1, y - 1, 2));
        spiderMovementPositions.add(new Position(x + 1, y, 2));
        spiderMovementPositions.add(new Position(x + 1, y + 1, 2));
        spiderMovementPositions.add(new Position(x, y + 1, 2));
        spiderMovementPositions.add(new Position(x - 1, y + 1, 2));
        spiderMovementPositions.add(new Position(x - 1, y, 2));
        spiderMovementPositions.add(new Position(x - 1, y - 1, 2));

        return spiderMovementPositions;
    }

}
