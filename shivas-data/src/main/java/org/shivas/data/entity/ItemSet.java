package org.shivas.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Multimap;

public class ItemSet implements Serializable {

	private static final long serialVersionUID = 4217103727949901207L;
	
	private short id;
	private List<ItemTemplate> items;
	private Multimap<Integer, ConstantItemEffect> effects;
	
	/**
	 * @return the id
	 */
	public short getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(short id) {
		this.id = id;
	}
	/**
	 * @return the items
	 */
	public List<ItemTemplate> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<ItemTemplate> items) {
		this.items = items;
	}
	/**
	 * @return the effects
	 */
	public Multimap<Integer, ConstantItemEffect> getEffects() {
		return effects;
	}
	/**
	 * @param effects the effects to set
	 */
	public void setEffects(Multimap<Integer, ConstantItemEffect> effects) {
		this.effects = effects;
	}
	
	public Collection<ConstantItemEffect> getEffects(int count) {
		return effects.get(count);
	}

}
