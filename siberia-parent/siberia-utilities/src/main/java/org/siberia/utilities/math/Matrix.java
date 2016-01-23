/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.utilities.math;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Representation of a Matrix
 * 
 * @author alexis
 */
public class Matrix implements Cloneable
{
    /** content */
    Number[][]  content         = null;
    
    /** row dimension */
    private int rowDimension    = 0;
    
    /** column dimension */
    private int columnDimension = 0;
    
    /**
     * Creates a new instance of Matrix which is filled with 0
     * 
     * @param n the dimension of the matrix
     *
     * @exception IllegalArgumentException if one of the dimension is less than 1
     */
    public Matrix(int n)
    {
	this(n, n, 0);
    }
    
    /**
     * Creates a new instance of Matrix which is filled with 0
     * 
     * @param n the row dimension
     * @param p the column dimension
     *
     * @exception IllegalArgumentException if one of the dimension is less than 1
     */
    public Matrix(int n, int p)
    {
	this(n, p, 0);
    }
    
    /**
     * Creates a new instance of Matrix
     * 
     * @param n the row dimension
     * @param p the column dimension
     * @param defaultValue the value to use to fill the matrix
     *
     * @exception IllegalArgumentException if one of the dimension is less than 1
     */
    public Matrix(int n, int p, Number defaultValue)
    {
	if ( n < 1 )
	{
	    throw new IllegalArgumentException("the row dimension of a matrix has to be greater or equals to 1");
	}
	if ( p < 1 )
	{
	    throw new IllegalArgumentException("the column dimension of a matrix has to be greater or equals to 1");
	}
	
	this.rowDimension    = n;
	this.columnDimension = p;
	this.content = new Number[n][p];
	
	this.fill(defaultValue);
    }
    
    /**
     * Creates a new instance of Matrix according to a given Matrix
     * 
     * @param matrix another matrix
     */
    public Matrix(Matrix matrix)
    {
	this(matrix.getRowDimension(), matrix.getColumnDimension());
	
	for(int i = 0; i < this.getRowDimension(); i++)
	{
	    for(int j = 0; j < this.getColumnDimension(); j++)
	    {
		this.set(i, j, matrix.get(i, j));
	    }
	}
    }
    
    /** return the row dimension
     *	@return an integer
     */
    public int getRowDimension()
    {	
	return this.rowDimension;
    }
    
    /** return the column dimension
     *	@return an integer
     */
    public int getColumnDimension()
    {	
	return this.columnDimension;
    }
    
    /** return true if the matrix is an identity matrix
     *	@return true if the matrix is an identity matrix
     */
    public boolean isIdentity()
    {
	boolean result = false;
	
	if ( this.getRowDimension() == this.getColumnDimension() )
	{
	    result = true;
	
	    main_loop :
	    for(int i = 0; i < this.getRowDimension(); i++)
	    {
		for(int j = 0; j < this.getColumnDimension(); j++)
		{
		    Number n = this.get(i, j);
		    
		    if ( n == null )
		    {
			result = false;
		    }
		    else
		    {
			if ( i == j )
			{
			    result = n.doubleValue() == 1.0;
			}
			else
			{
			    result = n.doubleValue() == 0.0;
			}
		    }
		    
		    if ( ! result )
		    {
			break main_loop;
		    }
		}
	    }
	}
	
	return result;
    }
    
    /** ########################################################################
     *  ####################### content management #############################
     *  ######################################################################## */
    
    /** return the element at the given row and column
     *	@param x the row index
     *	@param y the column index
     *	@return a Number
     */
    public Number get(int x, int y)
    {
	return this.content[x][y];
    }
    
    /** set the element for the given position
     *	@param item the item to set
     *	@param x the row index
     *	@param y the column index
     */
    public void set(int x, int y, Number item)
    {
	this.content[x][y] = item;
    }
    
