package org.shivas.data.entity;

import org.shivas.protocol.client.enums.ItemEffectEnum;
import org.shivas.protocol.client.types.BaseItemEffectType;

import java.io.Serializable;

public class ConstantItemEffect extends ItemEffect implements Serializable {

	private static final long serialVersionUID = -5447606535436769040L;

	private short bonus;
	
	public ConstantItemEffect(ItemEffectEnum type) {
        super(type);
	}
	
	public ConstantItemEffect(ItemEffectEnum type, short bonus) {
        super(type);
		this.bonus = bonus;
	}

	/**
	 * @return the bonus
	 */
	public short getBonus() {
		return bonus;
	}
	/**
	 * @param bonus the bonus to set
	 */
	public void setBonus(short bonus) {
		this.bonus = bonus;
	}
	
	public ConstantItemEffect copy() {
		return new ConstantItemEffect(type, bonus);
	}

    @Override
    public BaseItemEffectType toBaseItemEffectType() {
        return new BaseItemEffectType(type, bonus);
    }

    @Override
    public String toString(int radix) {
        return Integer.toString(type.value(), radix) + "," +
               Integer.toString(bonus, radix);
    }

    @Override
    public void fromString(String string, int radix) {
        String[] args = string.split(",");

        type = ItemEffectEnum.valueOf(Integer.parseInt(args[0], radix));
        bonus = Short.parseShort(args[1], radix);
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConstantItemEffect that = (ConstantItemEffect) o;
		return bonus == that.bonus;
	}

	@Override
	public int hashCode() {
		return (int) bonus;
	}
}
