/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideo;
import de.oscvev.virtualchoir.filesupport.VirtualChoirVideoLoadSaveHook;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


 @ServiceProvider(service=VirtualChoirVideoLoadSaveHook.class, position = 100)
public class DefaultSplitScreenVideoLoadSaveImpl extends VirtualChoirVideoLoadSaveHook {

    @Override
    public String getClassName() {
        return DefaultSplitScreenVideo.class.getName();
    }

    @Override
    public VirtualChoirVideo load(Element rootElement, VirtualChoir vChoir, String uuid, String name) {
        
        DefaultSplitScreenVideo dVideo = new DefaultSplitScreenVideo(uuid, name, vChoir, true);
        
        String audioPathStr = this.getTagValue(DefaultSplitScreenVideo.PROP_AUDIOPATH, rootElement);
        String videoPathStr = this.getTagValue(DefaultSplitScreenVideo.PROP_VIDEOPATH, rootElement);
        String audioVideoPathStr = this.getTagValue(DefaultSplitScreenVideo.PROP_AUDIOVIDEOPATH, rootElement);
        
        if (!audioPathStr.equals("null") || (new File(audioPathStr)).exists() ){
            dVideo.setAudioPath(Paths.get(audioPathStr));
        }
        if (!videoPathStr.equals("null") || (new File(videoPathStr)).exists() ){
            dVideo.setVideoPath(Paths.get(videoPathStr));
        }
        if (!audioVideoPathStr.equals("null") || (new File(audioVideoPathStr)).exists() ){
            dVideo.setAudioVideoPath(Paths.get(audioVideoPathStr));
        }
        
        org.w3c.dom.Node clipsNode = rootElement.getElementsByTagName("clips").item(0);

        if (clipsNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element videosElement = (Element) clipsNode;

            NodeList clipList = videosElement.getElementsByTagName("clip");
            if (clipList.getLength() > 0) {
                for (int jj = 0; jj < clipList.getLength(); jj++) {
                    org.w3c.dom.Node clipNode = clipList.item(jj);
                    if (clipNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element clipElement = (Element) clipNode;
                        uuid = clipElement.getAttribute("uuid");
                        name = clipElement.getAttribute("name");
                        String audioFileStr = getTagValue(SplitScreenClip.PROP_AUDIOFILE, clipElement);
                        String videoFileStr = getTagValue(SplitScreenClip.PROP_VIDEOFILE, clipElement);
                        boolean useAudio = Boolean.parseBoolean(getTagValue(SplitScreenClip.PROP_USEAUDIO, clipElement));
                        boolean useVideo = Boolean.parseBoolean(getTagValue(SplitScreenClip.PROP_USEVIDEO, clipElement));
                        String clipHash = getTagValue(SplitScreenClip.PROP_CLIPHASH, clipElement);
                        
                        Path audioFile = null;
                        if ((new File(audioFileStr)).exists()){
                            audioFile = Paths.get(audioFileStr);
                        }
                        Path videoFile = null;
                        if ((new File(videoFileStr)).exists()){
                            videoFile = Paths.get(videoFileStr);
                        }
                        
                        if (audioFile != null || videoFile != null){
                            SplitScreenClip clip = new SplitScreenClip(uuid, name, vChoir, true);
                            clip.setAudioFile(audioFile);
                            clip.setVideoFile(videoFile);
                            clip.setClipHash(clipHash);
                            clip.setUseAudio(useAudio && audioFile != null);
                            clip.setUseVideo(useVideo && videoFile != null);
                            dVideo.addClip(clip);
                        }
                    }
                }
            }
        }
        
        return dVideo;
    }

    @Override
    public void store(Document doc, Element rootElement, VirtualChoirVideo video) {
            DefaultSplitScreenVideo dVideo = (DefaultSplitScreenVideo)video;
            addValue(doc, DefaultSplitScreenVideo.PROP_AUDIOPATH, dVideo.getAudioPath() == null ? "null" : dVideo.getAudioPath().toString(), rootElement);
            addValue(doc, DefaultSplitScreenVideo.PROP_VIDEOPATH, dVideo.getVideoPath() == null ? "null" : dVideo.getVideoPath().toString(), rootElement);
            addValue(doc, DefaultSplitScreenVideo.PROP_AUDIOVIDEOPATH, dVideo.getAudioVideoPath() == null ? "null" : dVideo.getAudioVideoPath().toString(), rootElement);
            Element clipsElem = doc.createElement("clips");
            for (SplitScreenClip clip : dVideo.getClips()){
                Element clipElem = doc.createElement("clip");
                
                Attr attr = doc.createAttribute("uuid");
                attr.setValue(clip.getUUID());
                clipElem.setAttributeNode(attr);

                attr = doc.createAttribute("name");
                attr.setValue(clip.getName());
                clipElem.setAttributeNode(attr);
                
                addValue(doc, SplitScreenClip.PROP_AUDIOFILE, clip.getAudioFile().toString(), clipElem);
                addValue(doc, SplitScreenClip.PROP_VIDEOFILE, clip.getVideoFile().toString(), clipElem);
                addValue(doc, SplitScreenClip.PROP_USEAUDIO, Boolean.toString(clip.isUseAudio()), clipElem);
                addValue(doc, SplitScreenClip.PROP_USEVIDEO, Boolean.toString(clip.isUseVideo()), clipElem);
                addValue(doc, SplitScreenClip.PROP_CLIPHASH, clip.getClipHash(), clipElem);
                clipsElem.appendChild(clipElem);
            }
            rootElement.appendChild(clipsElem);
    }
    
}