    /** fill the matrix with the given Number
     *	@param number a Number
     */
    public void fill(Number number)
    {
	for(int i = 0; i < this.getRowDimension(); i++)
	{
	    Number[] rows = this.content[i];
	    
	    for(int j = 0; j < this.getColumnDimension(); j++)
	    {
		rows[j] = number == null ? 0 : number;
	    }
	}
    }
    
    /** fill the matrix with the array of array of Numbers
     *	@param arrays an array of array of Numbers
     */
    public void fill(Number[][] arrays)
    {
	if ( arrays != null )
	{
	    for(int i = 0; i < this.getRowDimension(); i++)
	    {
		for(int j = 0; j < this.getColumnDimension(); j++)
		{
		    this.set( i, j, arrays[i][j]);
		}
	    }
	}
    }
    
    /** fill the matrix column with the given Number
     *	@param column the index of the column of the matrix to fill with the given Number
     *	@param number a Number
     */
    public void fillColumn(int column, Number number)
    {
	for(int i = 0; i < this.getRowDimension(); i++)
	{
	    this.set(i, column, number);
	}
    }
    
    /** fill the matrix row with the given Number
     *	@param row the index of the row of the matrix to fill with the given Number
     *	@param number a Number
     */
    public void fillRow(int row, Number number)
    {
	for(int i = 0; i < this.getColumnDimension(); i++)
	{
	    this.set(row, i, number);
	}
    }
    
    /** ########################################################################
     *  ######################## matrix creation ###############################
     *  ######################################################################## */
    
    /** return a new matrix extracted from this matrix
     *	@param rowStart the included start index of the row to consider
     *	@param rowEnd the included end index of the row to consider
     *	@param columnStart the included start index of the column to consider
     *	@param columnEnd the included end index of the column to consider
     *	@return a new Matrix
     */
    public Matrix createExtractedMatrix(int rowStart, int rowEnd, int columnStart, int columnEnd)
    {	
	if ( rowEnd < rowStart )
	{
	    throw new IllegalArgumentException("rowEnd have to be greater or equals to rowStart");
	}
	if ( columnEnd < columnStart )
	{
	    throw new IllegalArgumentException("columnEnd have to be greater or equals to columnStart");
	}
	if ( rowStart < 0 || rowStart > this.getRowDimension() - 1 || rowEnd < 0 || rowEnd > this.getRowDimension() - 1 ||
	     columnStart < 0 || columnStart > this.getColumnDimension() - 1 || columnEnd < 0 || columnEnd > this.getColumnDimension() - 1 )
	{
	    throw new IllegalArgumentException("illegal extraction index");
	}
	
	Matrix result = new Matrix( (rowEnd - rowStart + 1), (columnEnd - columnStart + 1) );
	
	for(int i = rowStart; i <= rowEnd; i++)
	{
	    for(int j = columnStart; j <= columnEnd; j++)
	    {
		result.set( i - rowStart, j - columnStart, this.get(i, j));
	    }
	}
	
	return result;
    }
    
    /** return a new column matrix according to the data in the column which index is column
     *	@param column the index of the column to consider
     *	@return a new Matrix
     */
    public Matrix createColumnMatrix(int column)
    {
	return this.createExtractedMatrix(0, this.getRowDimension() - 1, column, column);
    }
    
    /** return a new row matrix according to the data in the row which index is row
     *	@param row the index of the row to consider
     *	@return a new Matrix
     */
    public Matrix createRowMatrix(int row)
    {
	return this.createExtractedMatrix(row, row, 0, this.getColumnDimension() - 1);
    }
    
    /** ########################################################################
     *  ####################### arithmetic methods #############################
     *  ######################################################################## */
    
    /** check if the given matrix has the same dimension, throw a IllegalArgumentException if not */
    private void checkMatrixDimensions(Matrix b)
    {
	if ( b == null || this.getRowDimension() != b.getRowDimension() || this.getColumnDimension() != b.getColumnDimension() )
	{
	    throw new IllegalArgumentException("operations on matrix have to be made on matrix of the same size");
	}
    }
    
