package Entities.collectableEntities.equipments;

import Entities.Defence;
import Entities.Entities;
import Entities.InventoryItem;
import Entities.collectableEntities.ConsumableEntity;
import dungeonmania.util.Position;

public class Armour extends ConsumableEntity implements Defence {

    public Armour(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void consumeItem() {
        // TODO Auto-generated method stub
        
    }

}