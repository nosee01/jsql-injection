/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import com.jsql.view.GUI;

/**
 * End the refreshing of File search button
 */
public class EndUpload implements Interaction{
    // The main View
    private GUI gui;

    /**
     * @param mainGUI
     * @param interactionParams
     */
    public EndUpload(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        gui.getOutputPanel().uploadManager.restoreButtonText();
        gui.getOutputPanel().uploadManager.setButtonEnable(true);
        gui.getOutputPanel().uploadManager.hideLoader();
    }
}