    /** create a matrix that is this plus m
     *	@param m a Matrix
     *	@retrun a Matrix
     *	
     *	@exception IllegalArgumentException if m does not have the same dimension
     */
    public Matrix plus(Matrix m)
    {
	Matrix result = null;
	
	if ( m != null )
	{
	    this.checkMatrixDimensions(m);
	    
	    result = new Matrix(this.getRowDimension(), this.getColumnDimension());
	    
	    for(int i = 0; i < this.getRowDimension(); i++)
	    {		
		for(int j = 0; j < this.getColumnDimension(); j++)
		{
		    Number n1 = this.get(i, j);
		    Number n2 = m.get(i, j);
		    
		    Number total = (n1 == null ? 0 : n1.doubleValue()) + (n2 == null ? 0 : n2.doubleValue()); 
		    
		    result.set(i, j, total);
		}
	    }
	}
	
	return result;
    }
    
    /** create a matrix that is this minus m
     *	@param m a Matrix
     *	@retrun a Matrix
     *	
     *	@exception IllegalArgumentException if m does not have the same dimension
     */
    public Matrix minus(Matrix m)
    {
	Matrix result = null;
	
	if ( m != null )
	{
	    this.checkMatrixDimensions(m);
	    
	    result = new Matrix(this.getRowDimension(), this.getColumnDimension());
	    
	    for(int i = 0; i < this.getRowDimension(); i++)
	    {		
		for(int j = 0; j < this.getColumnDimension(); j++)
		{
		    Number n1 = this.get(i, j);
		    Number n2 = m.get(i, j);
		    
		    Number total = (n1 == null ? 0 : n1.doubleValue()) - (n2 == null ? 0 : n2.doubleValue()); 
		    
		    result.set(i, j, total);
		}
	    }
	}
	
	return result;
    }
    
    /** create a transposition of the matrix
     *	@return a new Matrix
     */
    public Matrix transpose()
    {
	Matrix result = new Matrix(this.getColumnDimension(), this.getRowDimension());
	
	for(int i = 0; i < this.getRowDimension(); i++)
	{
	    for(int j = 0; j < this.getColumnDimension(); j++)
	    {
		result.set(j, i, this.get(i, j));
	    }
	}
		
	return result;
    }
    
    /** create a new matrix by multiplying its content by the given factor
     *	@param factor
     *	@return the new matrix
     */
    public Matrix multiply(Number factor)
    {
	Matrix result = new Matrix(this.getRowDimension(), this.getColumnDimension(), 0);
	
	if ( factor != null && factor.doubleValue() != 0 )
	{
	    for(int i = 0; i < this.getRowDimension(); i++)
	    {
		for(int j = 0; j < this.getColumnDimension(); j++)
		{
		    Number n = this.get(i, j);
		    if ( n != null )
		    {
			result.set(i, j, n.doubleValue() * factor.doubleValue());
		    }
		}
	    }
	}
	
	return result;
    }
    
    /** create a new matrix by multiplying current matrix with given one
     *	@param m a Matrix
     *	@return the new matrix
     *
     *	@exception IllegalArgumentException if the dimensions of matrix m does not convey with the dimensions
     *		    of the current matrix
     */
    public Matrix multiply(Matrix m)
    {
	if ( m == null || m.getRowDimension() != this.getColumnDimension() || m.getColumnDimension() != this.getRowDimension() )
	{
	    throw new IllegalArgumentException("matrix m must have dimensions(" + this.getColumnDimension() + ", " + this.getRowDimension() + ")");
	}
	
	Matrix result = new Matrix(this.getRowDimension(), m.getColumnDimension());
	
	for(int i = 0; i < result.getRowDimension(); i++)
	{
	    for(int j = 0; j < result.getColumnDimension(); j++)
	    {
		Number sum = null;
		
		for(int k = 0; k < this.getColumnDimension(); k++)
		{
		    Number o1 = this.get(i, k);
		    Number o2 = m.get(k, j);
		    
		    Number total = (o1 != null && o2 != null ? o1.doubleValue() * o2.doubleValue() : 0);
		    
		    if ( sum == null )
		    {
			sum = total;
		    }
		    else
		    {
			sum = sum.doubleValue() + total.doubleValue();
		    }
		}
		
		result.set(i, j, sum);
	    }
	}
	
	return result;
    }
    
