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

import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;

import org.apache.xmlbeans.XmlException;
import org.n52.worldwind.aixm.AIXMFactory;
import org.n52.worldwind.aixm.xmlbeans.TimeSliceTools;

import aero.aixm.schema.x51.AirspaceDocument;
import aero.aixm.schema.x51.RouteSegmentDocument;

public class DemoAirspaceLayer extends AirspaceLayer {

	private Airspace airspace;
	private Airspace route;

	public DemoAirspaceLayer() throws XmlException, IOException {
		super();
		this.airspace = createAirspace();
		this.route = createRoute();
		this.addAirspace(getAirspace());
		this.addAirspace(route);
		this.setEnableAntialiasing(true);
	}

	private Airspace createRoute() throws XmlException, IOException {
		RouteSegmentDocument routeDoc = RouteSegmentDocument.Factory.parse(getClass().getResourceAsStream("/aixm_test1_routesegment_touches.xml"));

		TimeSliceTools.resolveTimeSliceFromValidTime(routeDoc.getRouteSegment(), new Date());
		
		Airspace route = AIXMFactory.createAbstractAirspace(routeDoc.getRouteSegment());

		this.setupDefaultMaterial(route, Color.GREEN);
		return route;
	}

	private Airspace createAirspace() throws XmlException, IOException {
		AirspaceDocument routeDoc = AirspaceDocument.Factory.parse(getClass().getResourceAsStream("/aixm_test1_airspace.xml"));

		TimeSliceTools.resolveTimeSliceFromValidTime(routeDoc.getAirspace(), new Date());
		
		Airspace route = AIXMFactory.createAbstractAirspace(routeDoc.getAirspace());

		this.setupDefaultMaterial(route, Color.RED);
		return route;
	}

	public void setupDefaultMaterial(Airspace a, Color color) {
		a.getAttributes().setDrawOutline(false);
		a.getAttributes().setMaterial(new Material(color));
		a.getAttributes().setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
		a.getAttributes().setOpacity(0.8);
		a.getAttributes().setOutlineOpacity(0.9);
		a.getAttributes().setOutlineWidth(3.0);
	}


	public Airspace getAirspace() {
		return airspace;
	}

	public Airspace getRoute() {
		return route;
	}			
	
}
