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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumns.PlayListColumn;
import net.sourceforge.atunes.model.AudioObject;

/**
 * This class represents a column of Play List
 * 
 * @author fleax
 * 
 */
public abstract class Column implements Comparable<Column>, Serializable {

	/**
	 * Column identifier
	 */
	private PlayListColumn columnId;

	/**
	 * Header text of column
	 */
	private String headerText;

	/**
	 * Resizable
	 */
	private boolean resizable = true;

	/**
	 * Width of column
	 */
	private int width = 150;

	/**
	 * Class of data
	 */
	private Class<?> columnClass;

	/**
	 * Visible column
	 */
	private boolean visible;

	/**
	 * Order
	 */
	private int order;

	/**
	 * Text alignment
	 */
	private int alignment = GuiUtils.getComponentOrientationAsSwingConstant();

	/**
	 * Editable flag
	 */
	private boolean editable = false;

	/**
	 * Constructor with columnId, headerText and columnClass
	 * 
	 * @param columnId
	 * @param headerText
	 * @param columnClass
	 */
	public Column(PlayListColumn columnId, String headerText, Class<?> columnClass) {
		this.columnId = columnId;
		this.headerText = headerText;
		this.columnClass = columnClass;
		this.order = ColumnOrder.getOrder();
	}

	public void applyColumnBean(ColumnBean bean) {
		order = bean.getOrder();
		width = bean.getWidth();
		visible = bean.isVisible();
	}

	/**
	 * Compare method
	 */
	@Override
	public int compareTo(Column o) {
		return Integer.valueOf(order).compareTo(o.order);
	}

	/**
	 * @return the alignment
	 */
	public int getAlignment() {
		return alignment;
	}

	/**
	 * @return the cellEditor
	 */
	public TableCellEditor getCellEditor() {
		return null;
	}

	/**
	 * @return the cellRenderer
	 */
	public TableCellRenderer getCellRenderer() {
		return null;
	}

	public ColumnBean getColumnBean() {
		ColumnBean bean = new ColumnBean();
		bean.setOrder(order);
		bean.setWidth(width);
		bean.setVisible(visible);
		return bean;
	}

	/**
	 * @return the columnClass
	 */
	public Class<?> getColumnClass() {
		return columnClass;
	}

	/**
	 * @return the columnId
	 */
	public PlayListColumn getColumnId() {
		return columnId;
	}

	public String getColumnName() {
		return headerText;
	}

	/**
	 * Comparator to sort column
	 */
	public abstract Comparator<AudioObject> getComparator();

	/**
	 * @return the headerText
	 */
	public String getHeaderText() {
		return headerText;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Returns value for a column of an audiofile
	 * 
	 * @param audioObject
	 * @return
	 */
	public abstract Object getValueFor(AudioObject audioObject);

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return the resizable
	 */
	public boolean isResizable() {
		return resizable;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param alignment
	 *            the alignment to set
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	/**
	 * @param columnClass
	 *            the columnClass to set
	 */
	public void setColumnClass(Class<?> columnClass) {
		this.columnClass = columnClass;
	}

	/**
	 * @param columnId
	 *            the columnId to set
	 */
	public void setColumnId(PlayListColumn columnId) {
		this.columnId = columnId;
	}

	/**
	 * @param editable
	 *            the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * @param headerText
	 *            the headerText to set
	 */
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @param resizable
	 *            the resizable to set
	 */
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	/**
	 * Sets value for a property of an audio object
	 */
	public void setValueFor(AudioObject audioObject, Object value) {
		// Does nothing, should be overrided
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return headerText;
	}

}
