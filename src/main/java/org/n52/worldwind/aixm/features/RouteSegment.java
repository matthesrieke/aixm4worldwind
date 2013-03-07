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
package org.n52.worldwind.aixm.features;

import java.util.ArrayList;
import java.util.List;

import org.n52.oxf.conversion.gml32.geometry.AltitudeLimits;
import org.n52.oxf.conversion.gml32.geometry.AltitudeLimits.AltitudeReferences;
import org.n52.oxf.conversion.gml32.geometry.GeometryWithInterpolation;
import org.n52.oxf.conversion.gml32.geometry.RouteSegmentWithAltitudeLimits;
import org.n52.worldwind.aixm.AltitudeUtils;


import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.airspaces.Route;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

/**
 * A WW representation of an AIXM RouteSegment.
 */
public class RouteSegment extends Route {
	
	/**
	 * constructor for convenience. Applies some defaults altitudes and width
	 */
	public RouteSegment(LineString lineString) {
		this(lineString, 500.0, new AltitudeLimits(2000d, AltitudeReferences.SFC, 2500d, AltitudeReferences.SFC));
	}
	
	public RouteSegment(LineString lineString, double width, AltitudeLimits altitudes) {
		this(width, altitudes);
		
		List<LatLon> locations = new ArrayList<LatLon>();
		for (Coordinate c : lineString.getCoordinates()) {
			locations.add(createLatLon(c));
		}
		
		this.addLocations(locations);
	}

	public RouteSegment(RouteSegmentWithAltitudeLimits routeSegment) {
		this(routeSegment.getWidth(), routeSegment.getAltitudeLimits());
		
		List<LatLon> locations = new ArrayList<LatLon>();
		for (GeometryWithInterpolation  geom : routeSegment.getGeometries()) {
			/*
			 * remove last from previous, would be a duplicate
			 */
			if (locations.size() > 0) locations.remove(locations.size() - 1);
			
			for (Coordinate coord : geom.getGeometry().getCoordinates()) {
				locations.add(createLatLon(coord));
			}
		}
		this.addLocations(locations);
	}
	
	/**
	 * this constructor does not specify any horizontal projection.
	 * it is provided for convenience and better readability.
	 * 
	 * @param width the routes width in meters
	 * @param altitudes the altitudes
	 */
	private RouteSegment(double width, AltitudeLimits altitudes) {
		super();
		this.setEnableInnerCaps(false);
		this.setWidth(width);
		AltitudeUtils.applyAltitudes(altitudes, this);
	}

	private LatLon createLatLon(Coordinate c) {
		return LatLon.fromDegrees(c.y, c.x);
	}

}
