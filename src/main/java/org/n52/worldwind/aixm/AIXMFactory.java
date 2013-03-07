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
package org.n52.worldwind.aixm;

import java.util.Date;
import java.util.List;

import org.n52.worldwind.aixm.xmlbeans.XBeansAIXMFactory;

import aero.aixm.schema.x51.AbstractAIXMFeatureType;
import aero.aixm.schema.x51.AirspaceType;
import aero.aixm.schema.x51.RouteSegmentType;
import gov.nasa.worldwind.render.airspaces.Airspace;

/**
 * Abstract class providing the basis for an implementation of AIXM2WW
 * feature creation.
 */
public abstract class AIXMFactory {
	
	private static AIXMFactory factory;

	static {
		factory = new XBeansAIXMFactory();
	}

	public static void setFactory(AIXMFactory fac) {
		factory = fac;
	}
	
	public static Airspace createAbstractAirspace(AbstractAIXMFeatureType aixmFeauture) {
		return createAbstractAirspace(aixmFeauture, new Date());
	}
	
	public static Airspace createAbstractAirspace(AbstractAIXMFeatureType aixmFeauture, Date validTime) {
		
		if (aixmFeauture instanceof AirspaceType) {
			return factory.createAirspace((AirspaceType) aixmFeauture, validTime);
		}
		else if (aixmFeauture instanceof RouteSegmentType) {
			return factory.createRouteSegment((RouteSegmentType) aixmFeauture, validTime);
		}
		
		return null;
	}

	public abstract Airspace createRoute(List<RouteSegmentType> routeSegments, Date validTime);
	
	public abstract Airspace createRouteSegment(RouteSegmentType routeSegment, Date validTime);
	
	public abstract Airspace createAirspace(AirspaceType airspace, Date validTime);
	
}
