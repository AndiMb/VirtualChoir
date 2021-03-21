/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

import de.oscvev.virtualchoid.videoutilities.VideoUtilities;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirVideoClip extends VirtualChoirObject implements VideoPathProvider {
    
    private static final int UPDATE_PRIORITY = 100;
    public static final String PROP_OFFSET = "offset";
    public static final String PROP_PATH = "path";
    public static final String PROP_ROTATION = "rotation";
    public static final String PROP_STARTTIME = "startTime";
    public static final String PROP_ENDTIME = "endTime";
    public static final String PROP_CLIPLENGTH = "clipLength";

    private Path path;
    private double offset;
    private double rotation;
    private double startTime;
    private double endTime;
    private double clipLength;

    public VirtualChoirVideoClip(String uuid, String name, Path path, VirtualChoir vChoir, boolean addToLookup) {
        super(uuid, name, vChoir, addToLookup);
        vChoir.getLookup().add(this);
        this.path = path;
        this.clipLength = (new VideoUtilities("")).getVideoLength(this.path);
        this.endTime = this.clipLength;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        Path oldPath = this.path;
        this.path = path;
        firePropertyChange(PROP_PATH, oldPath, path);
        setEndTime((new VideoUtilities("")).getVideoLength(this.path));
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        double oldOffset = this.offset;
        this.offset = offset;
        firePropertyChange(PROP_OFFSET, oldOffset, offset);
    }

    @Override
    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        double oldRotation = this.rotation;
        this.rotation = rotation;
        firePropertyChange(PROP_ROTATION, oldRotation, rotation);
    }
    
    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        double oldStartTime = this.startTime;
        this.startTime = Math.max(0.0, startTime);
        firePropertyChange(PROP_STARTTIME, oldStartTime, startTime);
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        double oldEndTime = this.endTime;
        this.endTime = Math.min(endTime, clipLength);
        firePropertyChange(PROP_ENDTIME, oldEndTime, endTime);
    }

    public double getClipLength() {
        return clipLength;
    }

    public void setClipLength(double clipLength) {
        double oldClipLength = this.clipLength;
        this.clipLength = clipLength;
        firePropertyChange(PROP_CLIPLENGTH, oldClipLength, clipLength);
        if (endTime > clipLength){
            setEndTime(clipLength);
        }
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;
    }

    @Override
    public int hashCode() {
        if (path == null){
            return 0;
        }
        return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VirtualChoirVideoClip other = (VirtualChoirVideoClip) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public Path getVideoClipPath() {
        return getPath();
    }
}
