package com.jsql.view.swing.panel.address;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.panel.PanelAddressBar;

public class ActionStart implements ActionListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    protected PanelAddressBar panelAddressBar;
    
    public ActionStart(PanelAddressBar panelAddressBar) {
        
        this.panelAddressBar = panelAddressBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // No injection running
        if (this.panelAddressBar.getAddressMenuBar().getButtonInUrl().getState() == StateButton.STARTABLE) {
            this.startInjection();

        // Injection currently running, stop the process
        } else if (this.panelAddressBar.getAddressMenuBar().getButtonInUrl().getState() == StateButton.STOPPABLE) {
            this.stopInjection();
        }
    }
    
    protected void startInjection() {
        
        int option = 0;
        
        // Ask the user confirmation if injection already built
        if (MediatorModel.model().isInjectionAlreadyBuilt()) {
            
            // Fix #33930: ClassCastException on showConfirmDialog()
            // Implementation by sun.awt.image
            try {
                option = JOptionPane.showConfirmDialog(
                    null,
                    I18n.valueByKey("DIALOG_NEW_INJECTION_TEXT"),
                    I18n.valueByKey("DIALOG_NEW_INJECTION_TITLE"),
                    JOptionPane.OK_CANCEL_OPTION
                );
            } catch (ClassCastException e) {
                LOGGER.error(e, e);
            }
        }

        // Then start injection
        if (!MediatorModel.model().isInjectionAlreadyBuilt() || option == JOptionPane.OK_OPTION) {
            
            this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setToolTipText(I18n.valueByKey("BUTTON_STOP_TOOLTIP"));
            this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setInjectionRunning();
            this.panelAddressBar.getAddressMenuBar().getLoader().setVisible(true);

            // Erase everything in the view from a previous injection
            Request requests = new Request();
            requests.setMessage(Interaction.RESET_INTERFACE);
            MediatorModel.model().sendToViews(requests);

            MediatorModel.model().controlInput(
                this.panelAddressBar.getTextFieldAddress().getText().trim(),
                this.panelAddressBar.getTextFieldRequest().getText().trim(),
                this.panelAddressBar.getTextFieldHeader().getText().trim(),
                this.panelAddressBar.getMethodInjection(),
                this.panelAddressBar.getRequestPanel().getTypeRequest(),
                false
            );
        }
    }
    
    private void stopInjection() {
        
        this.panelAddressBar.getAddressMenuBar().getLoader().setVisible(false);
        this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setInjectionStopping();
        this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setToolTipText(I18n.valueByKey("BUTTON_STOPPING_TOOLTIP"));
        
        MediatorModel.model().setIsStoppedByUser(true);
    }
}