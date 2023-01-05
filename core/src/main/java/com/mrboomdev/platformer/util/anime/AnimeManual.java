package com.mrboomdev.platformer.util.anime;

import java.util.HashMap;
import com.badlogic.gdx.utils.Array;

public class AnimeManual<T> {
    private UpdateListener listener;
    private Array<Object> entities;
    private HashMap<Float, Timecode> timecodes = new HashMap<>();
    public float progress;
    
    public <T>AnimeManual() {
        entities = new Array<Object>();
    }
    
    public AnimeManual addEntity(Object... entities) {
        for(Object entity : entities) {
            this.entities.add(entity);
        }
        return this;
    }
    
    public void update(float delta) {
        progress += delta;
        listener.update(progress, delta, entities);
        for(Timecode timecode : timecodes.values()) {
            if(timecode.time <= progress && !timecode.wasActivated) {
                timecode.listener.update(entities);
                timecode.wasActivated = true;
            }
        }
    }
    
    public AnimeManual addTimecodeListener(float timecode, TimecodeListener listener) {
        timecodes.put(timecode, new Timecode(timecode, listener));
        return this;
    }
    
    public AnimeManual setUpdateListener(UpdateListener listener) {
        this.listener = listener;
        return this;
    }
    
    public class Timecode {
        public float time;
        public TimecodeListener listener;
        public boolean wasActivated = false;
        
        public Timecode(float time, TimecodeListener listener) {
            this.time = time;
            this.listener = listener;
        }
    }
    
    public interface TimecodeListener {
        void update(Array<Object> entries);
    }
    
    public interface UpdateListener {
        void update(float progress, float delta, Array<Object> entries);
    }
}