package org.shivas.data;

import java.util.Collection;

public interface Repository<T> {
	T byId(int id);
	Collection<T> all();
	int count();
	
	Class<T> getEntityClass();
}
