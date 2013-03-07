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
package org.n52.worldwind.aixm.xmlbeans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.n52.oxf.conversion.gml32.geometry.RouteSegmentWithAltitudeLimits;
import org.n52.worldwind.aixm.features.Route;
import org.n52.worldwind.aixm.features.RouteSegment;
import org.n52.worldwind.aixm.xmlbeans.jts.RouteSegmentGeometryFactory;

import aero.aixm.schema.x51.RouteSegmentTimeSliceType;
import aero.aixm.schema.x51.RouteSegmentType;

/**
 * Class providing static methods for creating WW airspace
 * representations from AIXM RouteSegments.
 */
public class RouteFactory {

	/**
	 * @param routeSegments the segments
	 * @return a combined Route
	 */
	public static Route createRoute(List<RouteSegmentType> routeSegments) {
		List<gov.nasa.worldwind.render.airspaces.Route> segments = new ArrayList<gov.nasa.worldwind.render.airspaces.Route>();

		for (RouteSegmentType routeSegment : routeSegments) {
			segments.add(createRouteSegment(routeSegment));
		}

		return new Route(segments);
	}

	/**
	 * @param routeSegment the segment
	 * @return a single RouteSegment with a validTime default to now()
	 */
	public static RouteSegment createRouteSegment(RouteSegmentType routeSegment) {
		return createRouteSegment(routeSegment, new Date());
	}

	/**
	 * @param routeSegment the segment
	 * @param validTime the validTime to determine the correct timeslice
	 * @return a single RouteSegment
	 */
	public static RouteSegment createRouteSegment(RouteSegmentType routeSegment, Date validTime) {
		RouteSegmentTimeSliceType slice = (RouteSegmentTimeSliceType) TimeSliceTools.resolveTimeSliceFromValidTime(routeSegment, validTime);
		
		if (slice != null) {
			return parseRouteSegmentTimeSlice(slice);
		}
		
		throw new IllegalArgumentException("No TimeSlice present in this RouteSegment.");
	}

	private static RouteSegment parseRouteSegmentTimeSlice(
			RouteSegmentTimeSliceType routeSegmentTimeSlice) {
		RouteSegmentWithAltitudeLimits routeSegment = RouteSegmentGeometryFactory.parseRouteSegment(routeSegmentTimeSlice);
		return new RouteSegment(routeSegment);
	}


}
