/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.videocreator.actions;

import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

public class VideoCreatorWizardWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VideoCreatorWizardVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VideoCreatorWizardVisualPanel1 getComponent() {
        if (component == null) {
            component = new VideoCreatorWizardVisualPanel1();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        component.setWorkingDirectory(NbPreferences.forModule(VideoCreatorWizardWizardPanel1.class).get("workingdirectory", ""));
        component.setClipResolution(Integer.parseInt(NbPreferences.forModule(VideoCreatorWizardWizardPanel1.class).get("clipresolution", "0")));
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty("workingdirectory", getComponent().getWorkingDirectory());
        wiz.putProperty("clipresolution", getComponent().getClipResolution());
        NbPreferences.forModule(VideoCreatorWizardWizardPanel1.class).put("workingdirectory", getComponent().getWorkingDirectory());
        NbPreferences.forModule(VideoCreatorWizardWizardPanel1.class).put("clipresolution", Integer.toString(getComponent().getClipResolution()));
    }

}
