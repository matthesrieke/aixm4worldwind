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

import java.util.Date;
import java.util.List;

import gov.nasa.worldwind.render.airspaces.Airspace;

import org.n52.worldwind.aixm.AIXMFactory;

import aero.aixm.schema.x51.AirspaceType;
import aero.aixm.schema.x51.RouteSegmentType;

/**
 * XMLBeans implementation of {@link AIXMFactory}.
 */
public class XBeansAIXMFactory extends AIXMFactory {

	@Override
	public Airspace createRoute(List<RouteSegmentType> routeSegments, Date validTime) {
		return RouteFactory.createRoute(routeSegments);
	}
	
	@Override
	public Airspace createRouteSegment(RouteSegmentType routeSegment, Date validTime) {
		return  RouteFactory.createRouteSegment(routeSegment, validTime);
	}

	@Override
	public Airspace createAirspace(AirspaceType airspace, Date validTime) {
		return AirspaceFactory.createAirspace(airspace, validTime);
	}




}