    /** ########################################################################
     *  ######################### print methods ################################
     *  ######################################################################## */
    
    /** print the matrix on System.out */
    public void print()
    {
	this.print(System.out);
    }
    
    /** print the matrix on the given PrintStream
     *	@param printStream a PrintStream
     */
    public void print(PrintStream printStream)
    {
	this.print(printStream, true);
    }
    
    /** print the matrix on the given PrintStream
     *	@param printStream a PrintStream
     *	@param sameColumnSize true to indicate that the column have to have the same size
     */
    public void print(PrintStream printStream, boolean sameColumnSize)
    {
	int   maxEver          = 1;
	int[] maxCarPerColumns = new int[this.getColumnDimension()];
	
	Arrays.fill(maxCarPerColumns, maxEver);
	
	for(int i = 0; i < this.getColumnDimension(); i++)
	{
	    for(int j = 0; j < this.getRowDimension(); j++)
	    {
		Number value = this.get(j, i);
		
		int nbCar = 1;
		
		if ( value == null )
		{
		    nbCar = 4;
		}
		else
		{
		    nbCar = Double.toString(value.doubleValue()).trim().length();
		}
		
		if ( maxCarPerColumns[i] < nbCar )
		{
		    maxCarPerColumns[i] = nbCar;
		}
		
		if ( maxEver < nbCar )
		{
		    maxEver = nbCar;
		}
	    }
	}
	
	if ( sameColumnSize )
	{
	    Arrays.fill(maxCarPerColumns, maxEver);
	}
	
	StringBuffer buffer = new StringBuffer(100);
	
	for(int i = 0; i < this.getRowDimension(); i++)
	{
	    buffer.delete(0, buffer.length());
	    buffer.append("[");
	    
	    for(int j = 0; j < this.getColumnDimension(); j++)
	    {
		Number value = this.get(i, j);
		
		String valueString = (value == null ? "null" : value.toString());
		
		int maxChar = maxCarPerColumns[j];
		
		int spaceLength = maxChar - valueString.length();
		
		for(int k = 0; k < spaceLength; k++)
		{
		    buffer.append(" ");
		}
		
		buffer.append(valueString);
		
		if ( j < this.getColumnDimension() - 1 )
		{
		    buffer.append(", ");
		}
	    }
	    
	    buffer.append("]");
	    
	    printStream.println(buffer.toString());
	}
    }
    
    /** ########################################################################
     *  ######################### Object methods ###############################
     *  ######################################################################## */
    
    /** clone the matrix
     *	@return an Object
     */
    public Object clone ()
    {
	return new Matrix(this);
    }
    
    /** return true if the given object is equals to this matrix
     *	@param o an Object
     */
    @Override
    public boolean equals(Object o)
    {
	boolean result = false;
	
	if ( o.getClass().equals(this.getClass()) )
	{
	    Matrix other = (Matrix)o;
	    
	    if ( this.getRowDimension() == other.getRowDimension() && this.getColumnDimension() == other.getColumnDimension() )
	    {
		result = true;
		
		/* compare content */
		main_loop :
		for(int i = 0; i < this.getRowDimension(); i++)
		{		    
		    for(int j = 0; j < this.getColumnDimension(); j++)
		    {
			Number o1 = this.get(i, j);
			Number o2 = other.get(i, j);
			
			if ( o1 == null )
			{
			    if ( o2 != null )
			    {
				result = false;
			    }
			}
			else
			{
			    result = o1.equals(o2);
			}
			
			if ( ! result )
			{
			    break main_loop;
			}
		    }
		}
	    }
	}
	
	return result;
    }
    
