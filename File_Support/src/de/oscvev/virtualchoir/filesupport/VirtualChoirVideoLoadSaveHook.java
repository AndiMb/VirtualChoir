/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class VirtualChoirVideoLoadSaveHook {
    
    public abstract String getClassName();
    
    public abstract VirtualChoirVideo load(Element rootElement, VirtualChoir vChoir, String uuid, String name);
    
    public abstract void store(Document doc, Element rootElement, VirtualChoirVideo video);

    public String getTagValue(String sTag, Element eElement) {
        NodeList nList = eElement.getElementsByTagName(sTag);
        if (nList.getLength() == 0){
            return null;
        }
        NodeList nlList = nList.item(0).getChildNodes();

        org.w3c.dom.Node nValue = nlList.item(0);

        return nValue.getNodeValue();
    }
    
    public void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
    
}
