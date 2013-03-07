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

import org.n52.oxf.conversion.gml32.geometry.AirspaceVolumeWithAltitudeLimits;
import org.n52.oxf.conversion.gml32.geometry.AltitudeLimits;
import org.n52.oxf.conversion.gml32.geometry.GeometryWithInterpolation;
import org.n52.oxf.conversion.gml32.xmlbeans.jts.GMLGeometryFactory;
import org.n52.worldwind.aixm.AltitudeUtils;
import org.n52.worldwind.aixm.features.AggregatedAirspace;
import org.n52.worldwind.aixm.xmlbeans.jts.AIXMGeometryFactory;
import org.n52.worldwind.aixm.xmlbeans.jts.AirspaceVolumeGeometryFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;


import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.airspaces.AbstractAirspace;
import gov.nasa.worldwind.render.airspaces.Airspace;
import aero.aixm.schema.x51.AirspaceGeometryComponentPropertyType;
import aero.aixm.schema.x51.AirspaceGeometryComponentType;
import aero.aixm.schema.x51.AirspaceTimeSliceType;
import aero.aixm.schema.x51.AirspaceType;

/**
 * Class provides static methods for the creation of WW Airspaces
 * from AIXM airspaces.
 */
public class AirspaceFactory {

	/**
	 * @param airspace the aixm airspace
	 * @param validTime the validTime (used to determine the correct timeslice if
	 * multiple are included)
	 * @return a WW airspace
	 */
	public static Airspace createAirspace(AirspaceType airspace, Date validTime) {
		AirspaceTimeSliceType slice = (AirspaceTimeSliceType) TimeSliceTools.resolveTimeSliceFromValidTime(airspace, validTime);
		
		if (slice != null) {
			return parseAirspaceTimeSlice(slice);
		}
		
		throw new IllegalStateException("Could not find an Airspace TimeSlice!");
	}

	private static Airspace parseAirspaceTimeSlice(AirspaceTimeSliceType slice) {
		List<AirspaceVolumeWithOperation> volumes = parseGeometryComponents(slice.getGeometryComponentArray());
		return createAirspaceFromVolumes(volumes);
	}
	
	private static Airspace createAirspaceFromVolumes(
			List<AirspaceVolumeWithOperation> volumes) {
		List<AbstractAirspace> airspaces = new ArrayList<AbstractAirspace>();
		
		boolean singleVolume = volumes.size() == 1;
		
		/*
		 * TODO: apply AIXM geometry operations
		 */
		for (AirspaceVolumeWithOperation volume : volumes) {
			/*
			 * TODO: Map JTS geometries to WW airspaces
			 */
			singleVolume = singleVolume && volume.volume.getGeometries().size() == 1;
			for (GeometryWithInterpolation volumeIntepol : volume.volume.getGeometries()) {
				if (volumeIntepol.getGeometry() instanceof Polygon) {
					gov.nasa.worldwind.render.airspaces.Polygon airspace = new gov.nasa.worldwind.render.airspaces.Polygon();
					List<LatLon> coords = new ArrayList<LatLon>();
					for (Coordinate c : volumeIntepol.getGeometry().getCoordinates()) {
						coords.add(LatLon.fromDegrees(c.y, c.x));
					}
					airspace.setLocations(coords);
					AltitudeLimits alt = volume.volume.getAltitudeLimits();
					AltitudeUtils.applyAltitudes(alt, airspace);
					
					if (singleVolume) {
						return airspace;
					}
					else {
						airspaces.add(airspace);
					}
				}
			}
		}
		
		return new AggregatedAirspace(airspaces);
	}

	public static List<AirspaceVolumeWithOperation> parseGeometryComponents(
			AirspaceGeometryComponentPropertyType[] geometryComponentArray) {
		List<AirspaceVolumeWithOperation> result = new ArrayList<AirspaceVolumeWithOperation>();
		
		AirspaceGeometryComponentType geomComp;
		for (AirspaceGeometryComponentPropertyType comp : geometryComponentArray) {
			geomComp = comp.getAirspaceGeometryComponent();

			if (!geomComp.isSetTheAirspaceVolume()) continue;
			
			/*
			 * operation
			 */
			String operation;
			if (geomComp.isSetOperation()) {
				operation = geomComp.getOperation().getStringValue().trim();
			} else {
				operation = "BASE";
			}
			
			/*
			 * operationSequence
			 */
			double operationSequence;
			if (geomComp.isSetOperationSequence()) {
				operationSequence = geomComp.getOperationSequence().getBigDecimalValue().doubleValue();
			} else {
				operationSequence = 1d;
			}
			
			/*
			 * wrap as AirspaceVolumeWithOperation
			 */
			AirspaceVolumeWithAltitudeLimits geom = AirspaceVolumeGeometryFactory.parseVolume(geomComp.getTheAirspaceVolume().getAirspaceVolume());
			
			GMLGeometryFactory.checkAndApplyInterpolation(geom.getGeometries());
			
			result.add(new AirspaceVolumeWithOperation(geom,
					operation, operationSequence));
		}
		
		return result;
	}



	public static class AirspaceVolumeWithOperation {

		private AirspaceVolumeWithAltitudeLimits volume;
		private String operation;
		private double operationSequence;

		public AirspaceVolumeWithOperation(AirspaceVolumeWithAltitudeLimits geom, String operation,
				double operationSequence) {
			this.volume = geom;
			this.operation = operation;
			this.operationSequence = operationSequence;
		}

		public AirspaceVolumeWithAltitudeLimits getVolume() {
			return volume;
		}

		public String getOperation() {
			return operation;
		}

		public double getOperationSequence() {
			return operationSequence;
		}
		
	}

}
