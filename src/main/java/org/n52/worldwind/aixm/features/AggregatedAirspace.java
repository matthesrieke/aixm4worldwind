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

import java.util.List;

import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.airspaces.AbstractAirspace;

/**
 * An aggregation of multiple WW airspaces. Implementation currently lacks support
 * for {@link #computeExtent(Globe, double)} and {@link #computeMinimalGeometry(Globe, double)}.
 */
public class AggregatedAirspace extends AbstractAirspace {

	private List<AbstractAirspace> airspaces;

	public AggregatedAirspace(List<AbstractAirspace> airspaces) {
		this.airspaces = airspaces;
	}

	@Override
	public Position getReferencePosition() {
		for (AbstractAirspace a : this.airspaces) {
			return a.getReferencePosition();
		}
		return null;
	}

	@Override
	protected Extent computeExtent(Globe globe, double verticalExaggeration) {
		//TODO calculate overall extent
		for (AbstractAirspace a : this.airspaces) {
			return a.getExtent(globe, verticalExaggeration);
		}
		return null;
	}

	@Override
	protected List<Vec4> computeMinimalGeometry(Globe globe,
			double verticalExaggeration) {
		//TODO is there an alternative?
		return null;
	}

	@Override
	protected void doRenderGeometry(DrawContext dc, String drawStyle) {
		for (AbstractAirspace a : this.airspaces) {
			a.renderGeometry(dc, drawStyle);
		}
	}

}
