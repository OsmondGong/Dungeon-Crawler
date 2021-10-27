package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import Entities.movingEntities.Spider;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SpiderTest {

    @Test
    public void testSpawnRandomLocation() {
        // Create a new game

        DungeonManiaController controller = new DungeonManiaController();
        controller.clear();

        controller.newGame("advanced", "Peaceful");

        // Spider spider = new Spider("spider1", new Position(0, 0));
         // Position spiderLocation = spider.spawnSpider();
        // assertEquals(spiderLocation, spider.getPosition());
        
        

    }

    @Test
    public void testSpiderMovement() {
        // Create a new game
        DungeonManiaController controller = new DungeonManiaController();
        controller.clear();

        controller.newGame("advanced", "Peaceful");

        Spider spider = new Spider("spider1", new Position(0, 0));
        // Position spiderLocation = spider.spawnSpider();
        // assertEquals(spiderLocation, spider.getPosition());


        // Check that the spider goes up and then circles the position that it spawn in
        controller.tick("",  Direction.UP);
        assertEquals(spider.getPosition(), new Position(spider.getPosition().getX(), spider.getPosition().getY() - 1));
        
        

    }

    @Test
    public void testSpiderTraverseWalls() {
        // Create a new game
        DungeonManiaController controller = new DungeonManiaController();
        controller.clear();
        controller.newGame("advanced", "Peaceful");

        
        

    }

    @Test
    public void testSpiderReversePosition() {
        // Create a new game
        DungeonManiaController controller = new DungeonManiaController();
        controller.clear();
        controller.newGame("advanced", "Peaceful");
        
        

    }


    
}
