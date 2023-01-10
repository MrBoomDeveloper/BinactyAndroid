package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.environment.FreePosition;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.environment.path.PositionPoint;

public class BotEntity extends PlayerEntity {
    public BotEntity(String name, String character, World world) {
        super(name, character, world);
    }
    
    public void doAiStuff(PlayerEntity player, MapManager map) {
        Vector2 power = player.body.getPosition().sub(body.getPosition());
        float distance = player.body.getPosition().dst(body.getPosition());
        
        String botTextPosition = PositionPoint.toText(body.getPosition());
        if(map.aiZones.containsKey(botTextPosition)) {
          String playerTextPosition = PositionPoint.toText(player.body.getPosition());
          if(map.aiZones.containsKey(playerTextPosition)) {
            GraphPath<FreePosition> path = map.positionGraph.findPath(map.aiZones.get(botTextPosition), map.aiZones.get(playerTextPosition));
            try {
              power = new Vector2(path.get(2).x, path.get(2).y).sub(body.getPosition()).scl(10).limit(4);
            } catch(Exception e) {try {
              power = new Vector2(path.get(1).x, path.get(1).y).sub(body.getPosition()).scl(10).limit(4);
            } catch(Exception error) {}}
          }
       }
       
       body.setLinearVelocity(power.scl(2).limit(4));
       if(distance > 10) {
           body.setLinearVelocity(new Vector2(0, 0));
       } else if(distance < 3) {
           power = player.body.getPosition().sub(body.getPosition());
           body.setLinearVelocity(power.scl(2).limit(4));
       }
    }
}