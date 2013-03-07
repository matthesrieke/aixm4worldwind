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

import gov.nasa.worldwind.render.airspaces.Box;

import java.util.List;


/**
 * A Route (extended from {@link gov.nasa.worldwind.render.airspaces.Route})
 * build from multiple Routes (e.g. constructed from AIXM RouteSegments)
 */
public class Route extends gov.nasa.worldwind.render.airspaces.Route {

	public Route(List<gov.nasa.worldwind.render.airspaces.Route> segments) {
		this.setEnableInnerCaps(false);
		for (gov.nasa.worldwind.render.airspaces.Route routeSegment : segments) {
			for (Box leg : routeSegment.getLegs()) {
				this.addLeg(leg);
			}
		}
	}

}
