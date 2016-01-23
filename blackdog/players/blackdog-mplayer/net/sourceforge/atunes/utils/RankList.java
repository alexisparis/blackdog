/*
 * aTunes 1.8.2
 * Copyright (C) 2006-2008 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/?page_id=7 for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a Rank: a list of objects, every one with an associate
 * counter. Objects are ordered by this counter. This class is used for
 * statistics
 * 
 */
public class RankList<T> implements Serializable {

	private static final long serialVersionUID = -4404155415144124761L;

	/**
	 * Order of elements
	 */
	private List<T> order;

	/**
	 * Count of every object
	 */
	private Map<T, Integer> count;

	/**
	 * Constructor
	 */
	public RankList() {
		order = new ArrayList<T>();
		count = new HashMap<T, Integer>();
	}

	/**
	 * Adds an object to rank. If rank contains object, adds 1 to counter and
	 * updates rank order If not, adds object with count 1
	 * 
	 * @param obj
	 */
	public void addItem(T obj) {
		if (order.contains(obj)) {
			Integer previousCount = count.get(obj);
			count.put(obj, previousCount + 1);
			moveUpOnList(obj);
		} else {
			order.add(obj);
			count.put(obj, 1);
		}
	}

	/**
	 * Returns count for a given object
	 * 
	 * @param obj
	 * @return
	 */
	public Integer getCount(T obj) {
		return count.get(obj);
	}

	/**
	 * Returns the first n elements count of this rank
	 * 
	 * @param n
	 * @return
	 */
	public List<Integer> getNFirstElementCounts(int n) {
		if (n <= -1 || n > order.size())
			n = order.size();
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			result.add(count.get(order.get(i)));
		}
		return result;
	}

	/**
	 * Returns the first n elements of this rank
	 * 
	 * @param n
	 * @return
	 */
	public List<T> getNFirstElements(int n) {
		if (n <= -1 || n > order.size())
			return new ArrayList<T>(order);
		else
			return new ArrayList<T>(order.subList(0, n));
	}

	/**
	 * @return the order
	 */
	public List<T> getOrder() {
		return order;
	}

	/**
	 * Updates order object
	 * 
	 * @param obj
	 */
	private void moveUpOnList(T obj) {
		int index = order.indexOf(obj);
		if (index > 0) {
			int previousItemCount = count.get(order.get(index - 1));
			int currentItemCount = count.get(order.get(index));
			if (previousItemCount < currentItemCount) {
				T previous = order.get(index - 1);
				T current = order.get(index);

				order.remove(previous);
				order.remove(current);

				order.add(index - 1, current);
				order.add(index, previous);

				moveUpOnList(obj);
			}
		}
	}

	/**
	 * Replaces an object, keeping order and count
	 * 
	 * @param oldItem
	 * @param newItem
	 */
	public void replaceItem(T oldItem, T newItem) {
		int order1 = this.order.indexOf(oldItem);
		this.order.remove(order1);
		this.order.add(order1, newItem);

		Integer count1 = this.count.get(oldItem);
		this.count.remove(oldItem);
		this.count.put(newItem, count1);
	}

	/**
	 * Return the size of rank
	 * 
	 * @return
	 */
	public int size() {
		return order.size();
	}
}
