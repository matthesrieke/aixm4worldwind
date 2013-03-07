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



import org.n52.oxf.conversion.gml32.geometry.AltitudeLimits;
import org.n52.oxf.conversion.gml32.geometry.AltitudeLimits.AltitudeReferences;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.airspaces.AbstractAirspace;

/**
 * Util class for applying altitudes to WW airspaces.
 */
public class AltitudeUtils {

	/**
	 * @param limits the altitudes
	 * @param feature the airspace
	 */
	public static void applyAltitudes(AltitudeLimits limits, AbstractAirspace feature) {
		applyAltitudes(limits.getLowerLimit(), limits.getLowerLimitReference(), limits.getUpperLimit(),
				limits.getUpperLimitReference(), feature);
	}
	
	/**
	 * applies the altitude to the airspace feature
	 */
	public static void applyAltitudes(double lowerLimit,
			AltitudeReferences lowerLimitReference, double upperLimit,
			AltitudeReferences upperLimitReference, AbstractAirspace feature) {
		feature.setAltitudes(lowerLimit, upperLimit);
		feature.setAltitudeDatum(mapAltitudeReference(lowerLimitReference), mapAltitudeReference(upperLimitReference));
	}

	private static String mapAltitudeReference(
			AltitudeReferences reference) {
		if (reference == null) return AVKey.ABOVE_GROUND_LEVEL;
		
		switch (reference) {
			case SFC:
				return AVKey.ABOVE_GROUND_LEVEL;
			case STD:
				return AVKey.ABOVE_GROUND_REFERENCE;
			case MSL:
				return AVKey.ABOVE_MEAN_SEA_LEVEL;
			default:
				return AVKey.ABOVE_GROUND_LEVEL;
		}
	}

	
}
