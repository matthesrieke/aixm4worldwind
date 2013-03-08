/**
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.worldwind;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.ToolTipController;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.xmlbeans.XmlException;

public class WorldWindMap extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws XmlException, IOException {
		
		new WorldWindMap().initialize(true, false);
	}

	private AppPanel wwjPanel;
	private LayerPanel layerPanel;
	private DemoAirspaceLayer demoLayer;

	protected void initialize(boolean includeStatusBar, boolean includeLayerPanel) throws XmlException, IOException
	{
		// Create the WorldWindow.
		this.wwjPanel = new AppPanel(new Dimension(1200, 700), true);

		for (Layer layer : wwjPanel.getWwd().getModel().getLayers()) {
			if (layer.getName().equals("Atmosphere") || layer.getName().equals("Stars") ||
					layer.getName().contains("Marble") || layer.getName().equals("World Map") ||
					layer.getName().equals("Scale bar") || layer.getName().equals("Compass") ||
					layer.getName().contains("cubed")) {
				
			} else {
				layer.setEnabled(false);
			}
		}
		
		// Put the pieces together.
		this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
		if (includeLayerPanel)
		{
			this.layerPanel = new LayerPanel(this.wwjPanel.getWwd(), null);
			this.getContentPane().add(this.layerPanel, BorderLayout.WEST);
		}


		// Create and install the view controls layer and register a controller for it with the World Window.
		ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
		insertBeforeCompass(wwjPanel.getWwd(), viewControlsLayer);
		this.wwjPanel.getWwd().addSelectListener(new ViewControlsSelectListener(this.wwjPanel.getWwd(), viewControlsLayer));

		// Register a rendering exception listener that's notified when exceptions occur during rendering.
		this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener()
		{
			public void exceptionThrown(Throwable t)
			{
				if (t instanceof WWAbsentRequirementException)
				{
					String message = "Computer does not meet minimum graphics requirements.\n";
					message += "Please install up-to-date graphics driver and try again.\n";
					message += "Reason: " + t.getMessage() + "\n";
					message += "This program will end when you press OK.";

					JOptionPane.showMessageDialog(WorldWindMap.this, message, "Unable to Start Program",
							JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			}
		});

		// Search the layer list for layers that are also select listeners and register them with the World
		// Window. This enables interactive layers to be included without specific knowledge of them here.
		for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers())
		{
			if (layer instanceof SelectListener)
			{
				this.wwjPanel.getWwd().addSelectListener((SelectListener) layer);
			}
		}

		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Center the application on the screen.
		WWUtil.alignComponent(null, this, AVKey.CENTER);
		this.setResizable(true);
		this.setVisible(true);
		
		demoLayer = new DemoAirspaceLayer();
		wwjPanel.getWwd().getModel().getLayers().add(demoLayer);
		
		zoomToDefault();
	}


	private void zoomToDefault() {

		Globe globe = wwjPanel.getWwd().getModel().getGlobe();
		double ve = wwjPanel.getWwd().getSceneController().getVerticalExaggeration();

		Extent extent = demoLayer.getAirspace().getExtent(globe, ve);

		Angle fov = wwjPanel.getWwd().getView().getFieldOfView();

		double zoom = extent.getRadius() / (fov.tanHalfAngle() * fov.cosHalfAngle());
		Position centerPos =  Position.fromDegrees(52.0, 7.0, zoom);

		wwjPanel.getWwd().getView().goTo(centerPos, zoom*2);
	}


	public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
	{
		// Insert the layer into the layer list just before the compass.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof CompassLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

	public static class AppPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected WorldWindow wwd;
		protected StatusBar statusBar;
		protected ToolTipController toolTipController;
		protected HighlightController highlightController;

		public AppPanel(Dimension canvasSize, boolean includeStatusBar)
		{
			super(new BorderLayout());

			this.wwd = this.createWorldWindow();
			((Component) this.wwd).setPreferredSize(canvasSize);

			// Create the default model as described in the current worldwind properties.
			Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
			this.wwd.setModel(m);

			// Setup a select listener for the worldmap click-and-go feature
			this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));

			this.add((Component) this.wwd, BorderLayout.CENTER);
			if (includeStatusBar)
			{
				this.statusBar = new StatusBar();
				this.add(statusBar, BorderLayout.PAGE_END);
				this.statusBar.setEventSource(wwd);
			}

			// Add controllers to manage highlighting and tool tips.
			this.toolTipController = new ToolTipController(this.getWwd(), AVKey.DISPLAY_NAME, null);
			this.highlightController = new HighlightController(this.getWwd(), SelectEvent.ROLLOVER);
		}

		protected WorldWindow createWorldWindow()
		{
			return new WorldWindowGLCanvas();
		}

		public WorldWindow getWwd()
		{
			return wwd;
		}

		public StatusBar getStatusBar()
		{
			return statusBar;
		}
	}


	
}
