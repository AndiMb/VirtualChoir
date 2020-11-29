/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo;

import de.oscvev.virtualchoir.core.VideoPathProvider;
import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirObject;
import java.nio.file.Path;

/**
 *
 * @author Andreas
 */
public class SplitScreenClip extends VirtualChoirObject implements VideoPathProvider{

    public static final String PROP_VIDEOFILE = "videoFile";
    public static final String PROP_AUDIOFILE = "audioFile";
    public static final String PROP_USEAUDIO = "useAudio";
    public static final String PROP_USEVIDEO = "useVideo";
    public static final String PROP_CLIPHASH = "clipHash";
    public static final String PROP_MASTERFILE = "masterfile";
    public static final String PROP_CORRELATIONCOEFFICIENT = "correlationCoefficient";

    private Path videoFile;
    private Path audioFile;
    private boolean useAudio;
    private boolean useVideo;
    private String clipHash;
    private Path masterfile;
    private double correlationCoefficient;
    
    public SplitScreenClip(String uuid, String name, VirtualChoir vChoir, boolean addToLookup) {
        super(uuid, name, vChoir, addToLookup);
    }
    
    public String getClipHash() {
        return clipHash;
    }

    public void setClipHash(String clipHash) {
        String oldClipHash = this.clipHash;
        this.clipHash = clipHash;
        firePropertyChange(PROP_CLIPHASH, oldClipHash, clipHash);
    }

    public boolean isUseVideo() {
        return useVideo;
    }

    public void setUseVideo(boolean useVideo) {
        boolean oldUseVideo = this.useVideo;
        this.useVideo = useVideo;
        firePropertyChange(PROP_USEVIDEO, oldUseVideo, useVideo);
    }


    public boolean isUseAudio() {
        return useAudio;
    }

    public void setUseAudio(boolean useAudio) {
        boolean oldUseAudio = this.useAudio;
        this.useAudio = useAudio;
        firePropertyChange(PROP_USEAUDIO, oldUseAudio, useAudio);
    }


    public Path getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(Path audioFile) {
        Path oldAudioFile = this.audioFile;
        this.audioFile = audioFile;
        firePropertyChange(PROP_AUDIOFILE, oldAudioFile, audioFile);
    }


    public Path getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(Path videoFile) {
        Path oldVideoFile = this.videoFile;
        this.videoFile = videoFile;
        firePropertyChange(PROP_VIDEOFILE, oldVideoFile, videoFile);
    }

    @Override
    public int getUpdatePriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Path getVideoClipPath() {
        Path returnPath = null;
        if (useVideo){
            returnPath = videoFile;
        }else if (useAudio){
            returnPath = audioFile;
        }
        return returnPath;
    }

    public Path getMasterfile() {
        return masterfile;
    }

    public void setMasterfile(Path masterfile) {
        Path oldMasterfile = this.masterfile;
        this.masterfile = masterfile;
        firePropertyChange(PROP_MASTERFILE, oldMasterfile, masterfile);
    }

    public double getCorrelationCoefficient() {
        return correlationCoefficient;
    }

    public void setCorrelationCoefficient(double correlationCoefficient) {
        double oldCorrelationCoefficient = this.correlationCoefficient;
        this.correlationCoefficient = correlationCoefficient;
        firePropertyChange(PROP_CORRELATIONCOEFFICIENT, oldCorrelationCoefficient, correlationCoefficient);
    }
}
