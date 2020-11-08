/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.videocreator.actions;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.videocreator.VideoCreator;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.*;

@ActionID(category = "VirtualChoirVoice",
        id = "de.oscvev.virtualchoir.videocreator.actions.CreateVideoAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/videocreator/resources/createvideo.png",
        displayName = "#CTL_CreateVideoAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoice", position = 100),
    @ActionReference(path = "VirtualChoirActions/VirtualChoir", position = 100)
})
public class CreateVideoAction implements ActionListener {

    private final VirtualChoir virtualChoir;

    public CreateVideoAction(VirtualChoir virtualChoir) {
        this.virtualChoir = virtualChoir;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new VideoCreatorWizardWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(CreateVideoAction.class, "CreateVideoWizard.title"));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            String workingDirectory = (String)wiz.getProperty("workingdirectory");
            int clipResolution = (Integer)wiz.getProperty("clipresolution");
            VideoCreator export = new VideoCreator(virtualChoir, workingDirectory);
            if (clipResolution == VideoCreatorWizardVisualPanel1.CLIPRESOLUTION_1920X1080){
                export.setClipWidth(1920);
                export.setClipHeight(1080);
            }
            RequestProcessor.getDefault().post(export);
        }
    }
}
