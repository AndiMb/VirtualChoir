/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

import java.util.ArrayList;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Andreas
 */
public class VirtualChoir extends VirtualChoirObject implements LookupListener{
    
    private static final int UPDATE_PRIORITY = 300;
    
    public static final String PROP_VOICE = "voice";
    
    public static final String PROP_VIDEO = "video";
    
    private final ArrayList<VirtualChoirVoice> voices = new ArrayList<>();
    
    private final ArrayList<VirtualChoirVideo> videos = new ArrayList<>();
    
    private final Lookup.Result<Object> result;
    
    public VirtualChoir(String uid, String name){
        this(uid, name, true);
    }
    
    public VirtualChoir(String uid, String name, boolean addToLookup){
        super(uid, name, null, addToLookup);
        result = this.getLookup().lookupResult(Object.class);
        result.addLookupListener(this);
    }
    
    public void addVoice(VirtualChoirVoice voice){
        voices.add(voice);
        firePropertyChange(PROP_VOICE, null, voice);
    }
    
    public void removeVoice(VirtualChoirVoice voice){
        voices.remove(voice);
        firePropertyChange(PROP_VOICE, voice, null);
    }

    public ArrayList<VirtualChoirVoice> getVoices() {
        return voices;
    }

    public void addVideo(VirtualChoirVideo video){
        videos.add(video);
        firePropertyChange(PROP_VIDEO, null, video);
    }

    public void removeVideo(VirtualChoirVideo video){
        videos.remove(video);
        firePropertyChange(PROP_VIDEO, null, video);
    }
    
    public ArrayList<VirtualChoirVideo> getVideos(){
        return videos;
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.firePropertyChange("Lookup", null, this.getLookup());
    }

    @Override
    public VirtualChoir getVirtualChoir() {
        return this;
    }
}
