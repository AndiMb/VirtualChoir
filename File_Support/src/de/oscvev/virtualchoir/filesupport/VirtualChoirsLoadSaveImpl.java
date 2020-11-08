/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import de.oscvev.virtualchoir.core.VirtualChoirVideo;
import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@ServiceProvider(service = LoadSaveHook.class, position = 1)
public class VirtualChoirsLoadSaveImpl extends LoadSaveHook {

    @Override
    public void load(Element rootElement) {

        HashMap<String, VirtualChoirVoiceLoadSaveHook> voiceHooks = new HashMap<>();
        for (VirtualChoirVoiceLoadSaveHook lsh : Lookup.getDefault().lookupAll(VirtualChoirVoiceLoadSaveHook.class)) {
            voiceHooks.put(lsh.getClassName(), lsh);
        }

        HashMap<String, VirtualChoirVideoLoadSaveHook> videoHooks = new HashMap<>();
        for (VirtualChoirVideoLoadSaveHook lsh : Lookup.getDefault().lookupAll(VirtualChoirVideoLoadSaveHook.class)) {
            videoHooks.put(lsh.getClassName(), lsh);
        }

        NodeList virtualChoirList = rootElement.getElementsByTagName("virtualchoir");
        if (virtualChoirList.getLength() > 0) {
            for (int ii = virtualChoirList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node virtualChoirNode = virtualChoirList.item(ii);
                if (virtualChoirNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element virtualChoirElement = (Element) virtualChoirNode;
                    String uuid = virtualChoirElement.getAttribute("uuid");
                    String name = virtualChoirElement.getAttribute("name");

                    VirtualChoir virtualChoir = new VirtualChoir(uuid, name);
                    
                    HashMap<String,VirtualChoirVideoClip> clipsMap = new HashMap<>(); 
                    org.w3c.dom.Node clipsNode = virtualChoirElement.getElementsByTagName("videoclips").item(0);
                    
                    if (clipsNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element clipsElement = (Element) clipsNode;
                        
                        NodeList clipList = clipsElement.getElementsByTagName("videoclip");
                        if (clipList.getLength() > 0) {
                            for (int jj = 0; jj < clipList.getLength(); jj++) {
                                org.w3c.dom.Node clipNode = clipList.item(jj);
                                if (clipNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                    Element clipElement = (Element) clipNode;
                                    uuid = clipElement.getAttribute("uuid");
                                    name = clipElement.getAttribute("name");
                                    
                                    String pathStr = this.getTagValue(VirtualChoirVideoClip.PROP_PATH, clipElement);
                                    double startTime = Double.parseDouble(this.getTagValue(VirtualChoirVideoClip.PROP_STARTTIME, clipElement));
                                    double entTime = Double.parseDouble(this.getTagValue(VirtualChoirVideoClip.PROP_ENDTIME, clipElement));
                                    double offset = Double.parseDouble(this.getTagValue(VirtualChoirVideoClip.PROP_OFFSET, clipElement));
                                    double rotation = Double.parseDouble(this.getTagValue(VirtualChoirVideoClip.PROP_ROTATION, clipElement));
                                    double clipLength = Double.parseDouble(this.getTagValue(VirtualChoirVideoClip.PROP_CLIPLENGTH, clipElement));
                                    
                                    Path path = Paths.get(pathStr);
                                    
                                    if (Files.exists(path)){
                                        VirtualChoirVideoClip videoClip = new VirtualChoirVideoClip(uuid, name, path, virtualChoir, true);
                                        videoClip.setStartTime(startTime);
                                        videoClip.setEndTime(entTime);
                                        videoClip.setClipLength(clipLength);
                                        videoClip.setOffset(offset);
                                        videoClip.setRotation(rotation);
                                        clipsMap.put(uuid, videoClip);
                                    }
                                }
                            }
                        }
                    }

                    org.w3c.dom.Node voicesNode = virtualChoirElement.getElementsByTagName("voices").item(0);

                    if (voicesNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element voicesElement = (Element) voicesNode;

                        NodeList voiceList = voicesElement.getElementsByTagName("voice");
                        if (voiceList.getLength() > 0) {
                            for (int jj = 0; jj < voiceList.getLength(); jj++) {
                                org.w3c.dom.Node voiceNode = voiceList.item(jj);
                                if (voiceNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                    Element voiceElement = (Element) voiceNode;
                                    String classname = voiceElement.getAttribute("class");
                                    
                                    uuid = voiceElement.getAttribute("uuid");
                                    name = voiceElement.getAttribute("name");
                                    
                                    VirtualChoirVoiceLoadSaveHook splsh = voiceHooks.get(classname);
                                    VirtualChoirVoice voice = splsh.load(voiceElement, virtualChoir, uuid, name, clipsMap);

                                    if (voice != null) {
                                        virtualChoir.addVoice(voice);
                                    }
                                }
                            }
                        }
                    }

                    org.w3c.dom.Node videosNode = virtualChoirElement.getElementsByTagName("videos").item(0);

                    if (videosNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element videosElement = (Element) videosNode;

                        NodeList videoList = videosElement.getElementsByTagName("video");
                        if (videoList.getLength() > 0) {
                            for (int jj = 0; jj < videoList.getLength(); jj++) {
                                org.w3c.dom.Node videoNode = videoList.item(jj);
                                if (videoNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                    Element videoElement = (Element) videoNode;
                                    String classname = videoElement.getAttribute("class");
                                    
                                    uuid = videoElement.getAttribute("uuid");
                                    name = videoElement.getAttribute("name");
                                    
                                    VirtualChoirVideoLoadSaveHook sflsh = videoHooks.get(classname);
                                    VirtualChoirVideo video = sflsh.load(videoElement, virtualChoir, uuid, name);

                                    if (video != null) {
                                        virtualChoir.addVideo(video);
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }
        }
    }

    @Override
    public void store(Document doc, Element rootElement) {

        HashMap<String, VirtualChoirVoiceLoadSaveHook> voiceHooks = new HashMap<>();
        for (VirtualChoirVoiceLoadSaveHook lsh : Lookup.getDefault().lookupAll(VirtualChoirVoiceLoadSaveHook.class)) {
            voiceHooks.put(lsh.getClassName(), lsh);
        }

        HashMap<String, VirtualChoirVideoLoadSaveHook> videoHooks = new HashMap<>();
        for (VirtualChoirVideoLoadSaveHook lsh : Lookup.getDefault().lookupAll(VirtualChoirVideoLoadSaveHook.class)) {
            videoHooks.put(lsh.getClassName(), lsh);
        }

        NodeList list = rootElement.getElementsByTagName("virtualchoir");
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                rootElement.removeChild(list.item(ii));
            }
        }

        ArrayList<VirtualChoir> virtualChoirList = new ArrayList<>();
        virtualChoirList.addAll(VirtualChoirLookup.getDefault().lookupAll(VirtualChoir.class));

        for (VirtualChoir vc : virtualChoirList) {
            Element virtualChoir = doc.createElement("virtualchoir");
            Attr attr = doc.createAttribute("uuid");
            attr.setValue(vc.getUUID());
            virtualChoir.setAttributeNode(attr);

            attr = doc.createAttribute("name");
            attr.setValue(vc.getName());
            virtualChoir.setAttributeNode(attr);

            Element voices = doc.createElement("voices");
            for (VirtualChoirVoice vcv : vc.getVoices()) {
                Element vVoice = doc.createElement("voice");
                attr = doc.createAttribute("uuid");
                attr.setValue(vcv.getUUID());
                vVoice.setAttributeNode(attr);

                attr = doc.createAttribute("name");
                attr.setValue(vcv.getName());
                vVoice.setAttributeNode(attr);

                attr = doc.createAttribute("class");
                attr.setValue(vcv.getClass().getName());
                vVoice.setAttributeNode(attr);
                
                VirtualChoirVoiceLoadSaveHook hook = voiceHooks.get(vcv.getClass().getName());
                if (hook != null){
                    hook.store(doc, vVoice, vcv);
                }
                voices.appendChild(vVoice);
            }
            virtualChoir.appendChild(voices);

            Element videos = doc.createElement("videos");
            for (VirtualChoirVideo vcv : vc.getVideos()) {
                Element vVideo = doc.createElement("video");
                attr = doc.createAttribute("uuid");
                attr.setValue(vcv.getUUID());
                vVideo.setAttributeNode(attr);

                attr = doc.createAttribute("name");
                attr.setValue(vcv.getName());
                vVideo.setAttributeNode(attr);

                attr = doc.createAttribute("class");
                attr.setValue(vcv.getClass().getName());
                vVideo.setAttributeNode(attr);
                
                VirtualChoirVideoLoadSaveHook hook = videoHooks.get(vcv.getClass().getName());
                if (hook != null){
                    hook.store(doc, vVideo, vcv);
                }
                videos.appendChild(vVideo);
            }
            virtualChoir.appendChild(videos);
            
            Element clips = doc.createElement("videoclips");
            for (VirtualChoirVideoClip clip : VirtualChoirLookup.getDefault().lookupAll(VirtualChoirVideoClip.class)){
                Element clipElem = doc.createElement("videoclip");
                attr = doc.createAttribute("uuid");
                attr.setValue(clip.getUUID());
                clipElem.setAttributeNode(attr);

                attr = doc.createAttribute("name");
                attr.setValue(clip.getName());
                clipElem.setAttributeNode(attr);
                
                addValue(doc, VirtualChoirVideoClip.PROP_PATH, clip.getPath().toString(), clipElem);
                addValue(doc, VirtualChoirVideoClip.PROP_STARTTIME, Double.toString(clip.getStartTime()), clipElem);
                addValue(doc, VirtualChoirVideoClip.PROP_ENDTIME, Double.toString(clip.getEndTime()), clipElem);
                addValue(doc, VirtualChoirVideoClip.PROP_OFFSET, Double.toString(clip.getOffset()), clipElem);
                addValue(doc, VirtualChoirVideoClip.PROP_ROTATION, Double.toString(clip.getRotation()), clipElem);
                addValue(doc, VirtualChoirVideoClip.PROP_CLIPLENGTH, Double.toString(clip.getClipLength()), clipElem);
                
                clips.appendChild(clipElem);
            }
            virtualChoir.appendChild(clips);

            rootElement.appendChild(virtualChoir);
        }
    }
}
