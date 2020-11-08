/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

/**
 *
 * @author Andreas
 */
public abstract class VirtualChoirVoice extends VirtualChoirObject {
    
    public VirtualChoirVoice(String uuid, String name, VirtualChoir vChoir, boolean addToLookup) {
        super(uuid, name, vChoir, addToLookup);
    }
}
