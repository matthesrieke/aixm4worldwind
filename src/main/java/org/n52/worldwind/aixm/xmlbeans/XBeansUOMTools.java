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

import org.n52.oxf.conversion.unit.UOMTools;

import aero.aixm.schema.x51.ValDistanceType;
import aero.aixm.schema.x51.ValDistanceVerticalType;

/**
 * XMLBeans specific UOM helper methods.
 */
public class XBeansUOMTools {

	public static double parseValDistance(ValDistanceType val, String targetUom) {
		if (val.isSetUom()) {
			return UOMTools.convertToTargetUnit(val.getBigDecimalValue().doubleValue(), val.getUom().trim(), targetUom);
		} else {
			return val.getBigDecimalValue().doubleValue();
		}
	}

	public static double parseValDistance(ValDistanceVerticalType val,
			String targetUom) {
		double number = Double.parseDouble(val.getStringValue());
		if (val.isSetUom()) {
			return UOMTools.convertToTargetUnit(number, val.getUom().trim(), targetUom);
		} else {
			return number;
		}
	}
	
}