    /** ########################################################################
     *  ######################### static methods ###############################
     *  ######################################################################## */
    
    /** create an identity matrix of the given dimension
     *	@param dimension an integer representing the dimension
     *	@return a Matrix
     *
     *	@exception IllegalArgumentException if n <= 0
     */
    public static Matrix createIdentityMatrix(int dimension)
    {
	if ( dimension <= 0 )
	{
	    throw new IllegalArgumentException("the dimension has to be greater than 0");
	}
	
	Matrix m = new Matrix(dimension, dimension, 0);
	
	for(int i = 0; i < dimension; i++)
	{
	    for(int j = 0; j < dimension; j++)
	    {
		if ( i == j )
		{
		    m.set(i, j, 1);
		}
		else
		{
		    m.set(i, j, 0);
		}
	    }
	}
	
	return m;
    }
    
    /**
     * Creates a new instance of Matrix according to a String
     * 
     * @param s a String like '[[2, 3, 4][4, 5, 6]]' or '[[2.3, 3.05, 4.00][425, 528.56, 6.01]]'
     *
     *	@exception
     */
    public static Matrix createMatrix(String s) throws ParseException, NumberFormatException
    {
	Matrix result = null;
	
	int errorOffset = -1;
	
	if ( s != null )
	{
	    if ( s.length() <= 2 || s.charAt(0) != '[' || s.charAt(s.length() - 1) != ']' )
	    {
		errorOffset = 0;
	    }
	    else
	    {
		List<List<Number>> numbers = new ArrayList<List<Number>>(10);

		/** current list of Numbers representing the current row */
		List<Number> currentRow = null;

		boolean inRow = false;

		String rows = s.substring(1, s.length() - 1);
		
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < rows.length(); i++)
		{
		    char c = rows.charAt(i);
		    
		    if ( inRow )
		    {
			if ( c == ']' || c == ',' )
			{   
			    /** flush buffer to currentRow after parsing double */
			    double n = 0;
			    
			    if ( buffer.length() > 0 )
			    {
				n = Double.parseDouble(buffer.toString());
				if ( currentRow == null )
				{
				    currentRow = new ArrayList<Number>(10);
				}
				currentRow.add(n);
			    }
			    
			    /* clear buffer */
			    buffer.delete(0, buffer.length());
			    
			    if ( c == ']' )
			    {
				/** add the current row to the list of rows */
				if ( currentRow != null && currentRow.size() > 0 )
				{
				    numbers.add(currentRow);
				    currentRow = null;
				}
				else
				{
				    errorOffset = i + 1;
				    break;
				}
				inRow = false;
			    }
			}
			else
			{
			    /** add to buffer */
			    buffer.append(c);
			}
		    }
		    else
		    {
			if ( c != '[' )
			{
			    errorOffset = i + 1;
			    break;
			}
			else
			{
			    inRow = true;
			}
		    }
		}
		
		if ( numbers != null && numbers.size() > 0 )
		{
		    /* check that all list of number have the same size */
		    // -1 not initialized
		    // -2 error occur parsing invalid
		    int size = -1;
		    
		    for(int i = 0; i < numbers.size() && size >= -1; i++)
		    {
			List<Number> list = numbers.get(i);
			
			int currentSize = (list == null ? 0 : list.size());
			
			if ( size == -1 )
			{
			    size = currentSize;
			}
			else
			{   
			    if ( currentSize != size )
			    {
				size = -2;
			    }
			}
		    }
		    
		    if ( size > 0 )
		    {
			result = new Matrix(numbers.size(), size);
			
			for(int i = 0; i < numbers.size(); i++)
			{
			    List<Number> list = numbers.get(i);
			    
			    for(int j = 0; j < size; j++)
			    {
				result.set(i, j, list.get(j));
			    }
			}
		    }
		}
	    }
	}
	
	if ( result == null )
	{
	    throw new ParseException("could not create a matrix according to '" + s + "'", errorOffset);
	}
	
	return result;
    }
}
