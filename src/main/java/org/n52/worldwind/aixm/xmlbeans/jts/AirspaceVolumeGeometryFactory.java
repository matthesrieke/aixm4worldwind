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
package org.n52.worldwind.aixm.xmlbeans.jts;

import java.util.ArrayList;
import java.util.List;

import net.opengis.gml.x32.AbstractSurfacePatchType;
import net.opengis.gml.x32.PolygonPatchType;
import net.opengis.gml.x32.RectangleType;

import org.n52.oxf.conversion.gml32.geometry.AirspaceVolumeWithAltitudeLimits;
import org.n52.oxf.conversion.gml32.geometry.AltitudeLimits;
import org.n52.oxf.conversion.gml32.geometry.GeometryWithInterpolation;
import org.n52.oxf.conversion.gml32.srs.SRSUtils;
import org.n52.worldwind.aixm.xmlbeans.AltitudeTools;

import aero.aixm.schema.x51.AirspaceVolumeType;
import aero.aixm.schema.x51.CurvePropertyType;
import aero.aixm.schema.x51.SurfacePropertyType;
import aero.aixm.schema.x51.SurfaceType;
import aero.aixm.schema.x51.ValDistanceType;

/**
 * Class provides static methods for parsing AIXM
 * AirspaceVolumes.
 */
public class AirspaceVolumeGeometryFactory {

	/**
	 * @param volume the airspace volume
	 * @return an airspacevolume with possible multiple geometries and altitudes defined.
	 */
	public static AirspaceVolumeWithAltitudeLimits parseVolume(AirspaceVolumeType volume) {
		if (!volume.isSetHorizontalProjection() && !volume.isSetCentreline()) throw new UnsupportedOperationException("No horizontal projection or centreline was found.");
		
		List<GeometryWithInterpolation> horiz;
		if (volume.isSetHorizontalProjection()) {
			horiz = parseHorizontalProjection(volume.getHorizontalProjection());
		} else {
			horiz = parseCentreLine(volume.getCentreline(), volume.getWidth());
		}
		
		AltitudeLimits altitude = AltitudeTools.createAltitueLimits(volume.getLowerLimit(), volume.getLowerLimitReference(),
				volume.getUpperLimit(), volume.getUpperLimitReference());
		
		return new AirspaceVolumeWithAltitudeLimits(horiz, altitude);
	}

	private static List<GeometryWithInterpolation> parseCentreLine(CurvePropertyType centreline,
			ValDistanceType width) {
		throw new UnsupportedOperationException("centreline is currently not supported.");
	}

	private static List<GeometryWithInterpolation> parseHorizontalProjection(
			SurfacePropertyType surfaceProperty) {
		SurfaceType surface = surfaceProperty.getSurface();
		
		String srs;
		if (surface.isSetSrsName()) {
			srs = surface.getSrsName();
		}
		else {
			srs = SRSUtils.DEFAULT_SRS;
		}
		
		List<GeometryWithInterpolation> result = new ArrayList<GeometryWithInterpolation>();
		for (AbstractSurfacePatchType patch : surface.getPatches().getAbstractSurfacePatchArray()) {
			if (patch instanceof PolygonPatchType) {
				result.add(AIXMGeometryFactory.createPolygonPatch((PolygonPatchType) patch, srs));
			} else if (patch instanceof RectangleType) {
				result.add(AIXMGeometryFactory.createRectangle((RectangleType) patch, srs));
			} else {
				throw new UnsupportedOperationException("Only Polygon and Rectangle patches supported.");
			}
		}
		return result;
	}
	
}
