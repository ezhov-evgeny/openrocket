package net.sf.openrocket.unit;

import java.text.DecimalFormat;

public abstract class Unit {
	
	/** No unit with 2 digit precision */
	public static final Unit NOUNIT2 = new GeneralUnit(1,"\u200b", 2);  // zero-width space

	protected final double multiplier;   // meters = units * multiplier
	protected final String unit;

	/**
	 * Creates a new Unit with a given multiplier and unit name.
	 * 
	 * Multiplier e.g. 1 in = 0.0254 meter
	 * 
	 * @param multiplier  The multiplier to use on the value, 1 this unit == multiplier SI units
	 * @param unit        The unit's short form.
	 */
	public Unit(double multiplier, String unit) {
		if (multiplier == 0)
			throw new IllegalArgumentException("Unit has multiplier=0");
		this.multiplier = multiplier;
		this.unit = unit;
	}

	/**
	 * Converts from SI units to this unit.  The default implementation simply divides by the
	 * multiplier.
	 * 
	 * @param value  Value in SI unit
	 * @return       Value in these units
	 */
	public double toUnit(double value) {
		return value/multiplier;
	}

	/**
	 * Convert from this type of units to SI units.  The default implementation simply 
	 * multiplies by the multiplier.
	 * 
	 * @param value  Value in these units
	 * @return       Value in SI units
	 */
	public double fromUnit(double value) {
		return value*multiplier;
	}

	
	/**
	 * Return the unit name.
	 * 
	 * @return	the unit.
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * Whether the value and unit should be separated by a whitespace.  This method 
	 * returns true as most units have a space between the value and unit, but may be 
	 * overridden.
	 * 
	 * @return  true if the value and unit should be separated
	 */
	public boolean hasSpace() {
		return true;
	}
	
	
	// Testcases for toString(double)
	public static void main(String arg[]) {
		System.out.println(NOUNIT2.toString(0.0049));
		System.out.println(NOUNIT2.toString(0.0050));
		System.out.println(NOUNIT2.toString(0.0051));
		System.out.println(NOUNIT2.toString(0.00123));
		System.out.println(NOUNIT2.toString(0.0123));
		System.out.println(NOUNIT2.toString(0.1234));
		System.out.println(NOUNIT2.toString(1.2345));
		System.out.println(NOUNIT2.toString(12.345));
		System.out.println(NOUNIT2.toString(123.456));
		System.out.println(NOUNIT2.toString(1234.5678));
		System.out.println(NOUNIT2.toString(12345.6789));
		System.out.println(NOUNIT2.toString(123456.789));
		System.out.println(NOUNIT2.toString(1234567.89));
		System.out.println(NOUNIT2.toString(12345678.9));
		
		System.out.println(NOUNIT2.toString(-0.0049));
		System.out.println(NOUNIT2.toString(-0.0050));
		System.out.println(NOUNIT2.toString(-0.0051));
		System.out.println(NOUNIT2.toString(-0.00123));
		System.out.println(NOUNIT2.toString(-0.0123));
		System.out.println(NOUNIT2.toString(-0.1234));
		System.out.println(NOUNIT2.toString(-1.2345));
		System.out.println(NOUNIT2.toString(-12.345));
		System.out.println(NOUNIT2.toString(-123.456));
		System.out.println(NOUNIT2.toString(-1234.5678));
		System.out.println(NOUNIT2.toString(-12345.6789));
		System.out.println(NOUNIT2.toString(-123456.789));
		System.out.println(NOUNIT2.toString(-1234567.89));
		System.out.println(NOUNIT2.toString(-12345678.9));
		
	}
	
	
	@Override
	public String toString() {
		return unit;
	}
	
	private static final DecimalFormat intFormat = new DecimalFormat("#");
	private static final DecimalFormat decFormat = new DecimalFormat("0.##");
	private static final DecimalFormat expFormat = new DecimalFormat("0.00E0");

	/**
	 * Format the given value (in SI units) to a string representation of the value in this
	 * units.  An suitable amount of decimals for the unit are used in the representation.
	 * The unit is not appended to the numerical value.
	 *  
	 * @param value  Value in SI units.
	 * @return       A string representation of the number in these units.
	 */
	public String toString(double value) {
		double val = toUnit(value);

		if (Math.abs(val) > 1E6) {
			return expFormat.format(val);
		}
		if (Math.abs(val) >= 100) {
			return intFormat.format(val);
		}
		if (Math.abs(val) <= 0.005) {
			return "0";
		}

		double sign = Math.signum(val);
		val = Math.abs(val);
		double mul = 1.0;
		while (val < 100) {
			mul *= 10;
			val *= 10;
		}
		val = Math.rint(val)/mul * sign;
		
		return decFormat.format(val);
	}
	
	
	/**
	 * Return a string with the specified value and unit.  The value is converted into
	 * this unit.  If <code>value</code> is NaN, returns "N/A" (not applicable).
	 * 
	 * @param value		the value to print in SI units.
	 * @return			the value and unit, or "N/A".
	 */
	public String toStringUnit(double value) {
		if (Double.isNaN(value))
			return "N/A";
		
		String s = toString(value);
		if (hasSpace())
			s += " ";
		s += unit;
		return s;
	}
	
	
	
	/**
	 * Creates a new Value object with the specified value and this unit.
	 * 
	 * @param value	the value to set.
	 * @return		a new Value object.
	 */
	public Value toValue(double value) {
		return new Value(value, this);
	}
	
	

	/**
	 * Round the value (in the current units) to a precision suitable for rough valuing
	 * (approximately 2 significant numbers).
	 * 
	 * @param value  Value in current units
	 * @return       Rounded value.
	 */
	public abstract double round(double value);

	/**
	 * Return the next rounded value after the given value.
	 * @param value  Value in these units.
	 * @return       The next suitable rounded value.
	 */
	public abstract double getNextValue(double value);
	
	/**
	 * Return the previous rounded value before the given value.
	 * @param value  Value in these units.
	 * @return       The previous suitable rounded value.
	 */
	public abstract double getPreviousValue(double value);
	
	//public abstract ArrayList<Tick> getTicks(double start, double end, double scale);
	
	/**
	 * Return ticks in the range start - end (in current units).  minor is the minimum
	 * distance between minor, non-notable ticks and major the minimum distance between
	 * major non-notable ticks.  The values are in current units, i.e. no conversion is
	 * performed.
	 */
	public abstract Tick[] getTicks(double start, double end, double minor, double major);
	
	/**
	 * Compares whether the two units are equal.  Equality requires the unit classes,
	 * multiplier values and units to be equal.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (this.getClass() != other.getClass())
			return false;
		return ((this.multiplier == ((Unit)other).multiplier) && 
				this.unit.equals(((Unit)other).unit));
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode() + this.unit.hashCode();
	}

}
