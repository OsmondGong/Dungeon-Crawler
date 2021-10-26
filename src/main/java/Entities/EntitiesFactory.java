package Entities;

import java.util.UUID;

import Entities.buildableEntities.Bow;
import Entities.buildableEntities.Shield;
import Entities.collectableEntities.consumableEntities.Bomb;
import Entities.collectableEntities.consumableEntities.InvincibilityPotion;
import Entities.collectableEntities.consumableEntities.Key;
import Entities.collectableEntities.equipments.Sword;
import Entities.collectableEntities.materials.Arrow;
import Entities.collectableEntities.materials.Treasure;
import Entities.collectableEntities.materials.Wood;
import Entities.staticEntities.Boulder;
import Entities.staticEntities.Door;
import Entities.staticEntities.Exit;
import Entities.staticEntities.FloorSwitch;
import Entities.staticEntities.Portal;
import Entities.staticEntities.Wall;
import app.data.Data;
import app.data.DataEntities;
import Entities.movingEntities.Character;
import Entities.movingEntities.Mercenary;
import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public class EntitiesFactory {

    public String getNextId() {
        return UUID.randomUUID().toString();
    }


    public Entities creatingEntitiesFactory(EntityResponse entity) {

        return createEntities(entity.getType(), entity.getPosition());

        
    }

    public Entities creatingEntitiesFactory(DataEntities entity) {
        String type = entity.getType();
        if (type.equals("door") || type.equals("key")) {
            return createEntities(entity.getType(), new Position(entity.getX(), entity.getY()), entity.getKey());

            
        } else if (type.equals("portal")) {
            return createEntities(entity.getType(), new Position(entity.getX(), entity.getY()), entity.getColour());


        } else {
            return createEntities(entity.getType(), new Position(entity.getX(), entity.getY()));

        }
    }

    
    
    public Entities createEntities(String type, Position position, String colour) {
        
        Entities newEntity = null;

        if (type.equals("portal")) {
            newEntity = new Portal(getNextId(), type, position, true, colour); // is door interactable?
        } 
        
        return newEntity;
    }

    public Entities createEntities(String type, Position position, int key) {
        
        Entities newEntity = null;

        if (type.equals("door")) {
            newEntity = new Door(getNextId(), type, position, true, key); // is door interactable?
        } else if (type.equals("key")) {
            newEntity = new Key(getNextId(), type, position, false, key); // is key interactable?
        }
        
        return newEntity;
    }

    // public Entities createEntities(String type) {
        
    //     Entities newEntity = null;

    //     if (type.equals("bow")) {
    //         newEntity = new Bow(getNextId(), true); // is bow interactable?
    //     } else if (type.equals("shield")) {
    //         newEntity = new Shield(getNextId(), false); // is shield interactable?
    //     }
        
    //     return newEntity;
    // }
    /**
     * Create entity of given type at given position
     * 
     * @param type
     * @param position
     * @return
     */
    public Entities createEntities(String type, Position position) {
        Entities newEntity = null;

        // TODO Can someone finissh the rest of this lol
        if (type.equals("wall")) {
            newEntity = new Wall(getNextId(), type, position, false);
        } else if (type.equals("bomb")) {
            newEntity = new Bomb(getNextId(), type, position, false);
        }  else if (type.equals("exit")) {
            newEntity = new Exit(getNextId(), type, position, false); // is exit interactable?
        } else if (type.equals("treasure")) {
            newEntity = new Treasure(getNextId(), type, position, true);
        }  else if (type.equals("arrow")) {
            newEntity = new Arrow(getNextId(), type, position, true); // interactable???
        }  else if (type.equals("wood")) {
            newEntity = new Wood(getNextId(), type, position, true); // interactable???
        }else if (type.equals("invincibility_potion")) {
            newEntity = new InvincibilityPotion(getNextId(), type, position, true);
        } else if (type.equals("switch")) {
            newEntity = new FloorSwitch(getNextId(), type, position, true);
        } else if (type.equals("player")) {
            newEntity = new Character(getNextId(), position, true, 100); // What is character health?
        } else if (type.equals("boulder")) {
            newEntity = new Boulder(getNextId(), position, false); // is boulder interctable?
        } else if (type.equals("sword")) {
            newEntity = new Sword(getNextId(), type, position, false); // is sword interctable?
        } else if (type.equals("mercenary")) {
            newEntity = new Mercenary(getNextId(), type, position, true, 100); // What is mecernary health?
        }

        return newEntity;
    }

}