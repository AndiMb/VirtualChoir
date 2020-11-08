/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices;

import de.oscvev.virtualchoid.videoutilities.VideoUtilities;
import de.oscvev.virtualchoir.core.VideoPathProvider;
import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreas
 */
public class DefaultVoice extends VirtualChoirVoice implements VideoPathProvider{

    private static final int UPDATE_PRIORITY = 200;
    public static final String PROP_MASTERFILE = "masterFile";
    public static final String PROP_VIDEOFILES = "videoFiles";

    private VirtualChoirVideoClip masterFile;
    private final List<VirtualChoirVideoClip> videoFiles = new ArrayList<>();

    public DefaultVoice(String uuid, String name, VirtualChoir vChoir) {
        super(uuid, name, vChoir, true);
    }

    public DefaultVoice(String uuid, String name, VirtualChoir vChoir, boolean addToLookup) {
        super(uuid, name, vChoir, addToLookup);
    }

    public VirtualChoirVideoClip getMasterFile() {
        return masterFile;
    }

    public void setMasterFile(VirtualChoirVideoClip masterFile) {
        VirtualChoirVideoClip oldMasterFile = this.masterFile;
        this.masterFile = masterFile;
        firePropertyChange(PROP_MASTERFILE, oldMasterFile, masterFile);
    }

    public List<VirtualChoirVideoClip> getVideoFiles() {
        return videoFiles;
    }

    public void addVideoFile(VirtualChoirVideoClip videoFile) {
        if (!videoFiles.contains(videoFile)) {
            videoFiles.add(videoFile);
            firePropertyChange(PROP_VIDEOFILES, null, videoFiles);
        }
    }

    public void addVideoFiles(List<VirtualChoirVideoClip> videoFiles) {
        for (VirtualChoirVideoClip v : videoFiles){
            if (!this.videoFiles.contains(v)) {
                this.videoFiles.add(v);
            }
        }
        firePropertyChange(PROP_VIDEOFILES, null, this.videoFiles);
    }

    public void removeVideoFils(VirtualChoirVideoClip videoFile) {
        videoFiles.remove(videoFile);
        firePropertyChange(PROP_VIDEOFILES, null, videoFiles);
    }

    public void removeVideoFiles(List<VirtualChoirVideoClip> videoFiles) {
        this.videoFiles.removeAll(videoFiles);
        firePropertyChange(PROP_VIDEOFILES, null, videoFiles);
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;
    }

    @Override
    public Path getVideoClipPath() {
        if (masterFile == null){
            return null;
        }
        return masterFile.getPath();
    }
}
