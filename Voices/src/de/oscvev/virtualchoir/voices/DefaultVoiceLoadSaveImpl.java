/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import de.oscvev.virtualchoir.filesupport.VirtualChoirVoiceLoadSaveHook;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
 @ServiceProvider(service=VirtualChoirVoiceLoadSaveHook.class, position = 100)
public class DefaultVoiceLoadSaveImpl extends VirtualChoirVoiceLoadSaveHook {

    @Override
    public String getClassName() {
        return DefaultVoice.class.getName();
    }

    @Override
    public VirtualChoirVoice load(Element rootElement, VirtualChoir vChoir, String uuid, String name, HashMap<String,VirtualChoirVideoClip> clipsMap) {
        
        if (clipsMap == null){
            return null;
        }
        
        String masterPathStr = this.getTagValue(DefaultVoice.PROP_MASTERFILE, rootElement);
        VirtualChoirVideoClip masterClip = clipsMap.get(masterPathStr);
        
        DefaultVoice dVoice = new DefaultVoice(uuid, name, vChoir);
        dVoice.setMasterFile(masterClip);
        
        List<VirtualChoirVideoClip> clips;
        NodeList clipList = rootElement.getElementsByTagName(DefaultVoice.PROP_VIDEOFILES);
        if (clipList.getLength() > 0) {
            clips = new ArrayList<>(clipList.getLength());
            for (int jj = 0; jj < clipList.getLength(); jj++) {
                org.w3c.dom.Node clipNode = clipList.item(jj).getChildNodes().item(0);
                String clipUUID = clipNode.getNodeValue();
                clips.add(clipsMap.get(clipUUID));
            }
            dVoice.addVideoFiles(clips);
        }
        
        return dVoice;
    }

    @Override
    public void store(Document doc, Element rootElement, VirtualChoirVoice voice) {
            DefaultVoice dVoice = (DefaultVoice)voice;
            addValue(doc, DefaultVoice.PROP_MASTERFILE, dVoice.getMasterFile().getUUID(), rootElement);
            for (VirtualChoirVideoClip clip : dVoice.getVideoFiles()){                
                addValue(doc, DefaultVoice.PROP_VIDEOFILES, clip.getUUID(), rootElement);
            }
    }
    
}
