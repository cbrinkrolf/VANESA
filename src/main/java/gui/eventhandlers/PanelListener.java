package gui.eventhandlers;

import gui.MainWindow;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.View;

public class PanelListener implements DockingWindowListener {
    @Override
    public void viewFocusChanged(View arg0, View arg1) {
    }

    @Override
    public void windowAdded(DockingWindow arg0, DockingWindow arg1) {
    }

    @Override
    public void windowClosed(DockingWindow arg0) {
        MainWindow.getInstance().removeView(arg0);
    }

    @Override
    public void windowClosing(DockingWindow arg0) {
    }

    @Override
    public void windowDocked(DockingWindow arg0) {
    }

    @Override
    public void windowDocking(DockingWindow arg0) {
    }

    @Override
    public void windowHidden(DockingWindow arg0) {
    }

    @Override
    public void windowMaximized(DockingWindow arg0) {
    }

    @Override
    public void windowMaximizing(DockingWindow arg0) {
    }

    @Override
    public void windowMinimized(DockingWindow arg0) {
    }

    @Override
    public void windowMinimizing(DockingWindow arg0) {
    }

    @Override
    public void windowRemoved(DockingWindow arg0, DockingWindow arg1) {
    }

    @Override
    public void windowRestored(DockingWindow arg0) {
    }

    @Override
    public void windowRestoring(DockingWindow arg0) {
    }

    @Override
    public void windowShown(DockingWindow arg0) {
        if (arg0 instanceof View) {
            MainWindow.getInstance().setSelectedView((View) arg0);
        }
    }

    @Override
    public void windowUndocked(DockingWindow arg0) {
    }

    @Override
    public void windowUndocking(DockingWindow arg0) {
    }
}
