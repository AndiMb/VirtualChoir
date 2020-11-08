/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport.actions;

import de.oscvev.virtualchoir.filesupport.NewFileCreator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

@ActionID(category = "File",
        id = "de.oscvev.virtualchoir.filesupport.actions.NewFileAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/filesupport/resources/filenew.png",
        displayName = "#CTL_NewFileAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = -100),
    @ActionReference(path = "Toolbars/File", position = -100)
})
public final class NewFileAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        NewFileCreator.create();
    }
}
